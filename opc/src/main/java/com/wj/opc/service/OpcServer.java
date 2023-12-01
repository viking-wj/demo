package com.wj.opc.service;

import com.wj.opc.pool.OpcNamespace;
import com.wj.opc.pool.OpcServerLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * @author w
 * {@code @time:} 17:15
 * Description: OPC SERVER
 */
@Slf4j
@Component
public class OpcServer {

    @PostConstruct
    public void run() {
        OpcServerLink opcServerLink = new OpcServerLink(12688);
        opcServerLink.start();
        OpcNamespace namespace = new OpcNamespace(opcServerLink.getOpcUaServer());
        Set<String> keys = getAllKeys("ehc.txt");
        namespace.addNodes(keys);
        namespace.startup();

    }

    private static Set<String> getAllKeys(String fileName) {
        Set<String> keys = new HashSet<>(50);
        try {
            InputStream is = OpcServer.class.getResourceAsStream("/points/" + fileName);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            String line;
            while ((line = in.readLine()) != null) {
                keys.add(line);
            }
        } catch (FileNotFoundException e) {
            log.error("文件未找到！", e);
        } catch (IOException e) {
            log.error("I/O异常！", e);
        }
        return keys;
    }
}
