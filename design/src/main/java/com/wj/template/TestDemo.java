package com.wj.template;

import org.junit.Test;

/**
 * 测试演示
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 9:37
 */
public class TestDemo {
    @Test
    public void testDemo(){
        AbstractClassDemo demo = new ConcreteClassDemo();
        demo.templateMethod();
    }
}
