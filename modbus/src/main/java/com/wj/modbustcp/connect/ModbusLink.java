package com.wj.modbustcp.connect;

import com.serotonin.modbus4j.Modbus;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.msg.ModbusRequest;
import com.serotonin.modbus4j.msg.ModbusResponse;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.sero.util.queue.ByteQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * modbus链接
 * Created by IntelliJ IDEA
 * 这是一个平凡的Class
 *
 * @author lyy
 * @date 2021/3/26 14:20
 */
@Slf4j
public class ModbusLink {

    private final ModbusMaster master;

    public ModbusLink(String slaveHost, int slavePort) throws ModbusInitException {
        IpParameters parameters = new IpParameters();
        parameters.setHost(slaveHost);
        parameters.setPort(slavePort);
        parameters.setEncapsulated(false);
        ModbusFactory modbusFactory = new ModbusFactory();
        master = modbusFactory.createTcpMaster(parameters, true);
        master.setTimeout(30000);
        master.setRetries(5);
        master.init();
    }

    public void closeConnect() {
        this.master.destroy();
    }

    public ByteQueue send(int slaveId, int readStartOff, int readLength) throws ModbusTransportException {
        ModbusRequest modbusRequest = null;
        modbusRequest = new ReadInputRegistersRequest(slaveId, readStartOff, readLength);
        ModbusResponse modbusResponse = null;
        modbusResponse = this.master.send(modbusRequest);

        ByteQueue byteQueue = new ByteQueue(1024);
        modbusResponse.write(byteQueue);
        return byteQueue;
    }

    public static void main(String[] args) throws ModbusInitException, ModbusTransportException {
        ModbusLink modbusLink = new ModbusLink("192.168.1.88", 502);
        ByteQueue send = modbusLink.send(1, 1000, 3);
        System.out.println(send.toString());
    }


}
