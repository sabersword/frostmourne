package org.ypq;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;

public class Agent {

    private static final String ENHANCE_CLASS = "org.ypq.controller.TestController";

    private static volatile ClassLoader frostmourneClassLoader;

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        System.out.println("Args is " + args);

        File agentJar = new File("agent-1.0-SNAPSHOT.jar");
        if (frostmourneClassLoader == null) {
            frostmourneClassLoader = new FrostmourneClassLoader(new URL[]{agentJar.toURI().toURL()});
        }

        if ("weave".equals(args)) {
            // 相当于new JavassistClassFileTransformer()
            ClassFileTransformer transformer = (ClassFileTransformer) frostmourneClassLoader.loadClass("org.ypq.transformer.JavassistClassFileTransformer").newInstance();
            enhance(transformer, instrumentation);
            System.out.println("完成增强类:" + ENHANCE_CLASS);
        } else if ("reset".equals(args)) {
            // 相当于new ResetClassFileTransformer()
            ClassFileTransformer transformer = (ClassFileTransformer) frostmourneClassLoader.loadClass("org.ypq.transformer.ResetClassFileTransformer").newInstance();
            enhance(transformer, instrumentation);
            System.out.println("完成重置类:" + ENHANCE_CLASS);
        } else if ("redefine".equals(args)) {
            Class<?> clazz = findLoadedClass(instrumentation, ENHANCE_CLASS);
            RandomAccessFile f = null;
            try {
                f = new RandomAccessFile("TestController.class", "r");
                final byte[] bytes = new byte[(int) f.length()];
                f.readFully(bytes);
                instrumentation.redefineClasses(new ClassDefinition(clazz, bytes));
                System.out.println("完成重定义类:" + ENHANCE_CLASS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("参数有误" + args);
        }
    }

    private static void enhance(ClassFileTransformer transformer, Instrumentation instrumentation) {
        try {
            instrumentation.addTransformer(transformer, true);
            Class<?> clazz = findLoadedClass(instrumentation, ENHANCE_CLASS);
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

    private static Class<?> findLoadedClass(Instrumentation instrumentation, String className) {
        Class<?>[] classes = instrumentation.getAllLoadedClasses();
        for (Class<?> clazz : classes) {
            // 只增强找到的第一个类
            if (clazz.getName().contains(className)) {
                return clazz;
            }
        }
        throw new RuntimeException("找不到类:" + ENHANCE_CLASS);
    }

    public static void main(String[] args) {

    }

}
