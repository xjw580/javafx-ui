package club.xiaojiawei.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 系统工具类
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/1 10:34
 */
@Slf4j
public class SystemUtil {


    /**
     * 获取指定包名下的所有类名
     * @param packageName 例：club.xiaojiawei.controls.ico
     * @return
     */
    public static List<String> getClassesInPackage(String packageName) {
        List<String> classNames = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                File directory = new File(resources.nextElement().getFile());
                if (directory.exists()) {
                    classNames.addAll(findClasses(packageName, directory));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classNames;
    }

    private static List<String> findClasses(String packageName, File directory) {
        List<String> classNames = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classNames.addAll(findClasses(packageName + "." + file.getName(), file));
                } else if (file.getName().endsWith(".class")) {
                    classNames.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                }
            }
        }
        return classNames;
    }
}
