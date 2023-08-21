package com.wj.modbustcp.config;

import com.wj.modbustcp.constant.DealWayEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * modbus设备属性
 *
 * @author w
 * @date 2023/08/21
 */
@Component
@ConfigurationProperties("modbus.device")
@Data
public class ModbusDeviceProperties {
    private String[] task;

    private int[] bit;

    private int[] digit;

    private int[] num;

    private int[] proType;

    private DealWayEnum[] dealWay;

}
