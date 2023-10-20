package com.wj.template;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 9:32
 */
public abstract class  AbstractClassDemo {

    /**
     * 初始化
     */
    public abstract void init();

    /**
     * 邏輯執行
     */
    public abstract void fun();

    /**
     * 关闭
     */
    public abstract void close();

    /**
     * 模板法
     */
    public final void templateMethod(){
        init();
        fun();
        close();
    }
}
