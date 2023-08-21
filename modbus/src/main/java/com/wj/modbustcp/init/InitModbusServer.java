package com.wj.modbustcp.init;

import com.serotonin.modbus4j.sero.util.queue.ByteQueue;
import com.wj.modbustcp.config.ModbusProperties;
import com.wj.modbustcp.connect.MasterRepository;
import com.wj.modbustcp.connect.ModbusLink;
import com.wj.modbustcp.connect.ModbusLinkFactory;
import com.wj.modbustcp.deal.DevicePointManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/21 17:28
 */
@Component
@Slf4j
public class InitModbusServer implements InitializingBean {

    @Resource
    private ModbusProperties modbusProperties;

    @Resource
    private MasterRepository masterRepository;

    @Resource
    private DevicePointManager devicePointManager;

    @Bean(name = "masterRepository")
    public MasterRepository getMasterRepository(){
        ModbusLinkFactory modbusLinkFactory = new ModbusLinkFactory(modbusProperties.getAddress());
        GenericObjectPoolConfig<ModbusLink> config = new GenericObjectPoolConfig<>();
        config.setMaxIdle(modbusProperties.getMaxIdle());
        config.setMaxTotal(modbusProperties.getMaxTotal());
        config.setMinIdle(modbusProperties.getMinIdle());
        config.setMaxWaitMillis(modbusProperties.getMaxWaitMillis());
        return new MasterRepository(modbusLinkFactory,config);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 开启定时任务间隔时间内获取实时数据
        ModbusLink modbusLink = masterRepository.borrowObject();
        ByteQueue send = modbusLink.send(modbusProperties.getSlaveId(),
                modbusProperties.getStartAddress(), modbusProperties.getRegisterNumber());
       log.info(""+send);
        // Convert ByteQueue to byte array
        byte[] byteArray = send.peekAll();
        // Convert byte array to hexadecimal string
        String hexString = bytesToHexString(byteArray);
        double[] result = devicePointManager.getResult(null, hexString);
        System.out.println(Arrays.toString(result));
    }

    private  String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to its hexadecimal representation
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
