package org.aion.avm.tooling.deploy.eliminator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.objectweb.asm.ClassReader;

public class JarDependencyCollector {

    private List<ClassDependencyVisitor> classVisitors = new ArrayList<>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<>();

    public JarDependencyCollector(Map<String, byte[]> classMap) {

        populateClassInfoMap();

        for (byte[] clazzBytes : classMap.values()) {
            visitClass(clazzBytes);
        }

        setParentsAndChildren();
    }

    // Should only be called once per class
    private void visitClass(byte[] classBytes) {

        ClassReader reader = new ClassReader(classBytes);

        ClassDependencyVisitor classVisitor = new ClassDependencyVisitor();
        classVisitors.add(classVisitor);
        reader.accept(classVisitor, 0);

        String className = classVisitor.getClassName();
        ClassInfo classInfo = new ClassInfo(className);
        classInfo.setMethodMap(classVisitor.getMethodMap());
        classInfoMap.put(className, classInfo);
    }

    private void setParentsAndChildren() {
        for (ClassDependencyVisitor visitor : classVisitors) {
            String className = visitor.getClassName();
            String superName = visitor.getSuperName();
            String[] interfaces = visitor.getInterfaces();
            ClassInfo classInfo = classInfoMap.get(className);
            // superName can only be null for java/lang/Object
            if (null != superName) {
                ClassInfo superInfo = classInfoMap.get(superName);
                classInfo.setSuperclass(superInfo);
                superInfo.addToChildren(classInfo, classInfo.getChildren());
                classInfo.addToParents(superInfo, superInfo.getParents());
            }
            if (null != interfaces) {
                for (String interfaceName : interfaces) {
                    ClassInfo interfaceInfo = classInfoMap.get(interfaceName);
                    interfaceInfo.addToChildren(classInfo, classInfo.getChildren());
                    classInfo.addToParents(interfaceInfo, interfaceInfo.getParents());
                }
            }
        }
    }

