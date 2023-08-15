package com.wj.file;

import org.junit.Before;
import org.junit.jupiter.api.TestTemplate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA
 * FILE 测试类
 * @author wj
 * @date 2023/8/15 16:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileApplicationTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws MalformedURLException {
        String url = String.format("http://localhost:%d/",port);
        System.out.println(String.format("port is : [%d]",port));
        this.base = new URL(url);
    }

    /**
     * 下载文件通过nio
     */
    public void downLoadFileByNIO(){
        ResponseEntity<String> forEntity = this.restTemplate.getForEntity(this.base.toString() + "/file/download/2", String.class, "");
        System.out.println(forEntity.getBody());
    }
}
