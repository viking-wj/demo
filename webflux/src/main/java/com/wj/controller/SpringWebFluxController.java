package com.wj.controller;

import com.wj.entity.Employee;
import com.wj.service.SpringWebFluxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/15 10:48
 */
@RestController
@Slf4j
@RequestMapping("/test/webflux/employee")
public class SpringWebFluxController {

    @Resource(name = "springWebFluxServiceImpl")
    SpringWebFluxService springWebFluxService;

    /** Flux和Mono区别
     *  Flux发送出0~N个
     *  Mono发送出0~1个
     *  请自行百度学习具体区别
     */

    /**
     * 测试r2dbc连接mysql数据库-查询
     */
    @GetMapping()
    public Flux<Employee> employeeList() {
        return springWebFluxService.findAll();
    }

    /**
     * 测试r2dbc连接mysql数据库-新增
     */
    @PostMapping()
    public Mono<Employee> addEmployee(@RequestBody Employee employee) {
        log.info(employee.toString());
        return springWebFluxService.insertDefined(employee);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> removeEmployee(@PathVariable Long id){
        return springWebFluxService.removeEmployee(id);
    }

    /**
     * Flux : 返回0-n个元素
     * 注：需要指定MediaType
     * @return
     */
    @GetMapping(value = "/realData", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<String> flux() {
        return Flux
                .fromStream(IntStream.range(1, 5).mapToObj(i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    return "flux data--" + i;
                }));
    }
}
