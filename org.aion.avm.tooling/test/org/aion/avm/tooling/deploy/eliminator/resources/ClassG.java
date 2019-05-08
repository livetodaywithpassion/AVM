package org.aion.avm.tooling.deploy.eliminator.resources;

import avm.Blockchain;

public class ClassG extends ClassE {

    public static byte[] main() {
        InterfaceB b = new ClassF();
        b.interfaceB();
        ClassD d = new ClassG();
        d.classD();
        ClassF f = new ClassF();
        f.interfaceC();
        ClassF.classFStaticMethod();
        callClassD(5);
        invokeFlambda();
        return null;
    }

    public static void invokeFlambda() {
        ClassF f = new ClassF();
        int y = f.getIncrementorLambda().apply(5);
        Blockchain.require(y == 6);
    }

    private static void callClassD(int val) {
        ClassD d = new ClassF();
        d.interfaceC();
    }

    public char classF() {
        return 'g';
    }

    @Override
    public char classE() {
        return 'e' + 'g';
    }

    @Override
    public char classD() {
        return 'd' + 'g';
    }

    @Override
    public char interfaceA() {
        return 'a' + 'g';
    }

    @Override
    public char interfaceB() {
        return 'b' + 'g';
    }

    @Override
    public char interfaceC() {
        return 'c' + 'g';
    }
}
