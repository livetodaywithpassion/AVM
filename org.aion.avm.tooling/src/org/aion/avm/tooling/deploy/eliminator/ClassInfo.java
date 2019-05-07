package org.aion.avm.tooling.deploy.eliminator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassInfo {

    private String className;
    private byte[] classBytes;

    private Map<String, MethodInfo> methodMap = new HashMap<>();

    private Set<ClassInfo> parents = new HashSet<>();
    private Set<ClassInfo> children = new HashSet<>();

    public ClassInfo(String className, byte[] classBytes) {
        this.className = className;
        this.classBytes = classBytes;
    }

    public void addToParents(ClassInfo parent, Set<ClassInfo> ancestors) {
        this.parents.add(parent);
        this.parents.addAll(ancestors);
        for (ClassInfo childClassInfo: children) {
            childClassInfo.addToParents(parent, ancestors);
        }
    }

    public void addToChildren(ClassInfo child, Set<ClassInfo> descendants) {
        this.children.add(child);
        this.children.addAll(descendants);
        for (ClassInfo parentsClassInfo: parents) {
            parentsClassInfo.addToChildren(child, descendants);
        }
    }

    public Set<ClassInfo> getParents() {
        return parents;
    }

    public Set<ClassInfo> getChildren() {
        return children;
    }

    public void setMethodMap(Map<String, MethodInfo> methodDependencies) {
        this.methodMap = methodDependencies;
    }

    public Map<String, MethodInfo> getMethodMap() {
        return methodMap;
    }
}
