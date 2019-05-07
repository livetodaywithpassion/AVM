package org.aion.avm.tooling.deploy.eliminator;

public class MethodInvocation {
    private String className;
    private String methodName;
    private int invocationOpcode;

    public MethodInvocation(String className, String methodName, int invocationOpcode) {
        this.className = className;
        this.methodName = methodName;
        this.invocationOpcode = invocationOpcode;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getInvocationOpcode() {
        return invocationOpcode;
    }
}
