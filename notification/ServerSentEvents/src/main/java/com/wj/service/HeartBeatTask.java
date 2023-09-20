package com.wj.service;

import com.wj.session.SessionManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/20 13:57
 */
@Slf4j
public class HeartBeatTask implements Runnable {

    private final String clientId;

    public HeartBeatTask(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void run() {
        log.info("MSG:SseHeartbeat | ID:{}|Data:{}", clientId, new Date());
        SessionManager.send(clientId, "ping");
    }
}
