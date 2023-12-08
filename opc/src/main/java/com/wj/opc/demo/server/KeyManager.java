package com.wj.opc.demo.server;

import com.google.common.collect.Sets;
import com.wj.opc.demo.config.OpcCertInfo;
import com.wj.opc.demo.config.OpcProperties;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author w
 * {@code @time:} 16:12
 * Description: 秘钥管理器
 */
@Slf4j
@Data
@Component
public class KeyManager {
    // 正则表达式用于匹配 IP 地址
    private static final Pattern IP_ADDR_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


    @Resource
    private OpcProperties opcProperties;


    // 获取服务端证书链
    // 服务端证书链、单一服务端证书和服务端密钥对
    @Getter
    private X509Certificate[] serverCertificateChain;
    private X509Certificate serverCertificate;
    private KeyPair serverKeyPair;

    // 加载密钥库的方法
    KeyManager load(Path baseDir) throws Exception {
        OpcCertInfo certInfo = opcProperties.getCertInfo();

        // 密钥库中服务端证书的别名和密码
        String serverAlias = certInfo.getAlias();
        char[] password = certInfo.getPassword().toCharArray();

        // 创建 PKCS#12 密钥库
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        // 获取服务端密钥库文件的路径
        File serverKeyStore = baseDir.resolve("server.pfx").toFile();

        // 输出加载密钥库的信息
        log.info("Loading KeyStore at {}", serverKeyStore);

        // 如果密钥库文件不存在，则生成自签名证书并保存到密钥库中
        if (!serverKeyStore.exists()) {
            keyStore.load(null, password);

            // 生成 RSA 密钥对
            KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

            // 生成自签名证书的建造者
            SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
                    .setCommonName("Keenyoda QiWu Server")
                    .setOrganization("Keenyoda")
                    .setOrganizationalUnit("QiWu")
                    .setLocalityName("shenzhen")
                    .setCountryCode("CN");

            // 生成应用程序 URI
            String applicationUri = "urn:keenyoda:milo:server:" + UUID.randomUUID();
            builder.setApplicationUri(applicationUri);

            // 获取主机名和 IP 地址列表，添加到证书中
            Set<String> hostnames = Sets.union(
                    Sets.newHashSet(HostnameUtil.getHostname()),
                    HostnameUtil.getHostnames("0.0.0.0", false)
            );

            for (String hostname : hostnames) {
                if (IP_ADDR_PATTERN.matcher(hostname).matches()) {
                    builder.addIpAddress(hostname);
                } else {
                    builder.addDnsName(hostname);
                }
            }

            // 构建自签名证书
            X509Certificate certificate = builder.build();

            // 将密钥对和证书保存到密钥库中
            keyStore.setKeyEntry(serverAlias, keyPair.getPrivate(), password, new X509Certificate[]{certificate});
            keyStore.store(Files.newOutputStream(serverKeyStore.toPath()), password);
        } else {
            // 如果密钥库文件已存在，则加载它
            keyStore.load(Files.newInputStream(serverKeyStore.toPath()), password);
        }

        // 获取服务端私钥并构建服务端密钥对
        Key serverPrivateKey = keyStore.getKey(serverAlias, password);
        if (serverPrivateKey instanceof PrivateKey) {
            serverCertificate = (X509Certificate) keyStore.getCertificate(serverAlias);

            serverCertificateChain = Arrays.stream(keyStore.getCertificateChain(serverAlias))
                    .map(X509Certificate.class::cast)
                    .toArray(X509Certificate[]::new);

            PublicKey serverPublicKey = serverCertificate.getPublicKey();
            serverKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
        }

        return this;
    }

    // 获取服务端证书
    X509Certificate getServerCertificate() {
        return serverCertificate;
    }

    // 获取服务端密钥对
    KeyPair getServerKeyPair() {
        return serverKeyPair;
    }
}
