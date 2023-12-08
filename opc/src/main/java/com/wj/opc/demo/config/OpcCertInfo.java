package com.wj.opc.demo.config;

import lombok.Data;

/**
 * @author w
 * {@code @time:} 15:44
 * Description: OPC Server Cert Infomation
 * DnsName CommonName IpAddress ValidityPeriod
 */
@Data
public class OpcCertInfo {

    private String certPath;

    private String commonName;

    private String ipAddress;

    private String dnsName;

    private Integer validityPeriod;

    private String alias = "qiwu-cert";

    private String password = "keenyoda";
}
