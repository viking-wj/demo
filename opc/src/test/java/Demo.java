import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.methods.UaMethod;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedDataItem;
import org.eclipse.milo.opcua.sdk.client.subscriptions.ManagedSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author w
 * @date: 2023/11/22
 * {@code @time:} 14:52
 * Description:
 */
public class Demo {
    private final static String endPointUrl = "opc.tcp://127.0.0.1:12686";

    /**
     * 创建OPC UA客户端
     *
     * @return
     * @throws Exception
     */
    private static OpcUaClient createClient() throws Exception {
        //opc ua服务端地址

        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
        Files.createDirectories(securityTempDir);
        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        return OpcUaClient.create(endPointUrl,
                endpoints ->
                        endpoints.stream()
                                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                                .setApplicationUri("urn:eclipse:milo:examples:client")
                                //访问方式
                                .setIdentityProvider(new AnonymousProvider())
                                .setRequestTimeout(UInteger.valueOf(500))
                                .build()
        );
    }

    /**
     * 遍历树形节点
     *
     * @param client OPC UA客户端
     * @param uaNode 节点
     * @throws Exception
     */
    private static void browseNode(OpcUaClient client, UaNode uaNode) throws Exception {

        List<? extends UaNode> nodes;
        if (uaNode == null) {
            nodes = client.getAddressSpace().browseNodes(new NodeId(2, "QIWU"));
//            nodes = client.getAddressSpace().browseNodes(Identifiers.RootFolder);
        } else {
            nodes = client.getAddressSpace().browseNodes(uaNode);
        }
        for (UaNode nd : nodes) {
            //排除系统行性节点，这些系统性节点名称一般都是以"_"开头
            if (Objects.requireNonNull(nd.getBrowseName().getName()).contains("_")) {
                continue;
            }
            System.out.println("Node= " + nd.getBrowseName().getName() + "," + nd.getNodeId());
            browseNode(client, nd);
        }
    }

    /**
     * 读取节点数据
     *
     * @param client OPC UA客户端
     * @throws Exception
     */
    private static void readNode(OpcUaClient client, NodeId nodeId) throws Exception {
        int namespaceIndex = 2;
        String identifier = "Modbus_TCP.气象站.大气压强";
        //节点
//        NodeId nodeId = new NodeId(namespaceIndex, identifier);
        //读取节点数据
        DataValue value = client.readValue(0.0, TimestampsToReturn.Neither, nodeId).get();
        //标识符
        identifier = String.valueOf(nodeId.getIdentifier());
        System.out.println(identifier + ": " + String.valueOf(value.getValue().getValue()));
    }

    /**
     * 写入节点数据
     *
     * @param client
     * @throws Exception
     */
    private static void writeNodeValue(OpcUaClient client, NodeId nodeId, Short value) throws Exception {
        //节点
//        NodeId nodeId = new NodeId(2, "通道 1.设备 1.标记 4");
//        Short i = 3;
        //创建数据对象,此处的数据对象一定要定义类型，不然会出现类型错误，导致无法写入
        DataValue nowValue = new DataValue(new Variant(value), null, null);
        //写入节点数据
        StatusCode statusCode = client.writeValue(nodeId, nowValue).join();
        System.out.println("结果：" + statusCode.isGood());
    }

    /**
     * 订阅(单个)
     *
     * @param client
     * @throws Exception
     */
    private static void subscribe(OpcUaClient client,NodeId nodeId) throws Exception {
        AtomicInteger a = new AtomicInteger();
        //创建发布间隔1000ms的订阅对象
        client
                .getSubscriptionManager()
                .createSubscription(1000.0)
                .thenAccept(t -> {
                    //节点
//                    NodeId nodeId = new NodeId(2, "通道 1.设备 1.标记 4");
                    ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
                    //创建监控的参数
                    MonitoringParameters parameters = new MonitoringParameters(UInteger.valueOf(a.getAndIncrement()), 1000.0, null, UInteger.valueOf(10), true);
                    //创建监控项请求
                    //该请求最后用于创建订阅。
                    MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);
                    List<MonitoredItemCreateRequest> requests = new ArrayList<>();
                    requests.add(request);
                    //创建监控项，并且注册变量值改变时候的回调函数。
                    t.createMonitoredItems(
                            TimestampsToReturn.Both,
                            requests,
                            (item, id) -> item.setValueConsumer((it, val) -> {
                                System.out.println("nodeid :" + it.getReadValueId().getNodeId());
                                System.out.println("value :" + val.getValue().getValue());
                            })
                    );
                }).get();

        //持续订阅
        Thread.sleep(Long.MAX_VALUE);
    }
    /**
     * 批量订阅
     *
     * @param client
     * @throws Exception
     */
