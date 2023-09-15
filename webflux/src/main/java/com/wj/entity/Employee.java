package com.wj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/9/15 10:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("employee")
public class Employee {

    @Id
    private Integer id;

    private String name;

    private Integer age;
}
