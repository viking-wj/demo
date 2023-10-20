package com.wj.template;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 9:36
 */
@Slf4j
public class ConcreteClassDemo extends AbstractClassDemo{
    @Override
    public void init() {
        log.info("初始化");
    }

    @Override
    public void fun() {
        log.info("執行中");
    }

    @Override
    public void close() {
        log.info("結束");
    }
}
