package org.ypq;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class Agent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("Args is " + args);

        if ("weave".equals(args)) {
//            ClassFileTransformer transformer = new JavassistClassFileTransformer();
            enhance(new JavassistClassFileTransformer(), instrumentation);
        } else if ("reset".equals(args)) {
            ClassFileTransformer transformer = (ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    -> {return null;};
            enhance(transformer, instrumentation);
        } else {
            System.out.println("参数有误" + args);
        }
    }

    private static void enhance(ClassFileTransformer transformer, Instrumentation instrumentation) {
        try {
            instrumentation.addTransformer(transformer, true);
            Class[] classes = instrumentation.getAllLoadedClasses();
            for (Class clazz : classes) {
                // 只增强Controller
                if (clazz.getName().contains("org.ypq.controller.TestController")) {
                    instrumentation.retransformClasses(clazz);
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }

}
