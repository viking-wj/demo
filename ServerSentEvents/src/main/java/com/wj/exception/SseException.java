package com.wj.exception;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/20 11:28
 */
public class SseException extends RuntimeException{
    public SseException(String message) {
        super(message);
    }
}
