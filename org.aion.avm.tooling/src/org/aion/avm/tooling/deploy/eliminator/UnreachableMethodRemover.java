package org.aion.avm.tooling.deploy.eliminator;

import static org.aion.avm.core.util.Helpers.internalNameToFulllyQualifiedName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.aion.avm.core.dappreading.JarBuilder;
import org.aion.avm.core.util.Helpers;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class UnreachableMethodRemover {

    private static final int MAX_CLASS_BYTES = 1024 * 1024;

    public byte[] optimize(byte[] jarBytes) throws Exception {

        Map<String, byte[]> classMap = new HashMap<>();
        String mainClassName = "";

        JarInputStream jarReader = null;
        try {
            jarReader = new JarInputStream(new ByteArrayInputStream(jarBytes), true);
            mainClassName = Helpers.fulllyQualifiedNameToInternalName(extractMainClassName(jarReader));
            classMap = extractClasses(jarReader);
        } catch (IOException e) {
            System.err.println("Error reading JAR file");
            e.printStackTrace();
        }

        // Use the MethodReachabilityDetector to get the information about reachability
        MethodReachabilityDetector detector = new MethodReachabilityDetector();
        Map<String, ClassInfo> classInfoMap = detector.optimize(mainClassName, classMap);

        for (Map.Entry<String, byte[]> entry : classMap.entrySet()) {
            ClassReader reader = new ClassReader(entry.getValue());
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassUnreachabilityVisitor classVisitor = new ClassUnreachabilityVisitor(classWriter, classInfoMap.get(entry.getKey()));
            reader.accept(classVisitor, 0);
            classMap.replace(entry.getKey(), classWriter.toByteArray());
        }

        return JarBuilder.buildJarForMainNameAndClassMap(mainClassName, classMap);
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
                int readSize = jarReader.readNBytes(tempReadingBuffer, 0, tempReadingBuffer.length);

                if (0 != jarReader.available()) {
                    throw new RuntimeException("Class file too big: " + name);
                }

                byte[] classBytes = new byte[readSize];
                System.arraycopy(tempReadingBuffer, 0, classBytes, 0, readSize);
                classMap.put(internalClassName, classBytes);
            }
        }
        return classMap;
    }
}

