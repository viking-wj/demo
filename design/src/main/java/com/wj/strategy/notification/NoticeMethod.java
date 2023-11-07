package com.wj.strategy.notification;

import lombok.extern.slf4j.Slf4j;

/**
 * 通知方式抽象类
 *
 * @author wj
 * @date 2023/11/2 10:09
 */
@Slf4j
public abstract class NoticeMethod {

    private NoticeBehavior noticeBehavior;

    public void sendMessage() {
        if (null == noticeBehavior) {
            log.info("默认通知方式");
        } else {
            noticeBehavior.sendMessage();
        }
    }

}
