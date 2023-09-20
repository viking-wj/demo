package com.wj.controller;

import com.wj.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;


/**
 * SSE测试控制器
 *
 * @author songyh
 * @date 2021/1/5
 */
@RestController
@RequestMapping("sse")
@Slf4j
public class SseTestController {

    @Resource(name = "sseServiceImpl")
    private SseService sseService;

    @RequestMapping("start")
    public SseEmitter start(@RequestParam String clientId) {
        return sseService.start(clientId);
    }

    @RequestMapping("/send")
    public String send(@RequestParam String clientId){
        return sseService.send(clientId);
    }


    @RequestMapping("/end")
    public String close(String clientId) {
        return sseService.close(clientId);
    }
}
