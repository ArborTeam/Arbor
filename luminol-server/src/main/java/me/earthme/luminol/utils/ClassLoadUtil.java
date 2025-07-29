package me.earthme.luminol.utils;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassLoadUtil {
    private static Logger LOGGER = LogUtils.getClassLogger();

    public static List<Class<?>> getClassesInPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = ClassLoadUtil.class.getClassLoader();
        URL jarUrl = classLoader.getResource(packageName.replace('.', '/'));
        if (jarUrl == null) {
            throw new IllegalArgumentException("Package not found: " + packageName);
        }
        String jarPath = jarUrl.getPath().replace("!/" + packageName.replace('.', '/'), "");
        if (jarPath.startsWith("file:")) {
            jarPath = jarPath.substring(5);
            jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
        }
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String className = entry.getName();
                try {
                    if (className.endsWith(".class") && className.startsWith(packageName.replace('.', '/'))) {
                        className = className.substring(0, className.length() - 6).replace('/', '.');
                        Class<?> clazz = classLoader.loadClass(className);
                        classes.add(clazz);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Error loading class: {}", className);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error finding classes in package: {}", packageName);
        }

        return classes;
    }

    public static <T> List<T> loadClasses(String dir, Class<T> clazz) {
        List<Class<?>> list = getClassesInPackage(dir);
        List<T> finalList = new ArrayList<>();
        for (Class<?> clazz1 : list) {
            try {
                if (clazz.isAssignableFrom(clazz1)) {
                    T instance = clazz.cast(clazz1.getDeclaredConstructor().newInstance());
                    finalList.add(instance);
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to transform class: {}", clazz1.getName());
            }
        }
        return finalList;
    }
}