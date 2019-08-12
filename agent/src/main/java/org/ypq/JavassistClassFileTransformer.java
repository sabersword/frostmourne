package org.ypq;

import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
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
                    method.insertBefore("{ System.out.print(\"age:\"+$1); System.out.println(\"string:\"+$2);}");
                    method.insertAfter("{ System.out.print(\"string:\"+$2); System.out.println(\"age:\"+$1);}");
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
