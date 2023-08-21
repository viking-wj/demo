package com.wj.modbustcp.constant;

import com.wj.modbustcp.util.HexStringUtil;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * 处理方式枚举
 *
 * @author zzh
 * @date 2022/11/22
 */
public enum DealWayEnum {

    /**
     * 平常算法，默认值。读取数据/数值比例
     */
    SIMPLE(null),

    /**
     * 系数A
     * {@link #TERM_A}
     */
    COEFFICIENT_A(null),

    /**
     * 系数B
     * {@link #TERM_B}
     */
    COEFFICIENT_B(null),

    /**
     * 项A
     * {@link #COEFFICIENT_A}
     */
    TERM_A(null),

    /**
     * 项B
     * {@link #COEFFICIENT_B}
     */
    TERM_B(null),

    /**
     * 4个字节的有符号单精度浮点型
     * 由C的float字节数转java的float
     */
    FLOAT(DealWayEnum::toFloat),

    /**
     * 2个字节的无符号整型
     */
    UNSIGNED_SHORT_INT(DealWayEnum::toUnsignedShortInt),

    /**
     * 2个字节的有符号整型
     */
    SHORT_INT(DealWayEnum::toShortInt),

    /**
     * 4个字节的有符号整型
     */
    UNSIGNED_INT(DealWayEnum::toUnsignedInt),

    /**
     * 蒸汽流量的算法
     */
    STREAM_FLOW(DealWayEnum::toFlowNum);

    public static String TERM = "TERM";

    public static String COEFFICIENT = "COEFFICIENT";

    private Function<String, Double> function;

    DealWayEnum(Function<String, Double> function) {
        this.function = function;
    }

    public Function<String, Double> getFunction() {
        return function;
    }

    void setFunction(Function<String, Double> function) {
        this.function = function;
    }

    /**
     * execute the function by the string of hex
     *
     * @param s the string of hex
     * @return the result of double when executing the function
     */
    public Double execute(String s) {
        if (this.function != null) {
            return this.function.apply(s);
        }
        throw new RuntimeException("no function");
    }

    /**
     * the string of hex to float value
     *
     * @param s the string of hex
     * @return float value
     */
    private static Double toFloat(String s) {
        long l = Long.parseLong(s, 16);
        float toFloat = Float.intBitsToFloat((int) l);
        return Double.valueOf(Float.toString(toFloat));
    }

    /**
     * the string of hex to unsigned short int value
     * finally, change to double
     *
     * @param hexStr the string of hex
     * @return unsigned int value
     */
    private static Double toUnsignedShortInt(String hexStr) {
        int i = Integer.valueOf(hexStr, 16);
        return (double) i;
    }

    /**
     * the string of hex to short int value
     * finally, change to double
     *
     * @param hexStr the string of hex
     * @return unsigned int value
     */
    private static Double toShortInt(String hexStr) {
        short i = Integer.valueOf(hexStr, 16).shortValue();
        return (double) i;
    }

    /**
     * the string of hex to unsigned int value
     * finally, change to double
     *
     * @param hexStr the string of hex
     * @return unsigned int value
     */
    private static Double toUnsignedInt(String hexStr) {
        long i = Long.valueOf(hexStr, 16);
        return (double) i;
    }

    /**
     * 累积量低位 4个字节
     * 累积量高位 4个字节
     * 1、累积量低位的十进制长整型数据中，低三位为小数，其它为整数
     * 2、累积量低位的整数部分到达1000,000，累积量高位加 1
     * 3、累积量 = 累积量高位 * 1000,000 + 累积量低位/1000
     *
     * @param hexString 16进制字符串，格式：累积量低位，累积量高位
     * @return 累积量
     */
    public static Double toFlowNum(String hexString) {
        int realLength = 16;
        if (hexString.length() != realLength) {
            throw new RuntimeException("the length is error");
        }
        // 取累积量低位转二进制
        String binaryString = HexStringUtil.hexStringToBinaryString(hexString.substring(0, 8), 32);
        // 取累积量低位 的整数
        String substring = binaryString.substring(0, 29);
        // 将累积量低位整数转为10进制
        BigInteger bigInteger = new BigInteger(substring, 10);
        int lowValue = bigInteger.intValue();

        // 累积量高位
        String highStr = hexString.substring(8, 16);
        int highValue = HexStringUtil.decodeIntValue(highStr);

        return (double) highValue * 1000000 + (double) lowValue / 1000;
    }

}
