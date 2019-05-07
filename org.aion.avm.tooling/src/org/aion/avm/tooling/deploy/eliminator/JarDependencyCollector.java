package org.aion.avm.tooling.deploy.eliminator;

import static org.aion.avm.core.util.Helpers.internalNameToFulllyQualifiedName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.objectweb.asm.ClassReader;

public class JarDependencyCollector {

    private String mainClassName;
    private List<ClassDependencyVisitor> classVisitors = new ArrayList<>();
    private Map<String, ClassInfo> classInfoMap = new HashMap<>();
    private static final int MAX_CLASS_BYTES = 1024 * 1024;

    public JarDependencyCollector(byte[] jarBytes) {
        Map<String, byte[]> classMap = new HashMap<>();
        JarInputStream jarReader = null;
        try {
            jarReader = new JarInputStream(new ByteArrayInputStream(jarBytes), true);
            mainClassName = extractMainClassName(jarReader);
            classMap = extractClasses(jarReader);
        } catch (IOException e) {
            System.err.println("Error reading JAR file");
            e.printStackTrace();
        }

        for (byte[] clazz : classMap.values()) {
            visitClass(clazz);
        }

        setParentsAndChildren();
    }

    private String extractMainClassName(JarInputStream jarReader) {
        Manifest manifest = jarReader.getManifest();
        if (null != manifest && manifest.getMainAttributes() != null) {
            return manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        } else {
            throw new RuntimeException("Manifest file required");
        }
    }

    private Map<String, byte[]> extractClasses(JarInputStream jarReader) throws IOException {

        Map<String, byte[]> classMap = new HashMap<>();
        byte[] tempReadingBuffer = new byte[MAX_CLASS_BYTES];

        JarEntry entry;
        while (null != (entry = jarReader.getNextJarEntry())) {
            String name = entry.getName();

            if (name.endsWith(".class")
                && !name.equals("package-info.class")
                && !name.equals("module-info.class")) {

                String internalClassName = name.replaceAll(".class$", "");
                String qualifiedClassName = internalNameToFulllyQualifiedName(internalClassName);
                int readSize = jarReader.readNBytes(tempReadingBuffer, 0, tempReadingBuffer.length);

                if (0 != jarReader.available()) {
                    throw new RuntimeException("Class file too big: " + name);
                }

                byte[] classBytes = new byte[readSize];
                System.arraycopy(tempReadingBuffer, 0, classBytes, 0, readSize);
                classMap.put(qualifiedClassName, classBytes);
            }
        }
        return classMap;
    }

    // Should only be called once per class
    private void visitClass(byte[] classBytes) {

        ClassReader reader = new ClassReader(classBytes);

        ClassDependencyVisitor classVisitor = new ClassDependencyVisitor();
        classVisitors.add(classVisitor);
        reader.accept(classVisitor, 0);

        String className = classVisitor.getClassName();
        ClassInfo classInfo = new ClassInfo(className, classBytes);
        classInfo.setMethodMap(classVisitor.getMethodMap());
        classInfoMap.put(className, classInfo);
    }

    private void setParentsAndChildren() {
        for (ClassDependencyVisitor visitor : classVisitors) {
            String className = visitor.getClassName();
            String superName = visitor.getSuperName();
            String[] interfaces = visitor.getInterfaces();
            ClassInfo classInfo = classInfoMap.get(className);
            if (null != superName && !superName.equals("java/lang/Object")) {
                ClassInfo superInfo = classInfoMap.get(superName);
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

    public Map<String, ClassInfo> getClassInfoMap() {
        return classInfoMap;
    }

    public String getMainClassName() {
        return mainClassName;
    }
}
