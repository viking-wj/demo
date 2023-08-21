package com.wj.modbustcp.connect;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;


/**
 * 主存储库
 * Created by IntelliJ IDEA
 *
 * @author lyy
 * @date 2021/3/24 14:43
 */

public class MasterRepository extends GenericObjectPool<ModbusLink> {

    public MasterRepository(PooledObjectFactory<ModbusLink> factory) {
        super(factory);
    }

    public MasterRepository(PooledObjectFactory<ModbusLink> factory, GenericObjectPoolConfig<ModbusLink> config) {
        super(factory, config);
    }

    public MasterRepository(PooledObjectFactory<ModbusLink> factory, GenericObjectPoolConfig<ModbusLink> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
