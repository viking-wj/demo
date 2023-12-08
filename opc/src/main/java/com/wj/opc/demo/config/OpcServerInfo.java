package com.wj.opc.demo.config;

import lombok.Data;

/**
 * @author w
 * {@code @time:} 15:13
 * Description: OPC Server infomation such as productUri,manufacturerName
 * default setting
 */
@Data
public class OpcServerInfo {

    private String productUri = "urn:keenyoda:milo:qiwu-server";

    private String manufacturerName = "keenyoda";

    private String productName = "keenyoda opc server";

    private String serverVersion = "1.0.0";

}
