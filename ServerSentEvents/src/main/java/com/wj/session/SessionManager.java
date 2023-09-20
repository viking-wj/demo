package com.wj.session;

import com.wj.exception.SseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/20 11:22
 */
@Slf4j
public class SessionManager {

    private static final ConcurrentHashMap<String, SseEmitter> SESSION = new ConcurrentHashMap<>();

    /**
     *  判断Session是否存在
     * @param id 客户端id
     * @return boolean 判断结果
     */
    public static boolean exist(String id) {
        return SESSION.containsKey(id);
    }

    /**
     * 建立新session
     * @param id 客户端id
     * @param sseEmitter 新SESSION
     */
    public static void add(String id, SseEmitter sseEmitter) {
        SseEmitter oldSseEmitter = SESSION.get(id);
        if (null != oldSseEmitter) {
            // 结束旧SESSION
            oldSseEmitter.completeWithError(new SSLException("RepeatConnect(Id:" + id + ")"));
        }
        // 建立新SESSION
        SESSION.put(id, sseEmitter);
    }

    /**
     * 删除session
     * @param id 客户端id
     * @return boolean 删除结果
     */
    public static boolean del(String id) {
        SseEmitter emitter = SESSION.remove(id);
        if (null != emitter) {
            emitter.complete();
            return true;
        }
        return false;
    }

    /**
     * 给指定的客户端发送数据，默认发送的为data
     *
     * @param id  客户端id
     * @param msg 消息实体
     * @return boolean 发送结果
     */
    public static boolean send(String id, Object msg) {
        SseEmitter emitter = SESSION.get(id);
        if (null != emitter) {
            try {
                emitter.send(msg);
                return true;
            } catch (IOException e) {
                log.error("MSG: SendMessageError-IOException " +
                        "| ID: " + id + " | Date: " + new Date() + " |", e);
                return false;
            }
        }
        return false;
    }

    /**
     * SseEmitter 触发onCompletion时业务中需要处理的逻辑，
     * 包括停止线程池中的线程执行（心跳），移除缓存的SESSION等
     *
     * @param id     客户端id
     * @param future 线程任务
     */
    public static void onCompletion(String id, ScheduledFuture<?> future) {
        SESSION.remove(id);
        if (null != future) {
            // 中断心跳发送
            future.cancel(true);
        }
    }

    /**
     * SseEmitter onTimeout 或 onError 后执行的逻辑
     *
     * @param id 客户端id
     * @param e  自定义异常
     */
    public static void onError(String id, SseException e) {
        SseEmitter emitter = SESSION.get(id);
        if (null != emitter) {
            emitter.completeWithError(e);
        }
    }
}
