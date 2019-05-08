package org.aion.avm.tooling.deploy.eliminator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassDependencyVisitor extends ClassVisitor {

    private String className;
    private String superName;
    private String[] interfaces;
    private List<MethodDependencyVisitor> methodVisitors = new ArrayList<>();
    private Map<String, MethodInfo> methodMap = new HashMap<>();

    public ClassDependencyVisitor() {
        super(Opcodes.ASM6);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
        String[] interfaces) {
        this.className = name;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
        String[] exceptions) {
        MethodDependencyVisitor mv = new MethodDependencyVisitor(name, descriptor,
            super.visitMethod(access, name, descriptor, signature, exceptions));
        methodVisitors.add(mv);
        return mv;

    }

    @Override
    public void visitEnd() {
        for (MethodDependencyVisitor methodVisitor : methodVisitors) {
            MethodInfo methodInfo = new MethodInfo(methodVisitor.getMethodIdentifier());
            methodInfo.setMethodInvocations(methodVisitor.getMethodsCalled());
            methodMap.put(methodVisitor.getMethodIdentifier(), methodInfo);
        }
    }

    public String getClassName() {
        return className;
    }

    public String getSuperName() {
        return superName;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public Map<String, MethodInfo> getMethodMap() {
        return methodMap;
    }
}
