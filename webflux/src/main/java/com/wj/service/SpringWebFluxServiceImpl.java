package com.wj.service;

import com.wj.entity.Employee;
import com.wj.mapper.SpringWebFluxMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/15 10:49
 */
@Service
public class SpringWebFluxServiceImpl implements SpringWebFluxService {

    @Resource
    SpringWebFluxMapper springWebFluxMapper;


    @Override
    public Flux<Employee> findAll() {
        return springWebFluxMapper.findAll();
    }

    /**
     * 新增员工信息
     * @param employee 员工信息
     * @return {@link Mono}<{@link Employee}>
     */
    @Override
    public Mono<Employee> insertDefined(Employee employee) {
        return springWebFluxMapper.insertDefined(employee);
    }

    @Override
    public Mono<Void> removeEmployee(Long id) {
        return springWebFluxMapper.deleteById(id);
    }
}

