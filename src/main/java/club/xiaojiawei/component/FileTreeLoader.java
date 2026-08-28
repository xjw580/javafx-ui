package club.xiaojiawei.component;

import club.xiaojiawei.config.JavaFXUIThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Slf4j
final class FileTreeLoader {

    static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    enum Outcome {
        SUCCESS,
        IO_FAILURE,
        TIMEOUT
    }

    record Entry(File file, boolean directory, boolean hidden, String name) {
        Entry {
            Objects.requireNonNull(file);
            Objects.requireNonNull(name);
        }
    }

    record Result(Outcome outcome, List<Entry> entries, Throwable error) {
        Result {
            Objects.requireNonNull(outcome);
            entries = List.copyOf(entries);
        }

        static Result success(List<Entry> entries) {
            return new Result(Outcome.SUCCESS, entries, null);
        }

        static Result ioFailure(Throwable error) {
            return new Result(Outcome.IO_FAILURE, List.of(), Objects.requireNonNull(error));
        }

        static Result timeout() {
            return new Result(Outcome.TIMEOUT, List.of(), null);
        }
    }

    static final class RequestToken {
        private final AtomicBoolean valid = new AtomicBoolean(true);

        private RequestToken() {
        }

        boolean isValid() {
            return valid.get();
        }

        void invalidate() {
            valid.set(false);
        }
    }

    static final class Request {
        private final RequestToken token;
        private final CompletableFuture<Result> completion;
        private final AtomicReference<Future<?>> worker = new AtomicReference<>();
        private final AtomicReference<Future<?>> timeoutWatcher = new AtomicReference<>();

        private Request(RequestToken token, CompletableFuture<Result> completion) {
            this.token = token;
            this.completion = completion;
        }

        RequestToken token() {
            return token;
        }

        CompletableFuture<Result> completion() {
            return completion;
        }

        void cancel() {
            token.invalidate();
            completion.cancel(false);
            cancel(worker.get());
            cancel(timeoutWatcher.get());
        }

        private static void cancel(Future<?> future) {
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    @FunctionalInterface
    interface DirectoryReader {
        List<Entry> read(File directory, Predicate<File> filter) throws IOException;
    }

    private final DirectoryReader directoryReader;
    private final long timeoutNanos;

    FileTreeLoader() {
        this(FileTreeLoader::readDirectory, DEFAULT_TIMEOUT);
    }

    FileTreeLoader(DirectoryReader directoryReader, Duration timeout) {
        this.directoryReader = Objects.requireNonNull(directoryReader);
        Objects.requireNonNull(timeout);
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        timeoutNanos = timeout.toNanos();
    }

    Request load(File directory, Predicate<File> filter) {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(filter);

        long submittedAt = System.nanoTime();
        RequestToken token = new RequestToken();
        CompletableFuture<Result> completion = new CompletableFuture<>();
        Request request = new Request(token, completion);

        Future<?> worker = JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
            Result result;
            try {
                result = Result.success(directoryReader.read(directory, filter));
            } catch (IOException | RuntimeException e) {
                result = Result.ioFailure(e);
            }
            if (token.isValid() && completion.complete(result)) {
                Request.cancel(request.timeoutWatcher.get());
            }
        });
        request.worker.set(worker);

        Future<?> timeoutWatcher = JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
            long remainingNanos = timeoutNanos - (System.nanoTime() - submittedAt);
            try {
                if (remainingNanos > 0) {
                    TimeUnit.NANOSECONDS.sleep(remainingNanos);
                }
                if (token.isValid() && completion.complete(Result.timeout())) {
                    Request.cancel(request.worker.get());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        request.timeoutWatcher.set(timeoutWatcher);
        if (completion.isDone()) {
            timeoutWatcher.cancel(true);
        }
        return request;
    }

    private static List<Entry> readDirectory(File directory, Predicate<File> filter) throws IOException {
        List<Entry> entries = new ArrayList<>();
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(directory.toPath())) {
            try {
                for (Path path : paths) {
                    try {
                        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                        File file = path.toFile();
                        if (filter.test(file)) {
                            String name = file.getName();
                            entries.add(new Entry(file, attributes.isDirectory(), Files.isHidden(path),
                                    name.isBlank() ? file.getAbsolutePath() : name));
                        }
                    } catch (IOException | RuntimeException e) {
                        log.warn("跳过加载失败的目录子项：{}", path, e);
                    }
                }
            } catch (DirectoryIteratorException e) {
                throw e.getCause();
            }
        } catch (SecurityException e) {
            throw new IOException("没有权限读取目录 " + directory, e);
        }
        return entries;
    }
}
