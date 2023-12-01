package com.wj.opc.pool;

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
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

/**
 * @author w
 * {@code @time:} 16:02
 * Description: OPC 服务端命名空间
 */
@Slf4j
public class OpcNamespace extends ManagedNamespaceWithLifecycle {

    public static final String NAMESPACE_URI = "urn:eclipse:milo:opc";

    private volatile Thread eventThread;
    private volatile boolean keepPostingEvents = true;

    private final DataTypeDictionaryManager dictionaryManager;

    private final SubscriptionModel subscriptionModel;


    public OpcNamespace(OpcUaServer server) {
        super(server, NAMESPACE_URI);
        subscriptionModel = new SubscriptionModel(server, this);
        dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), NAMESPACE_URI);

        getLifecycleManager().addLifecycle(dictionaryManager);
        getLifecycleManager().addLifecycle(subscriptionModel);
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


    private void startBogusEventNotifier() {
        // Set the EventNotifier bit on Server Node for Events.
        UaNode serverNode = getServer()
                .getAddressSpaceManager()
                .getManagedNode(Identifiers.Server)
                .orElse(null);

        if (serverNode instanceof ServerTypeNode) {
            ((ServerTypeNode) serverNode).setEventNotifier(ubyte(1));

            // Post a bogus Event every couple seconds
            eventThread = new Thread(() -> {
                while (keepPostingEvents) {
                    try {
                        BaseEventTypeNode eventNode = getServer().getEventFactory().createEvent(
                                newNodeId(UUID.randomUUID()),
                                Identifiers.BaseEventType
                        );
                        eventNode.setBrowseName(new QualifiedName(1, "foo"));
                        eventNode.setDisplayName(LocalizedText.english("foo"));
                        eventNode.setEventId(ByteString.of(new byte[]{0, 1, 2, 3}));
                        eventNode.setEventType(Identifiers.BaseEventType);
                        eventNode.setSourceNode(serverNode.getNodeId());
                        eventNode.setSourceName(serverNode.getDisplayName().getText());
                        eventNode.setTime(DateTime.now());
                        eventNode.setReceiveTime(DateTime.NULL_VALUE);
                        eventNode.setMessage(LocalizedText.english("event message!"));
                        eventNode.setSeverity(ushort(2));
                        //noinspection UnstableApiUsage
                        getServer().getEventBus().post(eventNode);
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

    /**
     * 方法描述: 创建节点方法
     *
     * @param keys 节点名称集合
     * @return
     * @throws
     */
    public void addNodes(Set<String> keys) {
        // Create a "opc" folder and add it to the node manager
        NodeId folderNodeId = newNodeId("opc");
        UaFolderNode folderNode = new UaFolderNode(
                getNodeContext(),
                folderNodeId,
                newQualifiedName("opc"),
                LocalizedText.english("opc")
        );
        getNodeManager().addNode(folderNode);
        // Make sure our new folder shows up under the server's Objects folder.
        folderNode.addReference(new Reference(
                folderNode.getNodeId(),
                Identifiers.Organizes,
                Identifiers.ObjectsFolder.expanded(),
                false
        ));
        // 增加属性节点
        for (String key : keys) {
            NodeId typeId = Identifiers.Double;
            Variant variant = new Variant(2d);
            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                    .setNodeId(newNodeId(key))
                    .setAccessLevel(AccessLevel.READ_WRITE)
                    .setUserAccessLevel(AccessLevel.READ_WRITE)
                    .setBrowseName(newQualifiedName(key))
                    .setDisplayName(LocalizedText.english(key))
                    .setDataType(typeId)
                    .setTypeDefinition(Identifiers.BaseDataVariableType)
                    .build();
            getNodeManager().addNode(node);
            folderNode.addOrganizes(node);
        }
        // 方法、事件
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
