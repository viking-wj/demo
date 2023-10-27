package com.wj.array;

import static java.lang.Math.min;

/**
 * The best container for holding water
 * height = {1, 8, 6, 2, 5, 4, 8, 3, 7}
 * return 49
 *
 * @author wj
 * @date 2023/10/23 14:07
 */
public class ContainerForWater {
    public static void main(String[] args) {
        int[] height = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        System.out.println(fun1(height));
        System.out.println(fun2(height));
    }

    /**
     * 暴力枚举
     * 超时
     *
     * @param height 高度
     * @return int
     */
    public static int fun1(int[] height) {
        int maxY = 0;
        for (int i = 1; i < height.length; i++) {
            int max = 0;
            for (int j = 0; j < height.length - i; j++) {
                int result = min(height[j], height[j + i]) * i;
                if (result > max) {
                    max = result;
                }
            }
            if (maxY < max) {
                maxY = max;
            }
        }
        return maxY;
    }

    /**
     * 双指针、贪心
     *
     * @param height 高度
     * @return int
     */
    public static int fun2(int[] height) {
        int max = 0;
        for (int i = 0, j = height.length - 1; i < j; i++, j--) {
            int y = min(height[i], height[j]);
            int x = j-i;
            max = Math.max(x*y,max);
        }
        return max;
    }
}
