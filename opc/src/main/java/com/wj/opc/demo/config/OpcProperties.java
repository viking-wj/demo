package com.wj.opc.demo.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author w
 * {@code @time:} 14:04
 * Description: OPC配置类
 */
@Configuration("opc")
@Data
public class OpcProperties {

    private OpcServerInfo serverInfo = new OpcServerInfo();

    private OpcCertInfo certInfo = new OpcCertInfo();

    private AuthenticMethodEnum authentication = AuthenticMethodEnum.PASSWORD_CERT;

    private Integer tcpPort = 12686;

    private Integer httpPort = 8443;

    private String username = "admin";

    private String password=  "admin";

}
