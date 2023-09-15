package com.wj.mapper;

import com.wj.entity.Employee;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/15 10:50
 */
@Repository
public interface SpringWebFluxMapper extends ReactiveSortingRepository<Employee,Long> {

    /**
     * 对象形式传参：:#{#对象名.字段名}
     * 字段传参：:字段名(@param定义)
     * 复杂查询同理
     *
     * @param employee 员工
     * @return {@link Mono}<{@link Employee}>
     */
    @Modifying
    @Query("insert into employee(id,name,age) values(:#{#employee.id},:#{#employee.name},:#{#employee.age})")
    Mono<Employee> insertDefined(Employee employee);

}

