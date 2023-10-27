### Java实现modbus-tcp通信
#### 依赖引入
引入modbus4j依赖
```xml
    <dependency>
    <groupId>com.infiniteautomation</groupId>
    <artifactId>modbus4j</artifactId>
    <version>3.0.5</version>
    </dependency>
    <!--modbus4j所在仓库-->
    <repositories>
    <repository>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <id>ias-snapshots</id>
        <name>Infinite Automation Snapshot Repository</name>
        <url>https://maven.mangoautomation.net/repository/ias-snapshot/</url>
    </repository>
    <repository>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>ias-releases</id>
        <name>Infinite Automation Release Repository</name>
        <url>https://maven.mangoautomation.net/repository/ias-release/</url>
    </repository>
</repositories>
```
#### 连接建立
```java
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
```


### Apache Commons Pool对象池 GenericObjectPool
#### GenericObjectPoolConfig 参数设置

- maxTotal
最大连接数，默认值 DEFAULT_MAX_TOTAL = 8
- maxIdle
最大空闲连接数， 默认值 DEFAULT_MAX_IDLE = 8
- minIdle
最小空闲连接数， 默认值 DEFAULT_MIN_IDLE = 0
- testOnBorrow 
在从对象池获取对象时是否检测对象有效(true : 是) , 配置true会降低性能；默认值 DEFAULT_TEST_ON_BORROW = false
- maxWaitMillis 当连接池资源用尽后，调用者获取连接时的最大等待时间（单位 ：毫秒）；默认值 DEFAULT_MAX_WAIT_MILLIS = -1L， 永不超时。
- lifo提供了后进先出(LIFO)与先进先出(FIFO)两种行为模式的池；默认 DEFAULT_LIFO = true， 当池中有空闲可用的对象时，调用borrowObject方法会返回最近（后进）的实例。（默认）
- fairness当从池中获取资源或者将资源还回池中时,是否使用；java.util.concurrent.locks.ReentrantLock.ReentrantLock 的公平锁机制。默认DEFAULT_FAIRNESS = false（默认）
- blockWhenExhausted当这个值为true的时候，maxWaitMillis参数才能生效。为false的时候，当连接池没资源，则立马抛异常。默认为true.
- testOnReturn 默认值 false； 设置为 true 时，当将资源返还个资源池时候，调用factory.validateObject()方法，验证资源的有效性,如果无效，则调用 factory.destroyObject()方法.

#### 实现步骤
1. makeObject 创建对象的具体实现
2. borrowObject 获取对象池中的对象简单而言就是去LinkedList中获取一个对象，如果不存在的话，要调用构造方法中第一个参数Factory工厂类的makeObject()方法去创建一个对象再获取，获取到对象后要调用validateObject方法判断该对象是否是可用的，如果是可用的才拿去使用。LinkedList容器减一。
3. validateObject方法判断该对象是否是可用的。
4. returnObject 先调用validateObject方法判断该对象是否是可用的，如果可用则归还到池中，LinkedList容器加一，如果是不可以的则则调用destroyObject方法进行销毁

#### 读取保留寄存器数据
```java
 ModbusRequest modbusRequest = null;
        modbusRequest = new ReadInputRegistersRequest(slaveId,readStartOff,readLength);
        ModbusResponse modbusResponse = null;
        modbusResponse = this.master.send(modbusRequest);
        ByteQueue byteQueue = new ByteQueue(1024);
        modbusResponse.write(byteQueue);
        return byteQueue;
```

#### 字节数组值转换
- 一条命令可以读出一个或多个数值
- 数值可能需要进行转换（如十六进制转十进制，乘以0.01等）

#### 知识链接  
[Modbus TCP](https://www.cnblogs.com/wenhao-Web/p/12993675.html)  
[GenericObjectPool 对象池](https://www.jianshu.com/p/2037d6c56b5f)

#### 优化
- 设备信息配置从配置文件优化到数据库中（考虑大量设备查询等待问题）
- 定时下发指令读取保留寄存器是否可以异步处理，同步存在下发失败、没有回应的问题
- 集成第三方边缘网关如Neuron则可以实现主动向emqx推送数据

#### 缺点
- modbus TCP网关单独只能作为从站使用，即不能主动向平台推送数据，只能由平台主动轮询获取网关数据。
- 设备状态无法立刻获取，只有当轮询到该设备获取数据超时才能判断


#### 优化ing
1. 从数据库获取配置参数（线程池配置、连接池配置、轮询定时配置）
2. 获取网关设备配置（网关id（IP以及端口号）、设备id、从机id、寄存器起始地址、寄存器个数）
3. 建立连接，轮询设备读取数据进行解析。
4. 数据入库或推送至数据库。
