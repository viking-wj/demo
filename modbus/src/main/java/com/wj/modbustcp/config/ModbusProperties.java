package com.wj.modbustcp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/21 17:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "modbus.master.global")
public class ModbusProperties {

    private int maxTotal;

    private int maxIdle;

    private int minIdle;

    private long maxWaitMillis;

    private int slaveId;

    private int startAddress;

    private int registerNumber;

    private List<String> address;
}
