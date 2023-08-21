package com.wj.modbustcp.connect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * modbus连接工厂
 * Created by IntelliJ IDEA
 *
 * @author lyy
 * @date 2021/3/26 11:41
 */
@Slf4j
public class ModbusLinkFactory implements PooledObjectFactory<ModbusLink> {

    private List<String> urlList;

    private AtomicLong atomic = new AtomicLong(-1);

    public ModbusLinkFactory(List<String> urlList){
        this.urlList = urlList;
    }

    @Override
    public PooledObject<ModbusLink> makeObject() throws Exception {
        int factor = urlList.size();
        // 保证数组下标不越界
        int index = (int)atomic.incrementAndGet() % factor;
        String url = urlList.get(index);
        log.info("创建modbus连接：" + url);
        String[] split = url.split(":");
        ModbusLink link = new ModbusLink(split[0], Integer.valueOf(split[1]));
        return new DefaultPooledObject<>(link);
    }

    @Override
    public void destroyObject(PooledObject<ModbusLink> pooledObject) throws Exception {
        pooledObject.markAbandoned();
        pooledObject.getObject().closeConnect();
    }

    @Override
    public boolean validateObject(PooledObject<ModbusLink> pooledObject) {
        return false;
    }

    @Override
    public void activateObject(PooledObject<ModbusLink> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<ModbusLink> pooledObject) throws Exception {

    }
}
