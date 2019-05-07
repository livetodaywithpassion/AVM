package org.aion.avm.tooling.deploy.eliminator.resources;

public class ClassG extends ClassE {

    public static void main() {
        InterfaceB b = new ClassF();
        b.interfaceB();
        ClassD d = new ClassG();
        d.classD();
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
