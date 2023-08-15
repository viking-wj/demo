package com.wj.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


/**
 * Created by IntelliJ IDEA
 * 这是一个平凡的Class
 *
 * @author wj
 * @date 2022/10/20 13:39
 */
public class CustomProducerCallbackPartitions {
    public static void main(String[] args) {
        String[] keys = new String[]{"a", "b", "c", "d", "e"};
//        setKey(keys);
        int partitions = 1;
        setPartitions(partitions);
    }


    private static void setKey(String[] keys) {
        KafkaProducer<String, String> kafkaProducer = CustomerProducerFactory.getCustomerProducer();
        // 发送消息以及设置回调
        int messageCount = 5;
        for (int i = 0; i < messageCount; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", keys[i], "test:" + i),
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

    /**
     * 指定partitions值
     */
    private static void setPartitions(Integer partition) {
        KafkaProducer<String, String> kafkaProducer = CustomerProducerFactory.getCustomerProducer();
        // 发送消息以及设置回调
        int messageCount = 5;
        for (int i = 0; i < messageCount; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", partition, "", "test:" + i),
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
