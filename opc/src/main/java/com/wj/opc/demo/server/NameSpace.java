package com.wj.opc.demo.server;

import com.wj.opc.examples.server.AttributeLoggingFilter;
import com.wj.opc.examples.server.methods.GenerateEventMethod;
import com.wj.opc.examples.server.methods.SqrtMethod;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.dtd.DataTypeDictionaryManager;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.BaseEventTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.*;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;

import java.util.List;
import java.util.UUID;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.*;

/**
 * @author w
 * {@code @time:} 17:41
 * Description: server namespace
 * init
 * create and ad nodes
 * start or shutdown
 */
@Slf4j
public class NameSpace extends ManagedNamespaceWithLifecycle {

    private static final Object[][] STATIC_SCALAR_NODES = new Object[][]{
            {"Boolean", Identifiers.Boolean, new Variant(false)},
            {"Byte", Identifiers.Byte, new Variant(ubyte(0x00))},
            {"SByte", Identifiers.SByte, new Variant((byte) 0x00)},
            {"Integer", Identifiers.Integer, new Variant(32)},
            {"Int16", Identifiers.Int16, new Variant((short) 16)},
            {"Int32", Identifiers.Int32, new Variant(32)},
            {"Int64", Identifiers.Int64, new Variant(64L)},
            {"UInteger", Identifiers.UInteger, new Variant(uint(32))},
            {"UInt16", Identifiers.UInt16, new Variant(ushort(16))},
            {"UInt32", Identifiers.UInt32, new Variant(uint(32))},
            {"UInt64", Identifiers.UInt64, new Variant(ulong(64L))},
            {"Float", Identifiers.Float, new Variant(3.14f)},
            {"Double", Identifiers.Double, new Variant(3.14d)},
            {"String", Identifiers.String, new Variant("string value")},
            {"DateTime", Identifiers.DateTime, new Variant(DateTime.now())},
            {"Guid", Identifiers.Guid, new Variant(UUID.randomUUID())},
            {"ByteString", Identifiers.ByteString, new Variant(new ByteString(new byte[]{0x01, 0x02, 0x03, 0x04}))},
            {"XmlElement", Identifiers.XmlElement, new Variant(new XmlElement("<a>hello</a>"))},
            {"LocalizedText", Identifiers.LocalizedText, new Variant(LocalizedText.english("localized text"))},
            {"QualifiedName", Identifiers.QualifiedName, new Variant(new QualifiedName(1234, "defg"))},
            {"NodeId", Identifiers.NodeId, new Variant(new NodeId(1234, "abcd"))},
            {"Variant", Identifiers.BaseDataType, new Variant(32)},
            {"Duration", Identifiers.Duration, new Variant(1.0)},
            {"UtcTime", Identifiers.UtcTime, new Variant(DateTime.now())},
    };
    private final SubscriptionModel subscriptionModel;
    private volatile Thread eventThread;
    private volatile boolean keepPostingEvents = true;

    public static final String NAMESPACE_URI = "urn:keenyoda:milo:qiwu";

    private final DataTypeDictionaryManager dictionaryManager;

