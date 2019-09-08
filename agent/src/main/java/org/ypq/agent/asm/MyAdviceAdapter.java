package org.ypq.agent.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.PrintStream;

public class MyAdviceAdapter extends AdviceAdapter {

    private static final String SPY_TYPE = "Ljava/ypq/spy/Spy;";
    private static final String BEFORE_METHOD = "ON_BEFORE_METHOD";
    private static final String RETURN_METHOD = "ON_RETURN_METHOD";
    private static final String THROW_METHOD = "ON_THROW_METHOD";

    private Label beginLabel = new Label();
    private Label endLabel = new Label();

    protected MyAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    /**
     * 1. 准备Spy#ON_METHOD_BEGIN
     * 2. 准备invoke的第一个参数Object = null
     * 3. 准备invoke数组
     * 4. 调用Spy#ON_METHOD_BEGIN#invoke
     * 5. 弹射出invoke的返回值
     * 以上所有组成一句Spy.ON_BEFORE_METHOD.invoke(null, {this, {方法参数}})
     */
    @Override
    protected void onMethodEnter() {
        getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
        visitLdcInsn("Hello!");
        invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println(String)"));

        // 准备Spy#ON_METHOD_BEGIN
        getStatic(Type.getType(SPY_TYPE), BEFORE_METHOD, Type.getType(java.lang.reflect.Method.class));
        // invoke的第一个参数Object = null
        push((Type) null);
        push(2);
        newArray(Type.getType(Object.class));
        // 准备invoke数组 第0位 this
        dup();
        push(0);
        // 本地变量表第0个是this指针
        mv.visitVarInsn(ALOAD, 0);
        arrayStore(Type.getType(Object.class));
        // 准备invoke数组 第1位 方法参数数组
        dup();
        push(1);
        loadArgArray();
        arrayStore(Type.getType(Object.class));
        invokeVirtual(Type.getType(java.lang.reflect.Method.class), Method.getMethod("Object invoke(Object, Object[])"));
        // 弹射出invoke方法返回值
        pop();
        // 标记try块开始的地方
        mark(beginLabel);

    }

    /**
     * 1. 复制一份返回值
     * 2. 准备Spy#ON_RETURN_METHOD
     * 3. 准备invoke的第一个参数Object = null
     * 4. 准备invoke的数组,该数组只有一个值,就是返回值
     * 5. 弹射出invoke方法返回值
     * 以上组成一句Spy.ON_RETURN_METHOD.invoke(null, {返回值}})
     */
    @Override
    protected void onMethodExit(int opcode) {
        // 如果是异常, 由visitMaxs产生catch块
        if (opcode == ATHROW) {
            return;
        }
        // 其余情况, 复制一份返回值
        if (opcode == RETURN) {
            push((Type)null);
        } else if (opcode == ARETURN) {
            dup();
        } else {
            if (opcode == LRETURN || opcode == DRETURN) {
                dup2();
            } else {
                dup();
            }
            box(Type.getReturnType(this.methodDesc));
        }
        // 准备Spy#ON_RETURN_METHOD
        getStatic(Type.getType(SPY_TYPE), RETURN_METHOD, Type.getType(java.lang.reflect.Method.class));
        // 准备invoke的第一个参数Object = null
        push((Type) null);
        // 准备invoke的数组,该数组只有一个值,就是返回值
        loadReturnArgs();
        invokeVirtual(Type.getType(java.lang.reflect.Method.class), Method.getMethod("Object invoke(Object, Object[])"));
        // 弹射出invoke方法返回值
        pop();
    }

    /**
     * 1. 复制一份异常
     * 2. 准备Spy#ON_THROW_METHOD
     * 3. 准备invoke的第一个参数Object = null
     * 4. 准备invoke的数组,该数组只有一个值,就是抛出异常
     * 5. 弹射出invoke方法返回值
     * 以上组成一句Spy.ON_THROW_METHOD.invoke(null, {抛出的异常}})
     */
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        // 标记try块结束
        mark(endLabel);
        // 开始编写catch块内容
        visitTryCatchBlock(beginLabel, endLabel, mark(), Type.getType(Throwable.class).getInternalName());
        // 此时栈顶是异常, 复制一份异常
        dup();
        // 准备Spy#ON_THROWS_METHOD
        getStatic(Type.getType(SPY_TYPE), THROW_METHOD, Type.getType(java.lang.reflect.Method.class));
        // 准备invoke的第一个参数Object = null
        push((Type) null);
        // 准备invoke的数组,该数组只有一个值,就是抛出的异常
        loadThrowArgs();
        invokeVirtual(Type.getType(java.lang.reflect.Method.class), Method.getMethod("Object invoke(Object, Object[])"));
        // 弹射出invoke方法返回值
        pop();
        // 重新抛出异常
        throwException();

        super.visitMaxs(maxStack, maxLocals);
    }

    private void loadReturnArgs() {
        dup2X1();
        pop2();
        push(1);
        newArray(Type.getType(Object.class));
        dup();
        dup2X1();
        pop2();
        push(0);
        swap();
        arrayStore(Type.getType(Object.class));
    }

    private void loadThrowArgs() {
        dup2X1();
        pop2();
        push(1);
        newArray(Type.getType(Object.class));
        dup();
        dup2X1();
        pop2();
        push(0);
        swap();
        arrayStore(Type.getType(Throwable.class));
    }

}
