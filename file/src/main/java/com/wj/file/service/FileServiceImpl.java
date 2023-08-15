package com.wj.file.service;

import com.wj.file.entity.RangeEntity;
import com.wj.file.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/14 14:25
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * 直接下载文件
     *
     * Tips： 支持断点下载
     * @param fileId 文件Id
     * @param response 返回
     * @param range 范围
     */
    @Override
    public void downloadFile(String fileId, HttpServletResponse response, String range) {
        //// 根据 fileId 获取文件信息
        //FileInfo fileInfo = getFileInfo(fileId);
        //
        //String bucketName = fileInfo.getBucketName();
        //String relativePath = fileInfo.getRelativePath();
        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
        File file = new File(path+"/test.txt");
        long usableSpace = file.getUsableSpace();
        //
        //// 处理 range，范围信息
        RangeEntity rangeInfo = executeRangeInfo(range, usableSpace);

         // rangeInfo = null，直接下载整个文件
        if (Objects.isNull(rangeInfo)) {
            FileUtils.downloadFile(file,response);
            return;
        }
        // 下载部分文件
        FileUtils.downloadFile(file,response,rangeInfo);
    }

    @Override
    public void downloadFileByNio(String fileId, HttpServletResponse response) throws IOException {
        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
        File file = new File(path+"/test.txt");
        FileUtils.downloadFileByNio(file,response);
    }

    private RangeEntity executeRangeInfo(String range, Long fileSize) {

        if (StringUtils.isEmpty(range) || !range.contains("bytes=") || !range.contains("-")) {

            return null;
        }

        long startByte = 0;
        long endByte = fileSize - 1;

        range = range.substring(range.lastIndexOf("=") + 1).trim();
        String[] ranges = range.split("-");

        if (ranges.length <= 0 || ranges.length > 2) {

            return null;
        }

        try {
            if (ranges.length == 1) {
                if (range.startsWith("-")) {

                    //1. 如：bytes=-1024  从开始字节到第1024个字节的数据
                    endByte = Long.parseLong(ranges[0]);
                } else if (range.endsWith("-")) {

                    //2. 如：bytes=1024-  第1024个字节到最后字节的数据
                    startByte = Long.parseLong(ranges[0]);
                }
            } else {
                //3. 如：bytes=1024-2048  第1024个字节到2048个字节的数据
                startByte = Long.parseLong(ranges[0]);
                endByte = Long.parseLong(ranges[1]);
            }
        } catch (NumberFormatException e) {
            startByte = 0;
            endByte = fileSize - 1;
        }

        if (startByte >= fileSize) {

            log.error("range error, startByte >= fileSize. " +
                    "startByte: {}, fileSize: {}", startByte, fileSize);
            return null;
        }

        return new RangeEntity(startByte, endByte);
    }
}
