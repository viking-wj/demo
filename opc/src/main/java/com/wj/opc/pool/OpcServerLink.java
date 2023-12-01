package com.wj.opc.pool;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;

import java.security.Security;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.*;

/**
 * @author w
 * {@code @time:} 8:45
 * Description: OPC 服务端代码
 */
@Slf4j
public class OpcServerLink {

    private final OpcUaServer opcUaServer;

    // 初始化

    public OpcServerLink(int port) {
        // 启用安全提供程序
        Security.addProvider(new BouncyCastleProvider());

        // 创建OPC UA 服务器配置
        // 启用 Bouncy Castle 安全提供程序
        Security.addProvider(new BouncyCastleProvider());

        // 绑定端口和ip
        EndpointConfiguration endpointConfiguration = EndpointConfiguration.newBuilder()
                .setBindPort(port).build();

        HashSet<EndpointConfiguration> endpointConfigurations = new HashSet<>();
        endpointConfigurations.add(endpointConfiguration);

        // 创建 OPC UA 服务器配置
        OpcUaServerConfig opcServerConfig = OpcUaServerConfig.builder()
                // OPC UA 服务程序名称
                .setApplicationName(LocalizedText.english("MiloOpcServer"))
                .setApplicationUri("urn:opc:server")
                .setProductUri("urn:opc:product")
                // 设置端口号
                .setEndpoints(endpointConfigurations)
                .build();

        this.opcUaServer = new OpcUaServer(opcServerConfig);
    }

    public OpcUaServer getOpcUaServer() {
        return opcUaServer;
    }

    // 启动

    public void start(){
        // 异步启动服务器
        CompletableFuture<OpcUaServer> future = opcUaServer.startup();
        future.whenComplete((server, ex) -> {
            if (ex == null) {
                log.info("OPC UA server started successfully!");
            } else {
                log.error("Error starting OPC UA server: " , ex);
            }
        });
    }




    // 停止

    public void shutdown(){
        // 关闭服务器
        CompletableFuture<OpcUaServer> future = opcUaServer.shutdown();
        future.whenComplete((unused, ex) -> {
            if (ex == null) {
                log.info("OPC UA server shutdown successfully!");
            } else {
                log.error("Error shutting down OPC UA server: " , ex);
            }
        });
    }
}
