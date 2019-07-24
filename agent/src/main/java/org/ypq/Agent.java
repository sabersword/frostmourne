package org.ypq;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("Args is " + args);
        System.out.println("begin to retransformClasses");

        try {
            instrumentation.addTransformer(new JavassistClassFileTransformer(), true);
            Class[] classes = instrumentation.getAllLoadedClasses();
            for (Class clazz : classes) {
                if (clazz.getName().contains("org.ypq.controller.TestController")) {
                    System.out.println("class matched " + clazz.getName());
                    instrumentation.retransformClasses(clazz);
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }

    }


}
