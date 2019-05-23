package org.aion.avm.tooling.deploy.eliminator;

import static org.aion.avm.core.util.Helpers.internalNameToFulllyQualifiedName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.aion.avm.core.dappreading.JarBuilder;
import org.aion.avm.tooling.deploy.eliminator.resources.ClassD;
import org.aion.avm.tooling.deploy.eliminator.resources.ClassE;
import org.aion.avm.tooling.deploy.eliminator.resources.ClassF;
import org.aion.avm.tooling.deploy.eliminator.resources.ClassG;
import org.aion.avm.tooling.deploy.eliminator.resources.InterfaceA;
import org.aion.avm.tooling.deploy.eliminator.resources.InterfaceB;
import org.aion.avm.tooling.deploy.eliminator.resources.InterfaceC;
import org.junit.Before;
import org.junit.Test;

public class UnreachableMethodRemoverTest {

    private UnreachableMethodRemover unreachableMethodRemover;

    private static String InterfaceAname = "org/aion/avm/tooling/deploy/eliminator/resources/InterfaceA";
    private static String InterfaceBname = "org/aion/avm/tooling/deploy/eliminator/resources/InterfaceB";
    private static String InterfaceCname = "org/aion/avm/tooling/deploy/eliminator/resources/InterfaceC";
    private static String ClassDname = "org/aion/avm/tooling/deploy/eliminator/resources/ClassD";
    private static String ClassEname = "org/aion/avm/tooling/deploy/eliminator/resources/ClassE";
    private static String ClassFname = "org/aion/avm/tooling/deploy/eliminator/resources/ClassF";
    private static String ClassGname = "org/aion/avm/tooling/deploy/eliminator/resources/ClassG";

    @Before
    public void setup() {
        unreachableMethodRemover = new UnreachableMethodRemover();
    }

    @Test
    public void testMethodRemoval() throws Exception {

        byte[] jar = JarBuilder
            .buildJarForMainAndClasses(ClassG.class, ClassF.class, ClassE.class, ClassD.class,
                InterfaceC.class, InterfaceB.class, InterfaceA.class);
        byte[] optimizedJar = unreachableMethodRemover.optimize(jar);
        assertNotNull(optimizedJar);

        Map<String, byte[]> classMap = extractClasses(optimizedJar);

        assertNotNull(optimizedJar);
        assertEquals(7, classMap.size());

        MethodReachabilityDetector methodReachabilityDetector = new MethodReachabilityDetector();
        methodReachabilityDetector.optimize(ClassGname, classMap);

        Map<String, ClassInfo> classInfoMap = methodReachabilityDetector.getClassInfoMap();
        assertEquals(25, classInfoMap.size());
        ClassInfo classInfoA = classInfoMap.get(InterfaceAname);
        ClassInfo classInfoB = classInfoMap.get(InterfaceBname);
        ClassInfo classInfoC = classInfoMap.get(InterfaceCname);
        ClassInfo classInfoD = classInfoMap.get(ClassDname);
        ClassInfo classInfoE = classInfoMap.get(ClassEname);
        ClassInfo classInfoF = classInfoMap.get(ClassFname);
        ClassInfo classInfoG = classInfoMap.get(ClassGname);
        assertNotNull(classInfoA);
        assertNotNull(classInfoB);
        assertNotNull(classInfoC);
        assertNotNull(classInfoD);
        assertNotNull(classInfoE);
        assertNotNull(classInfoF);
        assertNotNull(classInfoG);

        Map<String, MethodInfo> methodInfoMapA = classInfoA.getMethodMap();
        Map<String, MethodInfo> methodInfoMapB = classInfoB.getMethodMap();
        Map<String, MethodInfo> methodInfoMapC = classInfoC.getMethodMap();
        Map<String, MethodInfo> methodInfoMapD = classInfoD.getMethodMap();
        Map<String, MethodInfo> methodInfoMapE = classInfoE.getMethodMap();
        Map<String, MethodInfo> methodInfoMapF = classInfoF.getMethodMap();
        Map<String, MethodInfo> methodInfoMapG = classInfoG.getMethodMap();

        assertEquals(0, methodInfoMapA.size());
        assertEquals(1, methodInfoMapB.size());
        assertEquals(1, methodInfoMapC.size());
        assertEquals(3, methodInfoMapD.size());
        assertEquals(1, methodInfoMapE.size());
        assertEquals(8, methodInfoMapF.size());
        assertEquals(7, methodInfoMapG.size());
    }


    private Map<String, byte[]> extractClasses(byte[] jarBytes) throws IOException {

        JarInputStream jarReader = null;
        Map<String, byte[]> classMap = new HashMap<>();

        jarReader = new JarInputStream(new ByteArrayInputStream(jarBytes), true);

        byte[] tempReadingBuffer = new byte[1024 * 1024];

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
}
