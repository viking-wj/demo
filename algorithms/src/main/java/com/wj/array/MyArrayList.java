package com.wj.array;


/**
 * Created by IntelliJ IDEA
 * 自定义实现arrayList
 *
 * @author wj
 * @date 2023/10/8 17:02
 */
public class MyArrayList<T> {

    private Object[] array;

    private static final int defaultSize = 8;

    private static final int multiple = 2;

    private int length;

    private int size = 0;

    public MyArrayList() {
        this.length = defaultSize;
        this.array = new Object[defaultSize];
    }

    public MyArrayList(int size) {
        this.length = size;
        this.array = new Object[size];
    }

    public void add(T data) {
        expansion();
        this.array[size++] = data;
    }

    public void add(int index, T data) {
        expansion();
        // 后移数据
        for (int i = size-1; i >= index; i--) {
            array[i+1] = array[i];
        }
        array[index] = data;
        size++;
    }


    public T get(int index) {
        if (index > size) {
            return null;
        }

        return (T) this.array[index];
    }

    public void del(int index) {
        if (index != size-1) {
            // 前移数据进行覆盖
            for (int i = index; i < size; i++) {
                array[i] = array[i + 1];
            }
        }
        size--;
    }

    public int size() {
        return this.size;
    }

    private void expansion() {
        // 判断是否需要扩容
        if (size >= length) {
            Object[] newArray = new Object[length * multiple];
            length = length * multiple;
            System.arraycopy(this.array, 0, newArray, 0, this.array.length);
            this.array = newArray;
        }
    }

}
