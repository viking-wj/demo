package com.wj.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/20 13:41
 */
public interface SseService {

    /**
     * 建立连接
     * @param clientId 客户端id
     * @return {@link SseEmitter}
     */
    SseEmitter start(String clientId);

    /**
     * 发送数据
     * @param clientId 客户端id
     * @return {@link String}
     */
    String send(String clientId);

    /**
     * 关闭连接
     * @param clientId 客户端id
     * @return {@link String}
     */
    String close(String clientId);
}