//    private static void managedSubscriptionEvent(OpcUaClient client) throws Exception {
//        final CountDownLatch eventLatch = new CountDownLatch(1);
//
//        //处理订阅业务
//        handlerNode(client);
//
//        //持续监听
//        eventLatch.await();
//    }

    /**
     * 处理订阅业务
     *
     * @param client OPC UA客户端
     */
    private static void handlerNode(OpcUaClient client) {
        try {
            //创建订阅
            ManagedSubscription subscription = ManagedSubscription.create(client);

            //你所需要订阅的key
            List<String> key = new ArrayList<>();
            key.add("Modbus_TCP.气象站.大气压强");
            key.add("Modbus_TCP.气象站.温度");
            key.add("Virtual.温湿度传感器.温度");
            key.add("Modbus_TCP.气象站.PM.PM10");

            List<NodeId> nodeIdList = new ArrayList<>();
            for (String s : key) {
                nodeIdList.add(new NodeId(2, s));
            }

            //监听
            List<ManagedDataItem> dataItemList = subscription.createDataItems(nodeIdList);
            for (ManagedDataItem managedDataItem : dataItemList) {
                managedDataItem.addDataValueListener((t) -> {
                    System.out.println(managedDataItem.getNodeId().getIdentifier().toString() +
                            ":" + t.getValue().getValue().toString());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义订阅监听
     */
    private static class CustomSubscriptionListener implements UaSubscriptionManager.SubscriptionListener {

        private OpcUaClient client;


        CustomSubscriptionListener(OpcUaClient client) {
            this.client = client;
        }

        public void onKeepAlive(UaSubscription subscription, DateTime publishTime) {
            System.out.println("onKeepAlive");
        }

        public void onStatusChanged(UaSubscription subscription, StatusCode status) {
            System.out.println("onStatusChanged");
        }

        public void onPublishFailure(UaException exception) {
            System.out.println("onPublishFailure");
        }

        public void onNotificationDataLost(UaSubscription subscription) {
            System.out.println("onNotificationDataLost");
        }

        /**
         * 重连时 尝试恢复之前的订阅失败时 会调用此方法
         *
         * @param uaSubscription 订阅
         * @param statusCode     状态
         */
        public void onSubscriptionTransferFailed(UaSubscription uaSubscription, StatusCode statusCode) {
            System.out.println("恢复订阅失败 需要重新订阅");
            //在回调方法中重新订阅
            handlerNode(client);
        }
    }

    /**
     * 批量订阅
     *
     * @param client
     * @throws Exception
     */
    private static void managedSubscriptionEvent(OpcUaClient client) throws Exception {
        final CountDownLatch eventLatch = new CountDownLatch(1);

        //添加订阅监听器，用于处理断线重连后的订阅问题
        client.getSubscriptionManager().addSubscriptionListener(new CustomSubscriptionListener(client));

        //处理订阅业务
        handlerNode(client);

        //持续监听
        eventLatch.await();
    }

    private static void fun(OpcUaClient client,Double value){
        try {
            UaObjectNode objectNode = client.getAddressSpace()
                    .getObjectNode(NodeId.parse("ns=2;s=QIWU"));

            UaMethod sqrtMethod = objectNode.getMethod("sqrt(x)");


            System.out.println("输入:"+value);
            Variant[] inputs = {new Variant(value)};
            Variant[] outputs = sqrtMethod.call(inputs);
            System.out.println("调用方法节点后输出"+  outputs[0].getValue());
        } catch (UaException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        OpcUaClient client = createClient();
        client.connect().get();
        // 扫描QIWU下所有节点
        System.out.println("QIWU文件节点下所有节点");
        browseNode(client, null);
        // 浏览节点
        NodeId nodeId = new NodeId(2, "QIWU.ScalarTypes.Int16");
        System.out.println("读取节点" + nodeId.getIdentifier() + "的数据");
        readNode(client, nodeId);
        // 调用服务器方法节点
        fun(client,16.0);
//        System.out.println("向节点" + nodeId.getIdentifier() + "写入数据32");
//        writeNodeValue(client, nodeId, (short) 32);

        // 持续写入
//        new Thread(()->{
//            while (true){
//                short i = Short.parseShort(String.valueOf((int)(Math.random() * 10)));
//                System.out.println("向节点" + nodeId.getIdentifier() + "写入数据"+i);
//                try {
//                    writeNodeValue(client, nodeId, i);
//                    Thread.sleep(10000);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }).start();
        System.out.println("订阅节点" + nodeId.getIdentifier() + "的数据");
        subscribe(client,nodeId);
//      managedSubscriptionEvent(client);
    }


}
