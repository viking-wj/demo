package com.wj.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA
 * 这是一个平凡的Class
 *
 * @author wj
 * @date 2022/10/20 11:37
 */
public class CustomProducerCallback {
    public static void main(String[] args) {
        KafkaProducer<String, String> kafkaProducer = CustomerProducerFactory.getCustomerProducer();
        // 发送消息以及设置回调
        int messageCount = 5;
        for (int i = 0; i < messageCount; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "test:" + i),
                    (RecordMetadata recordMetadata, Exception e) -> {
                        if (e == null) {
                            System.out.println("主题：" + recordMetadata.topic() + "->" + "分区" + recordMetadata.partition());
                        } else {
                            e.printStackTrace();
                        }
                    }
            );
            try {
                Thread.sleep(2);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
            kafkaProducer.close();

    }
}
