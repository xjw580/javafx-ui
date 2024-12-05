package club.xiaojiawei.bean;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.function.Predicate;

/**
 * @author 肖嘉威
 * @date 2024/12/5 14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileChooserFilter {

    public static final FileChooserFilter FILE_CHOOSER_FILTER = new FileChooserFilter(null, File::isFile);

    public static final FileChooserFilter DIR_CHOOSER_FILTER = new FileChooserFilter(File::isDirectory, File::isDirectory);

    @Nullable
    private Predicate<@NotNull File> showFilter;

    @Nullable
    private Predicate<@NotNull File> resultFilter;

}
