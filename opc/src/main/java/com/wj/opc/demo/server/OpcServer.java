package com.wj.opc.demo.server;

import com.wj.opc.demo.config.OpcCertInfo;
import com.wj.opc.demo.config.OpcProperties;
import com.wj.opc.demo.config.OpcServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.identity.CompositeValidator;
import org.eclipse.milo.opcua.sdk.server.identity.UsernameIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.X509IdentityValidator;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.security.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.transport.TransportProfile;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.util.CertificateUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedHttpsCertificateBuilder;
import org.eclipse.milo.opcua.stack.server.EndpointConfiguration;
import org.eclipse.milo.opcua.stack.server.security.DefaultServerCertificateValidator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.*;

/**
 * @author w
 * {@code @time:} 11:55
 * Description: OPC 服务端
 * 创建服务 配置认证
 */
@Component
@Slf4j
public class OpcServer {

    private OpcUaServer server;


    private NameSpace nameSpace;

    @Resource
    private OpcProperties opcProperties;

    @Resource
    private KeyManager loader;

    @PostConstruct
    public void run() {
        try {
            initOpcServer();
            server.startup().get();
            final CompletableFuture<Void> future = new CompletableFuture<>();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> future.complete(null)));
            future.get();
        } catch (InterruptedException e) {
            log.error("server start interrupted exception", e);
        } catch (ExecutionException e) {
            log.error("future execution exception!", e);
        }
    }


    public void initOpcServer() {

        OpcCertInfo certInfo = opcProperties.getCertInfo();

        try {
            // 指定证书加载位置
            Path jarPath = Paths.get(OpcServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path basePath = jarPath.getParent();
            Path securityTempDir = basePath.resolve("server").resolve("security");
            Files.createDirectories(securityTempDir);

            if (!Files.exists(securityTempDir)) {
                log.error("unable to create security temp dir" + securityTempDir);
            }


            File pkiDir = securityTempDir.resolve("pki").toFile();

            log.info("security dir: {}", securityTempDir.toAbsolutePath());
            log.info("security pki dir: {}", pkiDir.getAbsolutePath());

            // 加载密钥库和证书(没有则自动生成)
            loader.load(securityTempDir);

            // 创建证书管理器：
            DefaultCertificateManager certificateManager = new DefaultCertificateManager(
                    loader.getServerKeyPair(),
                    loader.getServerCertificateChain()
            );

            // 创建受信任列表管理器
            DefaultTrustListManager trustListManager = new DefaultTrustListManager(pkiDir);

            // 创建服务器证书验证器
            DefaultServerCertificateValidator certificateValidator =
                    new DefaultServerCertificateValidator(trustListManager);

            // 生成 HTTPS 证书
            KeyPair httpsKeyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

            SelfSignedHttpsCertificateBuilder httpsCertificateBuilder = new SelfSignedHttpsCertificateBuilder(httpsKeyPair);
            httpsCertificateBuilder.setCommonName(HostnameUtil.getHostname());
            HostnameUtil.getHostnames("0.0.0.0").forEach(httpsCertificateBuilder::addDnsName);
            X509Certificate x509Certificate = httpsCertificateBuilder.build();

            X509IdentityValidator x509IdentityValidator = new X509IdentityValidator(c -> true);

            OpcServerInfo opcServerInfo = opcProperties.getServerInfo();
            X509Certificate certificate = certificateManager.getCertificates()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new UaRuntimeException(StatusCodes.Bad_ConfigurationError, "no certificate found"));

            // 从证书中获取applicationUri
            String applicationUri = CertificateUtil
                    .getSanUri(certificate)
                    .orElseThrow(() -> new UaRuntimeException(
                            StatusCodes.Bad_ConfigurationError,
                            "certificate is missing the application URI"));

            UsernameIdentityValidator usernameIdentityValidator = initPasswordIdentityValidator();
            Set<EndpointConfiguration> endpointConfigurations = createEndpointConfigurations(certificate);

            // 构建OPCUA Server配置
            OpcUaServerConfig serverConfig = OpcUaServerConfig.builder()
                    .setApplicationUri(applicationUri)
                    .setApplicationName(LocalizedText.english("OPC UA Server Demo"))
                    .setEndpoints(endpointConfigurations)
                    .setBuildInfo(
                            new BuildInfo(
                                    opcServerInfo.getProductUri(),
                                    opcServerInfo.getManufacturerName(),
                                    opcServerInfo.getProductName(),
                                    OpcUaServer.SDK_VERSION,
                                    opcServerInfo.getServerVersion(), DateTime.now()))
                    .setCertificateManager(certificateManager)
                    .setTrustListManager(trustListManager)
                    .setCertificateValidator(certificateValidator)
                    .setHttpsKeyPair(httpsKeyPair)
                    .setHttpsCertificateChain(new X509Certificate[]{x509Certificate})
                    .setIdentityValidator(new CompositeValidator(usernameIdentityValidator, x509IdentityValidator))
                    .setProductUri(opcServerInfo.getProductUri())
                    .build();

            server = new OpcUaServer(serverConfig);

            nameSpace = new NameSpace(server);
            nameSpace.startup();
        } catch (URISyntaxException e) {
            log.error("Failed to get the current path !", e);
        } catch (IOException e) {
            log.error("Failed to read file location", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm!", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建端点配置
     *
     * @param certificate
     * @return {@link Set}<{@link EndpointConfiguration}>
     */
    private Set<EndpointConfiguration> createEndpointConfigurations(X509Certificate certificate) {
        Set<EndpointConfiguration> endpointConfigurations = new LinkedHashSet<>();

        List<String> bindAddresses = newArrayList();
        bindAddresses.add("0.0.0.0");

        Set<String> hostnames = new LinkedHashSet<>();
        hostnames.add(HostnameUtil.getHostname());
        hostnames.addAll(HostnameUtil.getHostnames("0.0.0.0"));

        for (String bindAddress : bindAddresses) {
            for (String hostname : hostnames) {
                EndpointConfiguration.Builder builder = EndpointConfiguration.newBuilder()
                        .setBindAddress(bindAddress)
                        .setHostname(hostname)
                        .setCertificate(certificate)
                        .addTokenPolicies(
                                USER_TOKEN_POLICY_ANONYMOUS,
                                USER_TOKEN_POLICY_USERNAME,
                                USER_TOKEN_POLICY_X509);


                EndpointConfiguration.Builder noSecurityBuilder = builder.copy()
                        .setSecurityPolicy(SecurityPolicy.None)
                        .setSecurityMode(MessageSecurityMode.None);

                endpointConfigurations.add(buildTcpEndpoint(noSecurityBuilder));
                endpointConfigurations.add(buildHttpsEndpoint(noSecurityBuilder));

                // TCP Basic256Sha256 / SignAndEncrypt
                endpointConfigurations.add(buildTcpEndpoint(
                        builder.copy()
                                .setSecurityPolicy(SecurityPolicy.Basic256Sha256)
                                .setSecurityMode(MessageSecurityMode.SignAndEncrypt))
                );

                // HTTPS Basic256Sha256 / Sign (SignAndEncrypt not allowed for HTTPS)
                endpointConfigurations.add(buildHttpsEndpoint(
                        builder.copy()
                                .setSecurityPolicy(SecurityPolicy.Basic256Sha256)
                                .setSecurityMode(MessageSecurityMode.Sign))
                );

                /*
                  提供一个没有安全设置的 "discovery" 端点
                 */
                EndpointConfiguration.Builder discoveryBuilder = builder.copy()
                        .setPath("/discovery")
                        .setSecurityPolicy(SecurityPolicy.None)
                        .setSecurityMode(MessageSecurityMode.None);

                endpointConfigurations.add(buildTcpEndpoint(discoveryBuilder));
                endpointConfigurations.add(buildHttpsEndpoint(discoveryBuilder));
            }
        }

        return endpointConfigurations;
    }

    private EndpointConfiguration buildTcpEndpoint(EndpointConfiguration.Builder base) {
        return base.copy()
                .setTransportProfile(TransportProfile.TCP_UASC_UABINARY)
                .setBindPort(opcProperties.getTcpPort())
                .build();
    }

    private EndpointConfiguration buildHttpsEndpoint(EndpointConfiguration.Builder base) {
        return base.copy()
                .setTransportProfile(TransportProfile.HTTPS_UABINARY)
                .setBindPort(opcProperties.getHttpPort())
                .build();
    }

    /**
     * 初始化用户密码校验器
     *
     * @return {@link UsernameIdentityValidator} 用户密码校验器
     */
    private UsernameIdentityValidator initPasswordIdentityValidator() {
        // 配置用户密码校验规则
        return new UsernameIdentityValidator(
                true,
                authChallenge -> {
                    String username = authChallenge.getUsername();
                    String password = authChallenge.getPassword();
                    String loginName = null;
                    String loginPassword = null;
                    try {
                        if (null != opcProperties) {
                            loginName = opcProperties.getUsername();
                            loginPassword = opcProperties.getPassword();
                        } else {
                            log.error("opcProperties is null! " +
                                    "please check your properties!");
                        }
                    } catch (NullPointerException e) {
                        log.error("username or password is null! " +
                                "please check your properties!", e);
                    }
                    if (loginName != null && loginPassword != null) {
                        return loginName.equals(username)
                                && loginPassword.equals(password);
                    } else {
                        log.error("username or password is null!");
                        return false;
                    }
                }
        );
    }


    public CompletableFuture<OpcUaServer> startup() {
        return server.startup();
    }

    public CompletableFuture<OpcUaServer> shutdown() {
        nameSpace.shutdown();
        return server.shutdown();
    }
}
