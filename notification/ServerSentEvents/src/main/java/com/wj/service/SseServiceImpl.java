package com.wj.service;

import com.wj.exception.SseException;
import com.wj.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/20 13:56
 */
@Slf4j
@Service
public class SseServiceImpl implements SseService{

    private static final ScheduledExecutorService HEARTBEAT_EXECUTORS = Executors.newScheduledThreadPool(8);
    @Override
    public SseEmitter start(String clientId) {
        // 设置为0L为永不超时
        // 次数设置30秒超时,方便测试 timeout 事件
        SseEmitter emitter = new SseEmitter(30_000L);
        log.info("MSG: SseConnect | EmitterHash: {} | ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
        SessionManager.add(clientId, emitter);
        final ScheduledFuture<?> future = HEARTBEAT_EXECUTORS.scheduleAtFixedRate(new HeartBeatTask(clientId), 0, 10, TimeUnit.SECONDS);
        emitter.onCompletion(() -> {
            log.info("MSG: SseConnectCompletion | EmitterHash: {} |ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
            SessionManager.onCompletion(clientId, future);
        });
        emitter.onTimeout(() -> {
            log.error("MSG: SseConnectTimeout | EmitterHash: {} |ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
            SessionManager.onError(clientId, new SseException("TimeOut(clientId: " + clientId + ")"));
        });
        emitter.onError(t -> {
            log.error("MSG: SseConnectError | EmitterHash: {} |ID: {} | Date: {}", emitter.hashCode(), clientId, new Date());
            SessionManager.onError(clientId, new SseException("Error(clientId: " + clientId + ")"));
        });
        return emitter;

    }

    @Override
    public String send(String clientId) {
        if(SessionManager.send(clientId,System.currentTimeMillis())){
            return "Succeed!";
        }
        return "Error!";
    }

    @Override
    public String close(String clientId) {
        log.info("MSG: SseConnectClose | ID: {} | Date: {}", clientId, new Date());
        if(SessionManager.del(clientId)){
            return "Succeed!";
        }
        return "Error!";
    }
}
