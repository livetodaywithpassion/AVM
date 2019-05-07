package org.aion.avm.tooling.deploy.eliminator;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodDependencyVisitor extends MethodVisitor {

    private String methodName;
    private Set<MethodInvocation> methodsCalled = new HashSet<>();

    public MethodDependencyVisitor(String methodName, MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
        this.methodName = methodName;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        methodsCalled.add(new MethodInvocation(owner, name, opcode));
    }

    public Set<MethodInvocation> getMethodsCalled() {
        return methodsCalled;
    }

    public String getMethodName() {
        return methodName;
    }
}
