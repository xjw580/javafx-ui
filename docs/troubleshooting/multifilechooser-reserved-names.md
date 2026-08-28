# 目录包含 Windows 保留名称时加载失败

## 已验证的原因

在 Windows、GraalVM JDK 21 环境中，对报告的 EEG2100 目录执行与 `FileTreeLoader` 相同的目录枚举和属性读取：

- 共枚举到 16 个条目，其中 15 个可以正常读取属性。
- 名为 `nul` 的条目在 `Files.readAttributes(path, BasicFileAttributes.class)` 处抛出 `java.nio.file.FileSystemException`，原因为“参数错误”。
- 整轮检查耗时 18 毫秒，失败原因不是目录加载超时。
- 在此次 Java 探针中，给输入目录添加扩展路径前缀后仍出现相同异常。

`NUL` 是 Windows 保留设备名称，不能按普通文件名处理。参见 [Microsoft 文件命名文档](https://learn.microsoft.com/zh-cn/windows/win32/fileio/naming-a-file)。本次未调查该条目的创建来源。

## 为什么整个目录失败

修复前，`FileTreeLoader.readDirectory` 在应用显示过滤器前读取每个子项的属性，仅对 `NoSuchFileException` 跳过并记录警告。这里抛出的是其他 `FileSystemException`，因此加载结果为 `IO_FAILURE`，视图随后显示目录读取失败提示。

是否显示隐藏文件不影响这次属性读取失败。

## 修复后的处理规则

按更新后的要求，单个子项在属性读取、显示过滤或隐藏属性读取时抛出 `IOException` 或 `RuntimeException`，只跳过该项，并记录包含路径和异常堆栈的警告；其他子项继续加载。

目录本身无法打开、枚举过程失败或加载超时仍报告失败。不会修改或删除原始数据。

## 回归验证

- 对实际目录调用 `FileTreeLoader.load`，结果从 `IO_FAILURE`、0 项变为 `SUCCESS`、15 项。
- 对一个正常子项的过滤回调注入 `SecurityException`，其余 14 项仍成功返回。
- 父目录不存在时仍返回 `IO_FAILURE`。
- 打开实际 JavaFX 文件选择器并展开目标目录，确认显示 15 个子项，且不包含 `nul`。
- IDEA 编译和 `FileTreeLoader.java` 检查通过。
