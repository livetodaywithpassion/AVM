package org.aion.avm.tooling.deploy.eliminator;

public class MethodInvocation {
    private String className;
    private String methodIdentifier;
    private int invocationOpcode;

    public MethodInvocation(String className, String methodIdentifier, int invocationOpcode) {
        this.className = className;
        this.methodIdentifier = methodIdentifier;
        this.invocationOpcode = invocationOpcode;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodIdentifier() {
        return methodIdentifier;
    }

    public int getInvocationOpcode() {
        return invocationOpcode;
    }
}
