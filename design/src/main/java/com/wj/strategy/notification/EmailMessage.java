package com.wj.strategy.notification;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮箱消息
 *
 * @author wj
 * @date 2023/11/2 10:04
 */
@Slf4j
public class EmailMessage implements NoticeBehavior{

    @Override
    public void sendMessage() {
        log.info("发送邮箱信息");
    }
}
