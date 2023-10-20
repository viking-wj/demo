package com.wj.trans;

import com.wj.WebEvent;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/12 9:13
 */
public class TransAggregationTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<WebEvent> stream =
                env.fromElements( new WebEvent("Mary", "./home", 1000L),
                        new WebEvent("Bob", "./cart", 2000L)
                );
        // 指定字段名称
        stream.keyBy(e -> e.user).max("timestamp").print();

        DataStreamSource<Tuple2<String, Integer>> tupleStream = env.fromElements(
                Tuple2.of("a", 1),
                Tuple2.of("a", 3),
                Tuple2.of("b", 3),
                Tuple2.of("b", 4)
        );
        tupleStream.keyBy(r -> r.f0).sum(1).print();
        tupleStream.keyBy(r -> r.f0).sum("f1").print();
        tupleStream.keyBy(r -> r.f0).max(1).print();
        tupleStream.keyBy(r -> r.f0).max("f1").print();
        tupleStream.keyBy(r -> r.f0).min(1).print();
        tupleStream.keyBy(r -> r.f0).min("f1").print();
        tupleStream.keyBy(r -> r.f0).maxBy(1).print();
        tupleStream.keyBy(r -> r.f0).maxBy("f1").print();
        tupleStream.keyBy(r -> r.f0).minBy(1).print();
        tupleStream.keyBy(r -> r.f0).minBy("f1").print();
        env.execute();
    }

}
