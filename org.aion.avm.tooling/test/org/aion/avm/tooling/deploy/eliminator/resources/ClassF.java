package org.aion.avm.tooling.deploy.eliminator.resources;

public class ClassF extends ClassE {

    public char classF() {
        return 'f';
    }

    @Override
    public char classE() {
        return 'e' + 'f';
    }

    @Override
    public char classD() {
        return 'd' + 'f';
    }

    @Override
    public char interfaceA() {
        return 'a' + 'f';
    }

    @Override
    public char interfaceB() {
        return 'b' + 'f';
    }

    @Override
    public char interfaceC() {
        return 'c' + 'f';
    }
}
