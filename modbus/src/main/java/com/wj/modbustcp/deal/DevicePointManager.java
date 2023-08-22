package com.wj.modbustcp.deal;


import com.serotonin.modbus4j.sero.util.queue.ByteQueue;
import com.wj.modbustcp.config.ModbusDeviceProperties;
import com.wj.modbustcp.constant.DealWayEnum;
import com.wj.modbustcp.util.HexStringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备点位管理
 *
 * @author zzh
 * @date 2022/11/16
 */
@Component
@ConditionalOnBean(ModbusDeviceProperties.class)
public class DevicePointManager {

    private final ModbusDeviceProperties modbusDeviceProperties;



    /**
     * 正常返回 DTU_CMD 指令代码
     * hexString 正常返回数据的指令代码，格式为十六进制
     */
    private static final String RETURN_DTU_CMD = "81";

    /**
     * 异常通讯返回 DTU_CMD 指令代码
     * hexString 读取传感器无返回的情况，格式为十六进制
     */
    private static final String EXCEPTION_DTU_CMD = "C1";

    private static final String SPLIT = "/";


    /**
     * DevicePointManager Constructor
     */
    public DevicePointManager(ModbusDeviceProperties modbusDeviceProperties) {
        this.modbusDeviceProperties = modbusDeviceProperties;
    }

    @PostConstruct
    public void inspectionParam() {
            // 1、proType, num, digit, bit 的长度是否一致，且不能为空。
            boolean isEqual = modbusDeviceProperties.getProType().length == modbusDeviceProperties.getBit().length &&
                    modbusDeviceProperties.getBit().length == modbusDeviceProperties.getNum().length &&
                    modbusDeviceProperties.getNum().length == modbusDeviceProperties.getDigit().length;
            if (!isEqual) {
                throw new RuntimeException("all the size of proType, bit, num, digit are unequal: "
                        + Arrays.toString(modbusDeviceProperties.getTask()));
            }

        changeToDevicePointPoint();
    }

    private void changeToDevicePointPoint() {
//        List<DevicePoint> points = modbusDeviceProperties.getPoint();
//        Map<String, List<DevicePoint>> map = points.stream().collect(Collectors.groupingBy(DevicePoint::getDevEui));
//        for (Map.Entry<String, List<DevicePoint>> entry : map.entrySet()) {
//            String s = entry.getKey();
//            List<DevicePoint> devicePoints = entry.getValue();
//            String key = s + SPLIT;
//            for (DevicePoint devicePoint : devicePoints) {
//                String[] deviceIds = devicePoint.getDeviceId();
//                List<String>[] commands = devicePoint.getCommands();
//                for (int i = 0; i < commands.length; i++) {
//                    for (int j = 0; j < commands[i].size(); j++) {
//                        DeviceComPoint deviceComPoint = new DeviceComPoint();
//                        deviceComPoint.setDevEui(devicePoint.getDevEui());
//                        deviceComPoint.setDeviceId(deviceIds[i]);
//                        deviceComPoint.setDeviceType(devicePoint.getDeviceType());
//                        deviceComPoint.setTaskId(commands[i].get(j));
//                        DeviceCommand deviceCommand = devEuiMap.get(key + commands[i].get(j));
//                        if (deviceCommand == null) {
//                            throw new ParamIncorrectException(" no such the command of " + s);
//                        }
//                        deviceComPoint.setDigit(deviceCommand.getDigit());
//                        deviceComPoint.setProType(deviceCommand.getProType());
//                        deviceComPoint.setNum(deviceCommand.getNum());
//                        deviceComPoint.setBit(deviceCommand.getBit());
//                        deviceComPoint.setUnit(deviceCommand.getUnit());
//                        deviceComPoint.setType(deviceCommand.getType());
//                        deviceComPoint.setMark(deviceCommand.getMark());
//                        deviceComPoint.setDealWay(deviceCommand.getDealWay());
//                        deviceEuiMap.put(key + commands[i].get(j), deviceComPoint);
//                    }
//                }
//            }
//        }
    }

