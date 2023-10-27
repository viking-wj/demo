package com.wj.emqx;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/10/26 17:20
 */
@Component
@Slf4j
public class initEmqx {

    @PostConstruct
    public void init(){
        try {
            // 建立连接
            String broker = "tcp://192.168.1.31:31859";
            String subTopic = "keenyoda/realTimeData/upload/RAK7255";
            int qos = 2;
            String clientId = "emqx_test";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("emqx_test");
            connOpts.setPassword("emqx_test_password".toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);

            // 设置回调
            client.setCallback(new io.emqx.OnMessageCallback());

            // 建立连接
            log.info("Connecting to broker: " + broker);
            client.connect(connOpts);
            log.info("Connected");
            // 订阅消息
            client.subscribe(subTopic);
        } catch (MqttException e) {
            log.info("reason " + e.getReasonCode());
            log.info("msg " + e.getMessage());
            log.info("loc " + e.getLocalizedMessage());
            log.info("cause " + e.getCause());
            log.info("exception " + e);
        }
    }
}
