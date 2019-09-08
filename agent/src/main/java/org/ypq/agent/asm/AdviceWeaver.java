package org.ypq.agent.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collection;

import static java.lang.String.format;

public class AdviceWeaver extends ClassVisitor implements Opcodes {

    private String enhanceMethodName;
    private static long beginTimestamp;

    public AdviceWeaver(ClassVisitor cv, String enhanceMethodName) {
        super(Opcodes.ASM7, cv);
        this.enhanceMethodName = enhanceMethodName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            // 找到要增强的方法
            if (name.equals(enhanceMethodName)) {
                // 使用自定义 MethodVisitor，实际改写方法内容
                mv = new MyAdviceAdapter(Opcodes.ASM7, mv, access, name, descriptor);
            }
        }
        return mv;
    }

    public static void onBeforeMethod(Object target, Object[] args) {
        beginTimestamp = System.nanoTime();
        System.out.println("增强的对象" + target);
        for (int i = 0; i < args.length; i++) {
            System.out.println("第" + i + "个参数值是:" + convertObj(args[i]));
        }
    }

    public static void onReturnMethod(Object result) {
        long endTimestamp = System.nanoTime();
        System.out.println("该方法正常返回,耗时:" + (endTimestamp - beginTimestamp) / 1000000 + "ms");
        System.out.println("返回值是:" + convertObj(result));
    }

    public static void onThrowMethod(Throwable throwable) {
        long endTimestamp = System.nanoTime();
        System.out.println("该方法抛出异常,耗时:" + (endTimestamp - beginTimestamp) / 1000000 + "ms");
        System.out.println("抛出异常是:" + convertObj(throwable));
    }

    private static String convertObj(Object obj) {
        final Class<?> clazz = obj.getClass();
        final String className = clazz.getSimpleName();
        StringBuffer sb = new StringBuffer();

        // 7种基础类型,直接输出@类型[值]
        if (Integer.class.isInstance(obj)
                || Long.class.isInstance(obj)
                || Float.class.isInstance(obj)
                || Double.class.isInstance(obj)
                || Short.class.isInstance(obj)
                || Byte.class.isInstance(obj)
                || Boolean.class.isInstance(obj)) {
            sb.append(format("@%s[%s]", className, obj));
        } else if (String.class.isInstance(obj)) {
            sb.append(obj.toString());
        } else if (Collection.class.isInstance(obj)) {
            final Collection<Object> collection = (Collection<Object>) obj;
            sb.append("集合内的值分别是:");
            for (Object o : collection) {
                sb.append(o.toString() + ", ");
            }
        } else if (Throwable.class.isInstance(obj)) {
            final Throwable throwable = (Throwable) obj;
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            sb.append(sw.toString());
        } else {
            final Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    sb.append("属性" + field.getName() + ":" + field.get(obj) + " ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