    public void getDevicePoint(String devEui, String hexString, boolean isReturnParam) {
//        String headCode = hexString.substring(0, 2);
//        // 是否为返回数据
//        String code = hexString.substring(10, 12);
//        int codeInt = Integer.parseInt(code, 16);
//        if (headCode.equals(RETURN_DTU_CMD)) {
//            // 读取实时数据的指令
//            if (deviceEuiMap.containsKey(devEui + SPLIT + codeInt)) {
//                // 是否返回全部参数
//                if (isReturnParam) {
//                    return getDeviceProTypeList(devEui, String.valueOf(codeInt));
//                }
//                return deviceEuiMap.get(devEui + SPLIT + codeInt);
//            } else {
//                if (deviceEuiMap.containsKey(SPLIT + codeInt)) {
//                    if (isReturnParam) {
//                        return getDeviceProTypeList("", String.valueOf(codeInt));
//                    }
//                    return deviceEuiMap.get(SPLIT + codeInt);
//                }
//            }
//        } else if (headCode.equals(EXCEPTION_DTU_CMD)) {
//            // 错误指令
//            return null;
//        }
//        return null;
    }

    public void getDeviceProTypeList(String devEui, String codeInt) {
//        List<DevicePoint> points = modbusDeviceProperties.getPoint();
//        Map<String, List<DevicePoint>> map = points.stream().collect(Collectors.groupingBy(DevicePoint::getDevEui));
//        List<DevicePoint> devicePoints = map.get(devEui);
//        List<String> taskList = new ArrayList<>();
//        for (DevicePoint devicePoint : devicePoints) {
//            List<String>[] commands = devicePoint.getCommands();
//            for (int i = 0; i < commands.length; i++) {
//                boolean contains = commands[i].contains(codeInt);
//                if (contains) {
//                    taskList = commands[i];
//                    break;
//                }
//            }
//        }
//        DeviceComPoint paramPoint = new DeviceComPoint();
//        List<Integer> proTypeList = new ArrayList<>();
//        List<Integer> numList = new ArrayList<>();
//        for (String task : taskList) {
//            DeviceCommand deviceCommand = devEuiMap.get(devEui + SPLIT + task);
//            List<Integer> proTypes = Arrays.stream(deviceCommand.getProType()).boxed().collect(Collectors.toList());
//            List<Integer> nums = Arrays.stream(deviceCommand.getNum()).boxed().collect(Collectors.toList());
//            proTypeList.addAll(proTypes);
//            numList.addAll(nums);
//        }
//        paramPoint.setProType(proTypeList.stream().mapToInt(Integer::valueOf).toArray());
//        paramPoint.setNum(numList.stream().mapToInt(Integer::valueOf).toArray());
//        return paramPoint;
    }

    public double[] getResult(String hexString) {
        int[] digit = modbusDeviceProperties.getDigit();
        int[] bits = modbusDeviceProperties.getBit();
        String deviceId = "mims_01";
        double[] result = new double[digit.length];
        int dataLength = HexStringUtil.decodeIntValue(hexString.substring(4, 6))*2;
        String dataHexString = hexString.substring(6, 6 + dataLength);
//        if (StorageUnitEnum.BIT.equals(deviceCommand.getUnit())) {
//            HexStringUtil.decodeBit(dataHexString, bits, result);
//        } else {
            HexStringUtil.decode(dataHexString, bits, result);
//        }
        if (modbusDeviceProperties.getDealWay() != null) {
            DealWayEnum[] dealWay = modbusDeviceProperties.getDealWay();
            for (int i = 0; i < result.length; i++) {
                String substring = dataHexString.substring(0, bits[i]);
                dataHexString = dataHexString.substring(bits[i]);
                if (dealWay[i] != null && dealWay[i].getFunction() != null) {
                    result[i] = dealWay[i].execute(substring);
                    result[i] = result[i] / digit[i];
                } else {
                    result[i] = result[i] / digit[i];
                }
            }
            dealCoefficientValue(dealWay, result);
        } else {
            for (int i = 0; i < result.length; i++) {
                    result[i] = result[i] / digit[i];

            }
        }
        return result;
    }

    /**
     * 处理系数比的情况
     *
     * @param dealWays 处理类型
     * @param result   值数据
     */
    private void dealCoefficientValue(DealWayEnum[] dealWays, double[] result) {
        for (int j = 0, waysLength = dealWays.length; j < waysLength; j++) {
            String[] dealWaySplit = dealWays[j].name().split("_");
            if (dealWaySplit.length == 2 && DealWayEnum.COEFFICIENT.equals(dealWaySplit[0])) {
                String itemName = DealWayEnum.TERM + "_" + dealWaySplit[1];
                for (int i = 0, dealWaysLength = dealWays.length; i < dealWaysLength; i++) {
                    if (dealWays[i].name().equals(itemName)) {
                        result[i] = result[j] * result[i];
                    }
                }
            }
        }
    }
}
