package com.wj.source;



import com.wj.WebEvent;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/11 11:43
 */

public class ClickSource implements SourceFunction<WebEvent> {
    private Boolean running = true;

    @Override
    public void run(SourceContext<WebEvent> ctx) throws Exception {
        // 在指定的数据集中随机选取数据
        Random random = new Random();
        String[] users = {"Mary", "Alice", "Bob", "Cary"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=1", "./prod?id=2"};
        while (running) {
            ctx.collect(new WebEvent(
                    users[random.nextInt(users.length)],
                    urls[random.nextInt(urls.length)],
                    Calendar.getInstance().getTimeInMillis()
            ));
            // 隔 1 秒生成一个点击事件，方便观测
            wait(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

}