    public NameSpace(OpcUaServer server) {
        super(server, NAMESPACE_URI);

        subscriptionModel = new SubscriptionModel(server, this);
        dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), NAMESPACE_URI);

        getLifecycleManager().addLifecycle(dictionaryManager);
        getLifecycleManager().addLifecycle(subscriptionModel);

        getLifecycleManager().addStartupTask(this::createAndAddNodes);

        getLifecycleManager().addLifecycle(new Lifecycle() {
            @Override
            public void startup() {
                startBogusEventNotifier();
            }

            @Override
            public void shutdown() {
                try {
                    keepPostingEvents = false;
                    eventThread.interrupt();
                    eventThread.join();
                } catch (InterruptedException ignored) {
                    // ignored
                }
            }
        });
    }

    private void createAndAddNodes()  {
        // 创建根文件节点
        NodeId folderNodeId = newNodeId("QIWU");
        UaFolderNode folderNode = new UaFolderNode(
                getNodeContext(),
                folderNodeId,
                newQualifiedName("QIWU"),
                LocalizedText.english("QIWU")
        );
        getNodeManager().addNode(folderNode);
        // 确保新创建的文件夹在服务器的Objects文件夹下显示
        folderNode.addReference(new Reference(
                folderNode.getNodeId(),
                Identifiers.Organizes,
                Identifiers.ObjectsFolder.expanded(),
                false
        ));

        //添加数据节点
        addVariableNodes(folderNode);
        // 方法
        addSqrtMethod(folderNode);
        // 事件
        addGenerateEventMethod(folderNode);

        // 添加自定义枚举类型的变量节点
        // 添加自定义结构类型变量节点
        // 注册自定义的联合类型
        // 添加自定义对象类型和对象实例节点到文件夹节点
    }

    private void addVariableNodes(UaFolderNode rootNode) {
        // 数组节点
//        addArrayNodes(rootNode);
        // 标量节点
        addScalarNodes(rootNode);

    }

    private void addScalarNodes(UaFolderNode rootNode) {
        UaFolderNode scalarTypesFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("QIWU/ScalarTypes"),
                newQualifiedName("ScalarTypes"),
                LocalizedText.english("ScalarTypes")
        );

        getNodeManager().addNode(scalarTypesFolder);
        rootNode.addOrganizes(scalarTypesFolder);

        for (Object[] os : STATIC_SCALAR_NODES) {
            String name = (String) os[0];
            NodeId typeId = (NodeId) os[1];
            Variant variant = (Variant) os[2];

            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                    .setNodeId(newNodeId("QIWU/ScalarTypes/" + name))
                    .setAccessLevel(AccessLevel.READ_WRITE)
                    .setUserAccessLevel(AccessLevel.READ_WRITE)
                    .setBrowseName(newQualifiedName(name))
                    .setDisplayName(LocalizedText.english(name))
                    .setDataType(typeId)
                    .setTypeDefinition(Identifiers.BaseDataVariableType)
                    .build();

            // 网关.设备.监测属性.num
            node.setNodeId(newNodeId("QIWU.ScalarTypes." + name));
            node.setValue(new DataValue(variant));

            node.getFilterChain().addLast(new AttributeLoggingFilter(AttributeId.Value::equals));

            getNodeManager().addNode(node);
            scalarTypesFolder.addOrganizes(node);
        }
    }

    private void addGenerateEventMethod(UaFolderNode folderNode) {
        UaMethodNode methodNode = UaMethodNode.builder(getNodeContext())
                .setNodeId(newNodeId("QIWU/generateEvent(eventTypeId)"))
                .setBrowseName(newQualifiedName("generateEvent(eventTypeId)"))
                .setDisplayName(new LocalizedText(null, "generateEvent(eventTypeId)"))
                .setDescription(
                        LocalizedText.english("Generate an Event with the TypeDefinition indicated by eventTypeId."))
                .build();

        GenerateEventMethod generateEventMethod = new GenerateEventMethod(methodNode);
        methodNode.setInputArguments(generateEventMethod.getInputArguments());
        methodNode.setOutputArguments(generateEventMethod.getOutputArguments());
        methodNode.setInvocationHandler(generateEventMethod);

        getNodeManager().addNode(methodNode);

        methodNode.addReference(new Reference(
                methodNode.getNodeId(),
                Identifiers.HasComponent,
                folderNode.getNodeId().expanded(),
                false
        ));
    }
    private void addSqrtMethod(UaFolderNode folderNode) {
        UaMethodNode methodNode = UaMethodNode.builder(getNodeContext())
                .setNodeId(newNodeId("QIWU/sqrt(x)"))
                .setBrowseName(newQualifiedName("sqrt(x)"))
                .setDisplayName(new LocalizedText(null, "sqrt(x)"))
                .setDescription(
                        LocalizedText.english("Returns the correctly rounded positive square root of a double value."))
                .build();

        SqrtMethod sqrtMethod = new SqrtMethod(methodNode);
        methodNode.setInputArguments(sqrtMethod.getInputArguments());
        methodNode.setOutputArguments(sqrtMethod.getOutputArguments());
        methodNode.setInvocationHandler(sqrtMethod);

        getNodeManager().addNode(methodNode);

        methodNode.addReference(new Reference(
                methodNode.getNodeId(),
                Identifiers.HasComponent,
                folderNode.getNodeId().expanded(),
                false
        ));
    }
    private void startBogusEventNotifier() {
        // 获取服务器节点
        UaNode serverNode = getServer()
                .getAddressSpaceManager()
                .getManagedNode(Identifiers.Server)
                .orElse(null);

        // 设置事件通知器位 标识服务器支持事件
        if (serverNode instanceof ServerTypeNode) {
            ((ServerTypeNode) serverNode).setEventNotifier(ubyte(1));

            // 创建并发布模拟事件
            eventThread = new Thread(() -> {
                while (keepPostingEvents) {
                    try {

                        BaseEventTypeNode eventNode = getServer().getEventFactory().createEvent(
                                newNodeId(UUID.randomUUID()),
                                Identifiers.BaseEventType
                        );

                        // 设置浏览名 namespaceindex
                        eventNode.setBrowseName(new QualifiedName(1, "foo"));
                        // 设置显示名
                        eventNode.setDisplayName(LocalizedText.english("foo"));
                        // 设置事件ID
                        eventNode.setEventId(ByteString.of(new byte[]{0, 1, 2, 3}));
                        // 设置事件类型
                        eventNode.setEventType(Identifiers.BaseEventType);
                        // 设置事件源节点和源名称
                        eventNode.setSourceNode(serverNode.getNodeId());
                        eventNode.setSourceName(serverNode.getDisplayName().getText());
                        // 设置事件时间和接收时间
                        eventNode.setTime(DateTime.now());
                        eventNode.setReceiveTime(DateTime.NULL_VALUE);
                        // 设置事件消息严重性
                        eventNode.setMessage(LocalizedText.english("event message!"));
                        eventNode.setSeverity(ushort(2));

                        // 将创建的事件发布到事件总线
                        getServer().getEventBus().post(eventNode);
                        // 删除事件
                        eventNode.delete();
                    } catch (Throwable e) {
                        log.error("Error creating EventNode: {}", e.getMessage(), e);
                    }

                    try {
                        //noinspection BusyWait
                        Thread.sleep(2_000);
                    } catch (InterruptedException ignored) {
                        // ignored
                    }
                }
            }, "bogus-event-poster");

            eventThread.start();
        }
    }




    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }
}
