package org.ypq;

import java.io.RandomAccessFile;
import java.lang.instrument.*;
import java.security.ProtectionDomain;

public class Agent {

    private static final String ENHANCE_CLASS = "org.ypq.controller.TestController";

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("Args is " + args);

        if ("weave".equals(args)) {
            enhance(new JavassistClassFileTransformer(), instrumentation);
            System.out.println("成功增强类:" + ENHANCE_CLASS);
        } else if ("reset".equals(args)) {
            ClassFileTransformer transformer = (ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    -> {return null;};
            enhance(transformer, instrumentation);
            System.out.println("成功重置类:" + ENHANCE_CLASS);
        } else if ("redefine".equals(args)) {
            Class<?> clazz = findLoadedClass(instrumentation, ENHANCE_CLASS);
            RandomAccessFile f = null;
            try {
                f = new RandomAccessFile("TestController.class", "r");
                final byte[] bytes = new byte[(int) f.length()];
                f.readFully(bytes);
                instrumentation.redefineClasses(new ClassDefinition(clazz, bytes));
                System.out.println("成功重定义类:" + ENHANCE_CLASS);
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

}
