package com.wj.modbustcp.init;

import com.serotonin.modbus4j.sero.util.queue.ByteQueue;
import com.wj.modbustcp.config.ModbusProperties;
import com.wj.modbustcp.connect.MasterRepository;
import com.wj.modbustcp.connect.ModbusLink;
import com.wj.modbustcp.connect.ModbusLinkFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

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

    @Bean(name = "masterRepository")
    public MasterRepository getMasterRepository(){
        ModbusLinkFactory modbusLinkFactory = new ModbusLinkFactory(modbusProperties.getAddresses());
        GenericObjectPoolConfig<ModbusLink> config = new GenericObjectPoolConfig<>();
        config.setMaxIdle(1);
        config.setMaxTotal(1);
        config.setMinIdle(1);
        config.setMaxWaitMillis(1000);
        return new MasterRepository(modbusLinkFactory,config);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        ModbusLink modbusLink = masterRepository.borrowObject();
        ByteQueue send = modbusLink.send(1, 0, 1);
        log.info(send.toString());
    }
}
