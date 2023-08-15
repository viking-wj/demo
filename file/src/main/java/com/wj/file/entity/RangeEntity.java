package com.wj.file.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/14 15:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangeEntity {
    private Long startByte;

    private Long endByte;

}
