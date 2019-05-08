package org.aion.avm.tooling.deploy.eliminator;

import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodUnreachabilityVisitor extends MethodVisitor {

    private String methodIdentifier;
    private Set<MethodInvocation> methodsCalled = new HashSet<>();

    public MethodUnreachabilityVisitor(String methodName, String methodDescriptor, MethodVisitor mv) {
        super(Opcodes.ASM6, mv);
        // the concatenation of name + descriptor is a unique identifier for every method in a class
        this.methodIdentifier = methodName + methodDescriptor;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        methodsCalled.add(new MethodInvocation(owner, name + desc, opcode));
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        //TODO: Confirm assumptions like length >= 2, [1] is the Handle, etc.
        Handle ownerHandle = (Handle) bsmArgs[1];
        methodsCalled.add(new MethodInvocation(ownerHandle.getOwner(),
            ownerHandle.getName() + ownerHandle.getDesc(), Opcodes.INVOKEDYNAMIC));
    }

    public Set<MethodInvocation> getMethodsCalled() {
        return methodsCalled;
    }

    public String getMethodIdentifier() {
        return methodIdentifier;
    }
}
