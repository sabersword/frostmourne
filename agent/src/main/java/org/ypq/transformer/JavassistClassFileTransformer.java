package org.ypq.transformer;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class JavassistClassFileTransformer implements ClassFileTransformer {


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        CtClass ctClass = null;
        byte[] returnByte = null;

        try {
            ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
            for(CtBehavior method : ctClass.getDeclaredBehaviors()) {
                if (method.getLongName().contains("org.ypq.controller.TestController.test")) {
                    System.out.println("成功找到匹配的类和方法, 准备织入" + className + "  LongName :   " + method.getLongName());
                    CtClass throwableClass = ClassPool.getDefault().get("java.lang.Throwable");
                    method.addCatch("{ System.out.println(\"捕获到异常,重新抛出:\" + $e); throw $e; }", throwableClass);
                    method.insertBefore("{ System.out.println(\"age:\"+$1); System.out.println(\"string:\"+$2);}");
                }
            }
            returnByte = ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctClass != null) {
                ctClass.detach();
            }
        }
        return returnByte;
    }
}
