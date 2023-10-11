package com.wj.trans;

import com.wj.WebEvent;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * 基本转换算子
 * 映射、过滤、扁平映射
 *
 * @author wj
 * @date 2023/10/11 14:24
 */
public class TransMapTest {
    public static void main(String[] args) throws Exception {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度
        env.setParallelism(2);
        // 数据源
        DataStreamSource<WebEvent> stream = env.fromElements(new WebEvent("Mary", "./home", 1000L),
                new WebEvent("Bob", "./cart", 2000L));

        // 转换算子
        // 映射
        SingleOutputStreamOperator<String> map = stream.map((MapFunction<WebEvent, String>) WebEvent::getUser);

        // 输出
        map.print();
        // 过滤
        stream.filter((FilterFunction<WebEvent>) webEvent -> "Mary".equals(webEvent.user)).print();

        // 扁平映射
        stream.flatMap(new FlatMapFunction<WebEvent, String>() {
            @Override
            public void flatMap(WebEvent webEvent, Collector<String> collector) throws Exception {
                if (webEvent.user.equals("Mary")) {
                    collector.collect(webEvent.user);
                } else if (webEvent.user.equals("Bob")) {
                    collector.collect(webEvent.user);
                    collector.collect(webEvent.url);
                }
            }
        }).print();
        // 手动执行 自动关闭
        env.execute();
    }
}
