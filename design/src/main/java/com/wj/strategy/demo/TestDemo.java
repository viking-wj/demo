package com.wj.strategy.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 10:37
 */
@Slf4j
public class TestDemo {

    @Test
    public void test(){
        DuckDemo duck = new ToyDuckDemo(() -> log.info("toyDuck can not fly"));
        duck.fly();
    }
}
