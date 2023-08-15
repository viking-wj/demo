package com.wj.file.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/14 14:24
 */
public interface FileService {
    /**
     * 下载文件
     *
     * @param fileId   文件id
     * @param range    断点范围
     * @param response 响应
     */
    void downloadFile(String fileId, HttpServletResponse response, String range);

    /**
     * 下载文件通过nio
     *
     * @param fileId   文件标识
     * @param response 响应
     * @throws IOException ioexception
     */
    void downloadFileByNio(String fileId, HttpServletResponse response) throws IOException;
}
