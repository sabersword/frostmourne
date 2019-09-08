package org.ypq.agent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义类加载器,加载agent的jar,避免污染attach的应用
 * 对于sun.*和java.*还是交由Bootstrap加载,其余先本类加载,找不到则委托父类
 */
public class FrostmourneClassLoader extends URLClassLoader {

    public FrostmourneClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
    }

    public FrostmourneClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }
}
