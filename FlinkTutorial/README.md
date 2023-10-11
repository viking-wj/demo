# Flink DataStrea API

##### 执行环境

1. getExecutionEnvironment

   ```java
   // 根据当前运行的上下文直接得到相对应的运行环境
   StreamExecutionEnvironment env =
   StreamExecutionEnvironment.getExecutionEnvironment();
   ```



2. createLocalEnvironment

   ```java
   // 这个方法返回一个本地执行环境。可以在调用时传入一个参数，指定默认的并行度 默认并行度就是本地的 CPU 核心数
   StreamExecutionEnvironment localEnv =
   StreamExecutionEnvironment.createLocalEnvironment();
   ```



3. createRemoteEnvironment

   ```java
   // 这个方法返回集群执行环境。需要在调用时指定 JobManager 的主机名和端口号，并指定
   要在集群中运行的 Jar 包
   StreamExecutionEnvironment remoteEnv = StreamExecutionEnvironment 
    .createRemoteEnvironment( 
    "host", // JobManager 主机名
    1234, // JobManager 进程端口号
    "path/to/jarFile.jar" // 提交给 JobManager 的 JAR 包
    );
   ```

##### 执行模式

- 流执行模式（STREAMING）
- 批执行模式（BATCH）
- 自动模式（AUTOMATIC）

Flink默认为STREMANING模式

BATCH 模式的配置：

（1） 命令行

```bash
bin/flink run -Dexecution.runtime-mode=BATCH ...
```

（2）代码

```java
StreamExecutionEnvironment env =
StreamExecutionEnvironment.getExecutionEnvironment(); 
env.setRuntimeMode(RuntimeExecutionMode.BATCH);
```



**用 BATCH 模式处理批量数据，用 STREAMING 模式处理流式数据**



##### 源算子

1. 从集合中读取数据

   ```java
   // 最简单的读取数据的方式，就是在代码中直接创建一个 Java 集合，然后调用执行环境的fromCollection 方法进行读取
   public static void main(String[] args) throws Exception { 
    StreamExecutionEnvironment env =
   StreamExecutionEnvironment.getExecutionEnvironment(); 
   env.setParallelism(1); 
    ArrayList<Event> clicks = new ArrayList<>(); 
   clicks.add(new Event("Mary","./home",1000L)); 
   clicks.add(new Event("Bob","./cart",2000L)); 
    DataStream<Event> stream = env.fromCollection(clicks); 
    stream.print(); 
   env.execute(); 
   }
   ```



2. 从文件中读取数据

   ```java
   DataStream<String> stream = env.readTextFile("clicks.csv");
   /* 参数可以是目录，也可以是文件；
    路径可以是相对路径，也可以是绝对路径；
    相对路径是从系统属性 user.dir 获取路径: idea 下是 project 的根目录, standalone 模式
   下是集群节点根目录；
    也可以从 hdfs 目录下读取, 使用路径 hdfs://..., 由于 Flink 没有提供 hadoop 相关依赖, 
   需要 pom 中添加相关依赖:
   <dependency> 
    <groupId>org.apache.hadoop</groupId> 
    <artifactId>hadoop-client</artifactId> 
    <version>2.7.5</version> 
    <scope>provided</scope> 
   </dependency>
   */
   ```



3. 从Socket中读取数据

   ```java
   DataStream<String> stream = env.socketTextStream("localhost", 7777);
   ```



4. 从Kafka读取数据

   ```java
   package com.wj;
   
   import org.apache.flink.api.common.serialization.SimpleStringSchema;
   import org.apache.flink.streaming.api.datastream.DataStreamSource;
   import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
   import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
   
   import java.util.Properties;
   
   /**
    * @author SanShi
    */
   public class SourceKafkaTest {
       public static void main(String[] args) throws Exception {
           StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
           env.setParallelism(1);
           Properties properties = new Properties();
           properties.setProperty("bootstrap.servers", "hadoop102:9092");
           properties.setProperty("group.id", "consumer-group");
           properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
           properties.setProperty("auto.offset.reset", "latest");
           DataStreamSource<String> stream = env.addSource(
                   // topic 从哪些主题中读取数据。topic 、topic列表 、 正则表达式
                   // 键值对序列化器
                   // kafka客户端属性
                   new FlinkKafkaConsumer<>(
                           "clicks", new SimpleStringSchema(),
                           properties
                   ));
           stream.print("Kafka");
           env.execute();
       }
   }
   
   ```

   依赖：

   ```xml
   <dependency>
               <groupId>org.apache.flink</groupId>
               <artifactId>flink-connector-kafka_${scala.binary.version}</artifactId>
           </dependency>
   ```



5. 自定义Source

   ```java
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
   ```
##### 转换算子

- 基本转换算子
   - 映射（一一映射，消费一个元素产出一个元素）
   - 过滤（对数据流执行一个过滤）
   - 扁平映射（消费一个元素，可以产生 0 到多个元素）

```java
package com.wj.trans;

import com.wj.WebEvent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 基本转换算子
 *  映射、过滤、扁平映射
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
        DataStreamSource<WebEvent> stream = env.fromElements( new WebEvent("Mary", "./home", 1000L),
                new WebEvent("Bob", "./cart", 2000L));

        // 转换算子
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
        // 手动执行、自动关闭
        env.execute();
    }
}
```









