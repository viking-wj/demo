package com.wj.array;


/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date ${DATE} ${TIME}
 */

public class Main {
    public static void main(String[] args) {
        MyArrayList<Integer> integerMyArrayList = new MyArrayList<>(2);
        integerMyArrayList.add(1);
        integerMyArrayList.add(2);
        integerMyArrayList.add(3);
        integerMyArrayList.del(0);
        integerMyArrayList.add(0,4);
        for (int i = 0; i < integerMyArrayList.size(); i++) {
            System.out.println(integerMyArrayList.get(i));
        }
    }
}
