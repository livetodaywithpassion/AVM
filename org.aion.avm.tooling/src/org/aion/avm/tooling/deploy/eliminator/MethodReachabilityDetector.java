package org.aion.avm.tooling.deploy.eliminator;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.objectweb.asm.Opcodes;

public class MethodReachabilityDetector {

    private Map<String, ClassInfo> classInfoMap;
    private Queue<MethodInfo> methodQueue;

    public Map<String, ClassInfo> optimize(String mainClassName, Map<String, byte[]> classMap) throws Exception {

        // Use the JarDependencyCollector to build the classInfos we need

        JarDependencyCollector jarReader = new JarDependencyCollector(classMap);
        classInfoMap = jarReader.getClassInfoMap();

        // Starting with Main::main(), assess reachability
        ClassInfo mainClassInfo = classInfoMap.get(mainClassName);
        if (null == mainClassInfo) {
            throw new Exception("Main class info not found for class " + mainClassName);
        }
        MethodInfo mainMethodInfo = mainClassInfo.getMethodMap().get("main()[B");
        if (null == mainMethodInfo) {
            throw new Exception("Main method info not found!");
        }

        methodQueue = new LinkedList<>();
        traverse(mainMethodInfo);

        return classInfoMap;
    }

    private void traverse(MethodInfo mainMethodInfo)
        throws Exception {

        mainMethodInfo.setReachable();

        methodQueue.add(mainMethodInfo);

        while (!methodQueue.isEmpty()) {
            MethodInfo methodInfo = methodQueue.remove();
            if (!methodInfo.isReachable()) {
                throw new Exception("This method should have been marked as reachable!");
            }
            for (MethodInvocation invocation : methodInfo.getMethodInvocations()) {
                ClassInfo ownerClass = classInfoMap.get(invocation.getClassName());
                // if this class isn't in the classInfoMap, it's not part of usercode, so just proceed
                if (null != ownerClass) {
                    MethodInfo calledMethod = ownerClass.getMethodMap()
                        .get(invocation.getMethodIdentifier());
                    switch (invocation.getInvocationOpcode()) {
                        case Opcodes.INVOKESPECIAL:
                        case Opcodes.INVOKESTATIC:
                        case Opcodes.INVOKEDYNAMIC:
                            // this is the easy case: we just mark the methodInfo as reachable and enqueue it
                            enqueue(calledMethod);
                            break;
                        case Opcodes.INVOKEVIRTUAL:
                        case Opcodes.INVOKEINTERFACE:
                            if (null == calledMethod) {
                                // if this method is not implemented in this class, we need to set it as reachable in its superclass
                                ownerClass.getSuperclass()
                                    .setMethodAsReachable(invocation.getMethodIdentifier());
                            } else {
                                // we need to mark this method as reachable in this class, as well as all its children (and enqueue them all)
                                enqueueSelfAndChildren(ownerClass, calledMethod);
                            }
                            break;
                        default:
//                        throw new Exception("This is not an invoke method opcode");
                    }
                }
            }
        }
    }

    private void enqueueSelfAndChildren(ClassInfo classInfo, MethodInfo methodInfo) {
        enqueue(methodInfo);
        for (ClassInfo childClassInfo: classInfo.getChildren()) {
            MethodInfo childMethodInfo = childClassInfo.getMethodMap().get(methodInfo.getMethodIdentifier());
            if (null != childMethodInfo) {
                enqueue(childMethodInfo);
            }
        }
    }

    private void enqueue(MethodInfo methodInfo) {
        methodInfo.setReachable();
        methodQueue.add(methodInfo);
    }



    public Map<String, ClassInfo> getClassInfoMap() {
        return classInfoMap;
    }
}

