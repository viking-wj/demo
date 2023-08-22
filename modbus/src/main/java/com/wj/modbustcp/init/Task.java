package com.wj.modbustcp.init;

import com.serotonin.modbus4j.sero.util.queue.ByteQueue;
import com.wj.modbustcp.config.ModbusProperties;
import com.wj.modbustcp.connect.MasterRepository;
import com.wj.modbustcp.connect.ModbusLink;
import com.wj.modbustcp.deal.DevicePointManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/22 13:49
 */
@Slf4j
public class Task implements Runnable {

    @Resource
    private MasterRepository masterRepository;

    @Resource
    private DevicePointManager devicePointManager;

    @Resource
    private ModbusProperties modbusProperties;

    @Override
    public void run() {
        ModbusLink modbusLink = null;
        try {
            modbusLink = masterRepository.borrowObject();
            ByteQueue send = modbusLink.send(modbusProperties.getSlaveId(),
                    modbusProperties.getStartAddress(), modbusProperties.getRegisterNumber());
            log.info("实时数据：" + send);
            // Convert ByteQueue to byte array
            byte[] byteArray = send.peekAll();
            // 转为标准16进制字符
            String hexString = bytesToHexString(byteArray);
            double[] result = devicePointManager.getResult(hexString);
            System.out.println(Arrays.toString(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != modbusLink) {
                masterRepository.returnObject(modbusLink);
            }
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to its hexadecimal representation
            String hex = String.format("%02X", b & 0xFF);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
