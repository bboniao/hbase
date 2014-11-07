package com.bboniao.hbase.split;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigInteger;

/**
 * md5 拆分算法
 * Created by bboniao on 11/6/14.
 */
public class MD5StringSplit implements SplitAlgorithm {

    private static final String MAXMD5 = "FFFFFFFF";

    private static final BigInteger MAXMD5_INT = new BigInteger(MAXMD5, 16);

    private static final int ROWCOMPARISONLENGTH = MAXMD5.length();


    @Override
    public byte[][] split(int n) {
        BigInteger[] splits = new BigInteger[n - 1];
        BigInteger sizeOfEachSplit = MAXMD5_INT.divide(BigInteger.valueOf(n));
        for (int i = 1; i < n; i++) {
            // NOTE: this means the last region gets all the slop.
            // This is not a big deal if we're assuming n << MAXMD5
            splits[i - 1] = sizeOfEachSplit.multiply(BigInteger.valueOf(i));
        }
        return convertToBytes(splits);
    }

    private static byte[][] convertToBytes(BigInteger[] bigIntegers) {
        byte[][] returnBytes = new byte[bigIntegers.length][];
        for (int i = 0; i < bigIntegers.length; i++) {
            returnBytes[i] = convertToByte(bigIntegers[i]);
        }
        return returnBytes;
    }

    private static byte[] convertToByte(BigInteger bigInteger) {
        String bigIntegerString = bigInteger.toString(16);
        bigIntegerString = StringUtils.leftPad(bigIntegerString, ROWCOMPARISONLENGTH, '0');
        return Bytes.toBytes(bigIntegerString);
    }
}
