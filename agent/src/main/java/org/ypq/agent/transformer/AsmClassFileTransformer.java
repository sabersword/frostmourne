package org.ypq.agent.transformer;

import javassist.CtClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.ypq.agent.asm.AdviceWeaver;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

public class AsmClassFileTransformer implements ClassFileTransformer {

    private String enhanceMethodName;

    public AsmClassFileTransformer(String enhanceMethodName) {
        this.enhanceMethodName = enhanceMethodName;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        CtClass ctClass = null;
        byte[] returnByte = null;
        try {
//            Class<?> clazz = loader.loadClass("org.ypq.demo.pojo.Company");
//            ClassPool.getDefault().insertClassPath(new ClassClassPath(clazz));
//            Class<?> spyClazz = loader.loadClass("java.ypq.spy.Spy");
//            ClassPool.getDefault().insertClassPath(new ClassClassPath(clazz));
//            ClassPool.getDefault().importPackage("java.ypq.spy");
//            Class.forName("java.ypq.spy.Spy");
//            ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(classfileBuffer));
//            for(CtBehavior method : ctClass.getDeclaredBehaviors()) {
//                if (method.getLongName().contains("org.ypq.demo.controller.TestController.test")) {
//                    System.out.println("成功找到匹配的类和方法, 准备织入" + className + "  LongName :   " + method.getLongName());
//                    CtClass throwableClass = ClassPool.getDefault().get("java.lang.Throwable");
//                    method.addCatch("{ System.out.println(\"捕获到异常,重新抛出:\" + $e); throw $e; }", throwableClass);
//                    method.insertBefore("{ System.out.println(\"age:\"+$1); System.out.println(\"name:\"+$2);}");
//                    method.insertBefore("{ (new java.ypq.spy.Spy()).ON_BEFORE_METHOD.invoke(null, this, new Object[]{$$}); }");
//                }
//            }
//            Method spyInitMethod = spyClazz.getMethod("init", Method.class, Method.class, Method.class);
//            spyInitMethod.invoke(null, AdviceWeaver.class.getMethod("methodOnBegin", Object.class, Object[].class), null, null);
//            returnByte = ctClass.toBytecode();

            ClassReader cr = new ClassReader(classfileBuffer);

            // 字节码增强
            final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES | COMPUTE_MAXS);

            cr.accept(new AdviceWeaver(cw, enhanceMethodName), ClassReader.EXPAND_FRAMES);

            returnByte = cw.toByteArray();

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
