package com.wj.file.controller;

import com.wj.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA
 * 文件下载
 *
 * @author wj
 * @date 2023/8/14 14:21
 */
@RestController
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 下载文件
     * <p>
     * 对外提供
     *
     * @param fileId    文件Id
     *  token     token
     *  accountId 帐号Id
     *  response  响应
     */
    @GetMapping("/file/download")
    public void downloadFile(@RequestParam(required = false) String fileId,
                             @RequestHeader(value = "Range",required = false) String range,
                             HttpServletResponse response) {

        if ("1".equals(fileId)) {
            fileService.downloadFile(fileId, response, range);
        } else {
            try {
                fileService.downloadFileByNio(fileId, response);
            } catch (IOException e) {
                log.error("I/O异常！",e);
            }
        }
    }


}
