package com.wj.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 10:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class DuckDemo {
    private FlyBehavior flyBehavior;

    public void fly() {
        if (null != flyBehavior) {
            flyBehavior.fly();
        }else{
            log.info("duck can fly");
        }
    }
}
