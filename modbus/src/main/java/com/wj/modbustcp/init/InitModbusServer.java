package com.wj.modbustcp.init;

import com.wj.modbustcp.config.ModbusProperties;
import com.wj.modbustcp.connect.MasterRepository;
import com.wj.modbustcp.connect.ModbusLink;
import com.wj.modbustcp.connect.ModbusLinkFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

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
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

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

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        return threadPoolTaskScheduler;
    }


    @Override
    public void afterPropertiesSet()  {
        // 开启定时任务间隔时间内获取实时数据
        Task helloTask = new Task();
        String corn = "0/2 * * * *  ?";
        //将任务交给任务调度器执行
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(helloTask, new CronTrigger(corn));

        //将任务包装成ScheduledFutureHolder
        ScheduledFutureHolder scheduledFutureHolder = new ScheduledFutureHolder();
        scheduledFutureHolder.setScheduledFuture(schedule);
        scheduledFutureHolder.setRunnableClass(helloTask.getClass());
        scheduledFutureHolder.setCorn(corn);

    }




}
