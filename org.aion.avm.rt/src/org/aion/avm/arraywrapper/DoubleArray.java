package org.aion.avm.arraywrapper;

import org.aion.avm.internal.IDeserializer;
import org.aion.avm.internal.IObject;
import org.aion.avm.internal.IObjectDeserializer;
import org.aion.avm.internal.IObjectSerializer;

import java.util.Arrays;
import java.util.function.Consumer;


public class DoubleArray extends Array {

    private double[] underlying;

    public static DoubleArray initArray(int c){
        //IHelper.currentContractHelper.get().externalChargeEnergy(c * 64);
        return new DoubleArray(c);
    }

    public DoubleArray(int c) {
        this.underlying = new double[c];
    }

    // Deserializer support.
    public DoubleArray(IDeserializer deserializer, long instanceId) {
        super(deserializer, instanceId);
    }

    public void deserializeSelf(java.lang.Class<?> firstRealImplementation, IObjectDeserializer deserializer) {
        super.deserializeSelf(ByteArray.class, deserializer);
        
        // TODO:  We probably want faster array copies.
        int length = deserializer.readInt();
        this.underlying = new double[length];
        for (int i = 0; i < length; ++i) {
            this.underlying[i] = Double.longBitsToDouble(deserializer.readLong());
        }
    }

    public void serializeSelf(java.lang.Class<?> firstRealImplementation, IObjectSerializer serializer, Consumer<org.aion.avm.shadow.java.lang.Object> nextObjectQueue) {
        super.serializeSelf(IntArray.class, serializer, nextObjectQueue);
        
        // TODO:  We probably want faster array copies.
        serializer.writeInt(this.underlying.length);
        for (int i = 0; i < this.underlying.length; ++i) {
            serializer.writeLong(Double.doubleToLongBits(this.underlying[i]));
        }
    }

    public int length() {
        return this.underlying.length;
    }

    public double get(int idx) {
        return this.underlying[idx];
    }

    public void set(int idx, double val) {
        this.underlying[idx] = val;
    }

    public IObject avm_clone() {
        return new DoubleArray(Arrays.copyOf(underlying, underlying.length));
    }

    public IObject clone() {
        return new DoubleArray(Arrays.copyOf(underlying, underlying.length));
    }

    //========================================================
    // Methods below are used by runtime and test code only!
    //========================================================

    public DoubleArray(double[] underlying) {
        this.underlying = underlying;
    }

    public double[] getUnderlying() {
        return underlying;
    }

    public void setUnderlyingAsObject(java.lang.Object u){
        this.underlying = (double[]) u;
    }

    public java.lang.Object getUnderlyingAsObject(){
        return underlying;
    }
}
