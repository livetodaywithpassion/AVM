package org.aion.avm.tooling.deploy.eliminator;

import java.util.HashSet;
import java.util.Set;

public class MethodInfo {

    private String methodName;
    private boolean reachable = false;
    private Set<MethodInvocation> methodInvocations = new HashSet<>();

    public MethodInfo(String methodName) {
        this.methodName = methodName;
    }

    public void setMethodInvocations(
        Set<MethodInvocation> methodInvocations) {
        this.methodInvocations = methodInvocations;
    }

    public Set<MethodInvocation> getMethodInvocations() {
        return methodInvocations;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable() {
        this.reachable = true;
    }

    public void setUnreachable() {
        this.reachable = false;
    }
}
