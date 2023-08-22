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
    UNSIGNED_INT(DealWayEnum::toUnsignedInt);

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



}
