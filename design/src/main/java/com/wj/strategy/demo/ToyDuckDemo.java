package com.wj.strategy.demo;

import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/20 10:38
 */
@NoArgsConstructor
public class ToyDuckDemo extends DuckDemo{

    public ToyDuckDemo(FlyBehavior flyBehavior) {
        super(flyBehavior);
    }

}
