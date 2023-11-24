
OPC（OLE for Process Control）是一组标准和规范，旨在促进工业自动化中不同
设备和系统之间的互操作性。它最初是由微软提出的，后来成为一个开放的行业标准。
OPC 的目标是简化和标准化在工业自动化系统中进行数据交换的过程。
#### OPC协议规范
DA（Data Access）规范：实时数据。

A&E（Alarm and Event）规范：报警和事件数据。

HDA（History Data Acess）规范：历史数据；

**UA（Unified Architecture）规范：OPC UA 是一个更现代、跨平台、独立于
厂商和网络的规范。与 OPC Classic 不同，OPC UA 不依赖于 Microsoft 的 
COM/DCOM 技术，而是使用独立的协议。它提供了更强大的安全性、可扩展性和灵活性，
支持更广泛的数据模型和信息建。**



#### OPC Server

**KEPServerEX**  
KEPServerEX 是一款由 Kepware 公司开发的工业自动化数据通信平台。
它是一个灵活、可扩展的软件，用于连接、管理和通信不同供应商的工业设备、
控制系统和软件应用程序。KEPServerEX 提供了多种通信协议的支持，
包括 OPC（OLE for Process Control）、Modbus、SNMP、DNP3、OPC UA 等，
使其能够与各种设备和系统进行集成。  
首页：https://www.ptc.com/en/products/kepware/kepserverex

**添加通道（网关）**
![create_channel .png](src%2Fmain%2Fresources%2Fimg%2Fcreate_channel%20.png)
1. 选择协议（Simulator为虚拟设备）
![img.png](src/main/resources/img/selection_protocol.png)
2. 设置网关id
![img.png](src/main/resources/img/gatway_id.png)
3. 进行相应的配置（如modbus则需要配置slaveId，寄存器起始地址等）
![img.png](src/main/resources/img/setting.png)
**添加设备**
![img.png](src/main/resources/img/device_001.png)
4. 设置设备id
![img.png](src/main/resources/img/device_id.png)
5. 设备设置
![img.png](src/main/resources/img/device_setting.png)
**添加监测点**
![img.png](src/main/resources/img/add_protype.png)
填写对应配置
![img.png](src/main/resources/img/ptorype_setting.png)
#### OPC库

**milo（star 1k）**

github:https://github.com/eclipse/milo

首页：https://projects.eclipse.org/projects/iot.milo

```xml
<!--引入milo客户端依赖-->
        <dependency>
            <groupId>org.eclipse.milo</groupId>
            <artifactId>sdk-client</artifactId>
            <version>0.6.8</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.milo</groupId>
            <artifactId>sdk-server</artifactId>
            <version>0.6.8</version>
        </dependency>
        <!--数字证书解析依赖-->
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcpkix-jdk15on</artifactId>
            <version>1.70</version>
        </dependency>
```

OPC常用开源库：https://blog.csdn.net/whahu1989/article/details/106452683

#### 步骤

1. 与服务端建立连接（对象池）
2. 遍历树形节点
3. 读取节点数据
4. 写入节点数据
5. 订阅或批量订阅
6. 断线重订阅

参考：https://blog.csdn.net/u013810234/article/details/130175531
