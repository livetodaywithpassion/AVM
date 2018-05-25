package org.aion.avm.core;


/**
 * The test class loaded by HashCodeTest.
 */
public class HashCodeTestTarget {
    public HashCodeTestTarget() {
        // Empty instance - we just create these for their hashcodes.
    }
    
    public static int getOneHashCode() {
        return new HashCodeTestTarget().hashCode();
    }
}