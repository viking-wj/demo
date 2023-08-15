package com.wj.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA
 * 这是一个平凡的Class
 *
 * @author wj
 * @date 2022/10/20 11:24
 */
public class CustomProducer {
    public static void main(String[] args) {
        KafkaProducer<String, String> kafkaProducer = CustomerProducerFactory.getCustomerProducer();

        // 4. 调用 send 方法,发送消息
        int messageCount = 5;

        for (int i = 0; i < messageCount; i++) {

            kafkaProducer.send(new ProducerRecord<>("first", "w" + i));

        }

        // 5. 关闭资源
        kafkaProducer.close();
    }
}