    private void populateClassInfoMap() {
        Map<String, MethodInfo> methodDependencies;
        String methodName;

        String comparableName = "java/lang/Comparable";
        ClassInfo comparableInfo = new ClassInfo(comparableName);
        methodDependencies = new HashMap<>();
        methodName = "compareTo(Ljava/lang/Object;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        comparableInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String iterableName = "java/lang/Iterable";
        ClassInfo iterableInfo = new ClassInfo(iterableName);
        methodDependencies = new HashMap<>();
        methodName = "iterator()Ljava/util/Iterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "forEach(Ljava/util/function/Consumer;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "spliterator()Ljava/util/Spliterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        iterableInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String runnableName = "java/lang/Runnable";
        ClassInfo runnableInfo = new ClassInfo(runnableName);
        methodDependencies = new HashMap<>();
        methodName = "run()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        runnableInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String collectionName = "java/util/Collection";
        ClassInfo collectionInfo = new ClassInfo(collectionName);
        methodDependencies = new HashMap<>();
        methodName = "removeAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "retainAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray([Ljava/lang/Object;)[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray()[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "spliterator()Ljava/util/Spliterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "contains(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "iterator()Ljava/util/Iterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "containsAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "parallelStream()Ljava/util/stream/Stream;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "removeIf(Ljava/util/function/Predicate;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "add(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "size()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "isEmpty()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "stream()Ljava/util/stream/Stream;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "addAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        collectionInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String setName = "java/util/Set";
        ClassInfo setInfo = new ClassInfo(setName);
        methodDependencies = new HashMap<>();
        methodName = "removeAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray([Ljava/lang/Object;)[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of([Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "containsAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "add(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "clear()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "isEmpty()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "retainAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray()[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "copyOf(Ljava/util/Collection;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "spliterator()Ljava/util/Spliterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "contains(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "iterator()Ljava/util/Iterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "size()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "addAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of()Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        setInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String throwableName = "java/lang/Throwable";
        ClassInfo throwableInfo = new ClassInfo(throwableName);
        methodDependencies = new HashMap<>();
        methodName = "getOurStackTrace()[Ljava/lang/StackTraceElement;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getCause()Ljava/lang/Throwable;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "addSuppressed(Ljava/lang/Throwable;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "printStackTrace()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "printStackTrace(Ljava/lang/Throwable$PrintStreamOrWriter;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getLocalizedMessage()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "printStackTrace(Ljava/io/PrintStream;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "fillInStackTrace()Ljava/lang/Throwable;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/Throwable;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getMessage()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "readObject(Ljava/io/ObjectInputStream;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "printEnclosedStackTrace(Ljava/lang/Throwable$PrintStreamOrWriter;[Ljava/lang/StackTraceElement;Ljava/lang/String;Ljava/lang/String;Ljava/util/Set;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;Ljava/lang/Throwable;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getStackTrace()[Ljava/lang/StackTraceElement;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "printStackTrace(Ljava/io/PrintWriter;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "setStackTrace([Ljava/lang/StackTraceElement;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getSuppressed()[Ljava/lang/Throwable;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "initCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;Ljava/lang/Throwable;ZZ)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toString()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<clinit>()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "writeObject(Ljava/io/ObjectOutputStream;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "fillInStackTrace(I)Ljava/lang/Throwable;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        throwableInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String exceptionName = "java/lang/Exception";
        ClassInfo exceptionInfo = new ClassInfo(exceptionName);
        methodDependencies = new HashMap<>();
        methodName = "<init>(Ljava/lang/String;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;Ljava/lang/Throwable;ZZ)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;Ljava/lang/Throwable;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/Throwable;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        exceptionInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String iteratorName = "java/util/Iterator";
        ClassInfo iteratorInfo = new ClassInfo(iteratorName);
        methodDependencies = new HashMap<>();
        methodName = "hasNext()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "next()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "forEachRemaining(Ljava/util/function/Consumer;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        iteratorInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String functionName = "java/util/function/Function";
        ClassInfo functionInfo = new ClassInfo(functionName);
        methodDependencies = new HashMap<>();
        methodName = "apply(Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$andThen$1(Ljava/util/function/Function;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "identity()Ljava/util/function/Function;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "compose(Ljava/util/function/Function;)Ljava/util/function/Function;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "andThen(Ljava/util/function/Function;)Ljava/util/function/Function;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$identity$2(Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$compose$0(Ljava/util/function/Function;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        functionInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String mapEntryName = "java/util/Map$Entry";
        ClassInfo mapEntryInfo = new ClassInfo(mapEntryName);
        methodDependencies = new HashMap<>();
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "$deserializeLambda$(Ljava/lang/invoke/SerializedLambda;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "comparingByKey(Ljava/util/Comparator;)Ljava/util/Comparator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getValue()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "comparingByValue()Ljava/util/Comparator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$comparingByValue$827a17d5$1(Ljava/util/Comparator;Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$comparingByKey$bbdbfea9$1(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getKey()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$comparingByKey$6d558cbf$1(Ljava/util/Comparator;Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$comparingByValue$1065357e$1(Ljava/util/Map$Entry;Ljava/util/Map$Entry;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "comparingByValue(Ljava/util/Comparator;)Ljava/util/Comparator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "comparingByKey()Ljava/util/Comparator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "setValue(Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        mapEntryInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String enumName = "java/lang/Enum";
        ClassInfo enumInfo = new ClassInfo(enumName);
        methodDependencies = new HashMap<>();
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "readObject(Ljava/io/ObjectInputStream;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "ordinal()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "compareTo(Ljava/lang/Enum;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getDeclaringClass()Ljava/lang/Class;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "clone()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "compareTo(Ljava/lang/Object;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "name()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toString()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "finalize()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "readObjectNoData()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>(Ljava/lang/String;I)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        enumInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String mapName = "java/util/Map";
        ClassInfo mapInfo = new ClassInfo(mapName);
        methodDependencies = new HashMap<>();
        methodName = "putAll(Ljava/util/Map;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "replace(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "containsKey(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "merge(Ljava/lang/Object;Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "get(Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "clear()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "computeIfPresent(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "putIfAbsent(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "isEmpty()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "compute(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(Ljava/lang/Object;Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "copyOf(Ljava/util/Map;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "keySet()Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "forEach(Ljava/util/function/BiConsumer;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "containsValue(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "replaceAll(Ljava/util/function/BiFunction;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "ofEntries([Ljava/util/Map$Entry;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "replace(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "entry(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map$Entry;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "size()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "values()Ljava/util/Collection;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of()Ljava/util/Map;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "entrySet()Ljava/util/Set;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        mapInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String charSequenceName = "java/lang/CharSequence";
        ClassInfo charSequenceInfo = new ClassInfo(charSequenceName);
        methodDependencies = new HashMap<>();
        methodName = "subSequence(II)Ljava/lang/CharSequence;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toString()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "charAt(I)C";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "length()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$codePoints$1()Ljava/util/Spliterator$OfInt;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lambda$chars$0()Ljava/util/Spliterator$OfInt;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "chars()Ljava/util/stream/IntStream;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "codePoints()Ljava/util/stream/IntStream;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        charSequenceInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String objectName = "java/lang/Object";
        ClassInfo objectInfo = new ClassInfo(objectName);
        methodDependencies = new HashMap<>();
        methodName = "registerNatives()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "getClass()Ljava/lang/Class;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "wait(JI)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "clone()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "wait()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "wait(J)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "notify()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "notifyAll()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toString()Ljava/lang/String;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<clinit>()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "<init>()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "finalize()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        objectInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String listIteratorName = "java/lang/ListIterator";
        ClassInfo listIteratorInfo = new ClassInfo(listIteratorName);
        methodDependencies = new HashMap<>();
        methodName = "set(Ljava/lang/Object;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "previousIndex()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "nextIndex()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "add(Ljava/lang/Object;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hasNext()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "next()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "previous()Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hasPrevious()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        listIteratorInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String serializableName = "java/lang/Serializable";
        ClassInfo serializableInfo = new ClassInfo(serializableName);
        methodDependencies = new HashMap<>();
        serializableInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String runtimeExceptionName = "java/lang/RuntimeException";
        ClassInfo runtimeExceptionInfo = new ClassInfo(runtimeExceptionName);
        methodDependencies = new HashMap<>();
        runtimeExceptionInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        String listName = "java/util/List";
        ClassInfo listInfo = new ClassInfo(listName);
        methodDependencies = new HashMap<>();
        methodName = "removeAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray([Ljava/lang/Object;)[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(I)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "containsAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "lastIndexOf(Ljava/lang/Object;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "subList(II)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "add(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "clear()V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "equals(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "add(ILjava/lang/Object;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "remove(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "isEmpty()Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "replaceAll(Ljava/util/function/UnaryOperator;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of()Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "set(ILjava/lang/Object;)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "addAll(ILjava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "retainAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "hashCode()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "copyOf(Ljava/util/Collection;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "toArray()[Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "sort(Ljava/util/Comparator;)V";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "spliterator()Ljava/util/Spliterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "contains(Ljava/lang/Object;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "iterator()Ljava/util/Iterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "listIterator(I)Ljava/util/ListIterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "indexOf(Ljava/lang/Object;)I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "listIterator()Ljava/util/ListIterator;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "get(I)Ljava/lang/Object;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "size()I";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "addAll(Ljava/util/Collection;)Z";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of([Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        methodName = "of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;";
        methodDependencies.put(methodName, new MethodInfo(methodName));
        listInfo.setMethodMap(methodDependencies);
        //TODO: parents and chikldren

        classInfoMap.put(comparableName, comparableInfo);
        classInfoMap.put(iterableName, iterableInfo);
        classInfoMap.put(runnableName, runnableInfo);
        classInfoMap.put(collectionName, collectionInfo);
        classInfoMap.put(setName, setInfo);

        classInfoMap.put(throwableName, throwableInfo);
        classInfoMap.put(exceptionName, exceptionInfo);
        classInfoMap.put(iteratorName, iteratorInfo);
        classInfoMap.put(functionName, functionInfo);
        classInfoMap.put(mapEntryName, mapEntryInfo);

        classInfoMap.put(enumName, enumInfo);
        classInfoMap.put(mapName, mapInfo);
        classInfoMap.put(charSequenceName, charSequenceInfo);
        classInfoMap.put(objectName, objectInfo);
        classInfoMap.put(listIteratorName, listIteratorInfo);

        classInfoMap.put(serializableName, serializableInfo);
        classInfoMap.put(runtimeExceptionName, runtimeExceptionInfo);
        classInfoMap.put(listName, listInfo);
    }

    public Map<String, ClassInfo> getClassInfoMap() {
        return classInfoMap;
    }
}
