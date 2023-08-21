package com.wj.modbustcp.util;

import java.math.BigInteger;

/**
 * 十六进制字符串工具类
 *
 * @author zzh
 * @date 2021/1/20
 */
public class HexStringUtil {

    /**
     * hexString to int
     *
     * @param str hexString
     * @return 十进制的整型
     */
    public static int decodeIntValue(String str) {
        BigInteger bigint = new BigInteger(str, 16);
        return bigint.intValue();
    }

    /**
     * hexString to long
     *
     * @param str hexString
     * @return 十进制的长整型
     */
    public static long decodeLongValue(String str) {
        BigInteger bigint = new BigInteger(str, 16);
        return bigint.longValue();
    }

    /**
     * 将16进制字符串转为二进制字符串
     * eg: hexString="0FFF", bit = 16
     * return 00001111111111111
     *
     * @param hexString hexString
     * @param bit       设置返回二进制字符的位数
     * @return 二进制字符串
     */
    public static String hexStringToBinaryString(String hexString, int bit) {
        //16进制转10进制
        BigInteger sint = new BigInteger(hexString, 16);
        BigInteger bigInteger = sint.setBit(bit - 1);
        //10进制转2进制
        String origin = sint.toString(2);
        String target = bigInteger.toString(2);
        if (origin.equals(target)) {
            return target;
        } else {
            return target.replaceFirst("1", "0");
        }
    }

    /**
     * hexString to int[]
     *
     * @param str    十六进制字符串
     * @param bits   每一个数据的位数
     * @param result 每一个数据解析之后的数据
     */
    public static void decode(String str, int[] bits, double[] result) {
        if (bits.length != result.length) {
            return;
        }
        for (int i = 0; i < bits.length; i++) {
            String substring = str.substring(0, bits[i]);
            str = str.substring(bits[i]);
            if (bits[i] == 4) {
                result[i] = decodeIntValue(substring);
            } else {
                result[i] = decodeLongValue(substring);
            }
        }
    }

    /**
     * hexString to int[]
     *
     * @param str    十六进制字符串
     * @param bits   每一个数据的位数
     * @param result 每一个数据解析之后的数据
     */
    public static void decodeBit(String str, int[] bits, double[] result) {
        if (bits.length != result.length) {
            return;
        }
        String binaryString = hexStringToBinaryString(str, bits.length);
        for (int i = 0; i < binaryString.length(); i++) {
            char c = binaryString.charAt(i);
            if (c == '1') {
                result[i] = 1;
            } else {
                result[i] = 0;
            }
        }
    }
}
