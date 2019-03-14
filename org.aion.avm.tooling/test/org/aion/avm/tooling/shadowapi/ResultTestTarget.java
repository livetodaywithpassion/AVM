package org.aion.avm.tooling.shadowapi;

import org.aion.avm.api.ABIDecoder;
import org.aion.avm.api.BlockchainRuntime;
import org.aion.avm.api.Result;


public class ResultTestTarget {

    public static byte[] main() {
        return ABIDecoder.decodeAndRunWithClass(ResultTestTarget.class, BlockchainRuntime.getData());
    }

    public static String getToStringSuccessTrue() {
        Result result = new Result(true,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.toString();
    }

    public static String getToStringSuccessFalse() {
        Result result = new Result(false,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.toString();
    }

    public static boolean getEquals() {
        Result result = new Result(true,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        Result result1 = new Result(true,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.equals(result1);
    }

    public static boolean getUnequalsSameSuccessDiffData() {
        Result result = new Result(true,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        Result result1 = new Result(true,
                new byte[]{
                        0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f, 0x20
                });

        return result.equals(result1);
    }

    public static boolean getUnequalsDiffSuccessSameData() {
        Result result = new Result(false,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        Result result1 = new Result(true,
                new byte[]{
                        0x01, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.equals(result1);
    }

    public static int getHashCodeSuccessTrue() {
        Result result = new Result(true,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.hashCode();
    }

    public static int getHashCodeSuccessFalse() {
        Result result = new Result(false,
                new byte[]{
                        0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa,
                        0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11, 0x12, 0x13, 0x14,
                        0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d,
                        0x1e, 0x1f
                });

        return result.hashCode();
    }
}
