package com.wj.service;

import com.wj.entity.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/15 10:49
 */
public interface SpringWebFluxService {

    /**
     * 查询员工列表
     * @return {@link Flux}<{@link Employee}>
     */
    Flux<Employee> findAll();

    /**
     * 新增员工信息
     * @param employee 员工信息
     * @return {@link Mono}<{@link Employee}>
     */
    Mono<Employee> insertDefined(Employee employee);

    /**
     * 删除员工
     * @param id 员工id
     * @return {@link Flux}<{@link Employee}>
     */
    Mono<Void> removeEmployee(Long id);
}
