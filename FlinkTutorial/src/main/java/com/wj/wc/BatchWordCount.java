package com.wj.wc;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.springframework.core.io.ClassPathResource;


import java.util.Objects;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/11 9:37
 */
public class BatchWordCount {
    public static void main(String[] args) throws Exception {
        // 1. 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // 2. 从文件读取数据 按行读取(存储的元素就是每行的文本) 数据输入
        String path = Objects.requireNonNull(BatchWordCount.class.getClassLoader().getResource("")).getPath();
        ClassPathResource classPathResource = new ClassPathResource(path + "/input/word.txt");
        DataSource<String> lineDs = env.readTextFile(classPathResource.getPath());
        // 3. 转换数据格式 数据处理
        FlatMapOperator<String, Tuple2<String, Long>> wordAndOne = lineDs
                .flatMap((String line, Collector<Tuple2<String, Long>> out)
                        -> {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        out.collect(Tuple2.of(word, 1L));
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.LONG));
        // map,返回每个字符长度
        MapOperator<String, Integer> map = lineDs.map((MapFunction<String, Integer>) String::length);

        map.print();
        FilterOperator<String> h = lineDs.filter((FilterFunction<String>) s -> !s.startsWith("h"));
        h.print();

        //当 Lambda 表达式 使用 Java 泛型的时候, 由于泛型擦除的存在, 需要显示的声明类型信息
        // 4. 按照 word 进行分组
        UnsortedGrouping<Tuple2<String, Long>> wordAndOneUg = wordAndOne.groupBy(0);
        // 5. 分组内聚合统计
        AggregateOperator<Tuple2<String, Long>> sum = wordAndOneUg.sum(1);
        // 6. 打印结果 数据输出
        sum.print();
    }
}
