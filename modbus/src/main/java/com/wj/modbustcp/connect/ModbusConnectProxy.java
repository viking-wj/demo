package com.wj.modbustcp.connect;


import org.springframework.stereotype.Component;


/**
 * modbus通讯协议连接代理
 * Created by IntelliJ IDEA
 * connect proxy
 *
 * @author lyy
 * @date 2021/3/26 11:53
 */
@Component
public class ModbusConnectProxy  {

//    @Autowired
//    private ModbusGlobalContext context;
//
//    @Autowired
//    private ConfigDataHolder dataHolder;
//
//    public ByteQueue query(ModbusLiftConfig config) throws Exception{
//        MasterRepository masterRepository = context.getSource(config);
//        ModbusLink modbusLink = masterRepository.borrowObject();
//        masterRepository.returnObject(modbusLink);
//        return modbusLink.send(config.getSlaveId(),config.getAddressStart(),config.getAddressLength());
//    }
//
//    public ByteQueue queryAllDeviceData() throws Exception {
//        MasterRepository globalRepository = context.getGlobalRepository();
//        ModbusLink modbusLink = globalRepository.borrowObject();
//        try{
//            ByteQueue result = modbusLink.send(dataHolder.getSlaveId(), dataHolder.getAddressStart(), dataHolder.getDeviceCount());
//            globalRepository.returnObject(modbusLink);
//            return result;
//        }catch (Exception e){
//            System.out.println("queryAllDeviceData发生异常");
//            modbusLink.closeConnect();
//            e.printStackTrace();
//            return null;
//        }
//    }

}
