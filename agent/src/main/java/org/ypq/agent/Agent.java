package org.ypq.agent;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.jar.JarFile;

public class Agent {

    private static volatile ClassLoader frostmourneClassLoader;
    private static String enhanceClass;
    private static String enhanceMethod;

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        System.out.println("Args is " + args);
        String[] argArray = args.split(" ");
        if (argArray.length != 3) {
            System.out.println("参数有误, 请输入{命令 类名 方法名}");
            return;
        }
        String instruction = argArray[0];
        enhanceClass = argArray[1];
        enhanceMethod = argArray[2];

        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File("spy-1.0-SNAPSHOT.jar")));

        File agentJar = new File("agent-1.0-SNAPSHOT.jar");
        if (frostmourneClassLoader == null) {
//            ClassLoader childClassLoaders = null;
//            for (Class loadedClass : instrumentation.getAllLoadedClasses()) {
//                ClassLoader classLoader = loadedClass.getClassLoader();
//                ClassLoader tempClassLoader = classLoader;
//                while (tempClassLoader != null) {
//                    if (tempClassLoader.getParent() == childClassLoaders) {
//                        childClassLoaders = classLoader;
//                        break;
//                    }
//                    tempClassLoader = tempClassLoader.getParent();
//                }
//            }
//            frostmourneClassLoader = new FrostmourneClassLoader(new URL[]{agentJar.toURI().toURL()}, childClassLoaders);
            frostmourneClassLoader = new FrostmourneClassLoader(new URL[]{agentJar.toURI().toURL()});
        }
//        Thread.currentThread().setContextClassLoader(frostmourneClassLoader);

        // 给Spy注入钩子方法
        Class<?> adviceWeaverClass = frostmourneClassLoader.loadClass("org.ypq.agent.asm.AdviceWeaver");
        Method onBeforeMethod = adviceWeaverClass.getMethod("onBeforeMethod", Object.class, Object[].class);
        Method onReturnMethod = adviceWeaverClass.getMethod("onReturnMethod", Object.class);
        Method onThrowMethod = adviceWeaverClass.getMethod("onThrowMethod", Throwable.class);
        Class<?> spyClass = frostmourneClassLoader.loadClass("java.ypq.spy.Spy");
        Method initMethod = spyClass.getMethod("init", Method.class, Method.class, Method.class);
        initMethod.invoke(null, onBeforeMethod, onReturnMethod, onThrowMethod);

        if ("weave".equals(instruction)) {
            // 相当于new AsmClassFileTransformer()
            ClassFileTransformer javassistTransformer = (ClassFileTransformer) frostmourneClassLoader.loadClass("org.ypq.agent.transformer.AsmClassFileTransformer").getConstructor(String.class).newInstance(enhanceMethod);
            ClassFileTransformer dumpTransformer = (ClassFileTransformer) frostmourneClassLoader.loadClass("org.ypq.agent.transformer.DumpClassFileTransformer").newInstance();
            enhance(enhanceClass, instrumentation, javassistTransformer, dumpTransformer);
            System.out.println("完成增强类:" + enhanceClass);
        } else if ("reset".equals(instruction)) {
            // 相当于new ResetClassFileTransformer()
            ClassFileTransformer transformer = (ClassFileTransformer) frostmourneClassLoader.loadClass("org.ypq.agent.transformer.ResetClassFileTransformer").newInstance();
            enhance(enhanceClass, instrumentation, transformer);
            System.out.println("完成重置类:" + enhanceClass);
        } else if ("redefine".equals(instruction)) {
            Class<?> clazz = findLoadedClass(instrumentation, enhanceClass);
            RandomAccessFile f = null;
            try {
                f = new RandomAccessFile("TestController.class", "r");
                final byte[] bytes = new byte[(int) f.length()];
                f.readFully(bytes);
                instrumentation.redefineClasses(new ClassDefinition(clazz, bytes));
                System.out.println("完成重定义类:" + enhanceClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("参数有误" + args);
        }
    }

    private static void enhance(String enhanceClass, Instrumentation instrumentation, ClassFileTransformer... transformers) {
        try {
            for (ClassFileTransformer transformer : transformers) {
                instrumentation.addTransformer(transformer, true);
            }
            Class<?> clazz = findLoadedClass(instrumentation, enhanceClass);
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            for (ClassFileTransformer transformer : transformers) {
                instrumentation.removeTransformer(transformer);
            }
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
        throw new RuntimeException("找不到类:" + enhanceClass);
    }

    public static void main(String[] args) {

    }

}
