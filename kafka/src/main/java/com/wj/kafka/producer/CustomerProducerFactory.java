package com.wj.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA
 * 这是一个平凡的Class
 *
 * @author wj
 * @date 2022/10/20 13:46
 */
public class CustomerProducerFactory {

    private static KafkaProducer<String, String> kafkaProducer = null;

    /**
     *  生成一个普通的生成者对象
     * @return 生产者对象
     */
    public static KafkaProducer<String, String> getCustomerProducer() {
        if (kafkaProducer == null) {
            // 配置信息
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // 创建kafka对象
            kafkaProducer = new KafkaProducer<>(properties);
        }
        return kafkaProducer;
    }

    /**
     * 生成一个高吞吐量的生产者
     * @param size  消息批次大小
     * @param waitTime 等待时间
     * @param cacheSize 缓冲区大小
     * @param compressionType 压缩类型 gzip、snappy、 lz4 和 zstd
     * @return 生产者producer对象
     */
    public static KafkaProducer<String, String> getCustomerProducer(String size, String waitTime, String cacheSize, String compressionType) {
        if (kafkaProducer == null) {
            // 配置信息
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            // key和value序列化
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // 设置批次大小
            properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, size);
            // 设置等待时间
            properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, waitTime);
            // 设置压缩类型
            properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,compressionType);
            // 设置缓冲区大小
            properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG,cacheSize);

            // 创建kafka对象
            kafkaProducer = new KafkaProducer<>(properties);

        }
        return kafkaProducer;
    }

    public static KafkaProducer<String, String> getCustomerProducer(String ackType, String retries) {
        if (kafkaProducer == null) {
            // 配置信息
            Properties properties = new Properties();
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            // key和value序列化
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // 设置ACK
           properties.setProperty(ProducerConfig.ACKS_CONFIG,ackType);

           // 设置重试次数
           properties.setProperty(ProducerConfig.RETRIES_CONFIG,retries);

            // 创建kafka对象
            kafkaProducer = new KafkaProducer<>(properties);

        }
        return kafkaProducer;
    }

}
