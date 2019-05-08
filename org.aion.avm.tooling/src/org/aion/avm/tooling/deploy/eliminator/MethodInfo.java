package org.aion.avm.tooling.deploy.eliminator;

import java.util.HashSet;
import java.util.Set;

public class MethodInfo {

    private String methodIdentifier;
    private boolean reachable = false;
    private Set<MethodInvocation> methodInvocations = new HashSet<>();

    public MethodInfo(String methodIdentifier) {
        this.methodIdentifier = methodIdentifier;
    }

    public void setMethodInvocations(
        Set<MethodInvocation> methodInvocations) {
        this.methodInvocations = methodInvocations;
    }

    public Set<MethodInvocation> getMethodInvocations() {
        return methodInvocations;
    }

    public String getMethodIdentifier() {
        return methodIdentifier;
    }

    public void setMethodIdentifier(String methodIdentifier) {
        this.methodIdentifier = methodIdentifier;
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
