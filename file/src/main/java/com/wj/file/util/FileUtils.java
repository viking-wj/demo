package com.wj.file.util;

import com.wj.file.entity.RangeEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件下载工具类
 *
 * @author wj
 * @date 2023/8/15 15:11
 */
public class FileUtils {

    public static void downloadFile(File file, HttpServletResponse response) {
        InputStream fis = null;
        OutputStream toClient = null;
        try {

            //获取文件名
            String filename = file.getName();
            //取得文件的后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            //以流的形式下载文件
            fis = new BufferedInputStream(new FileInputStream(file));
            //创建一个和文件一样大小的缓存区
            byte[] buffer = new byte[fis.available()];
            //读取流
            fis.read(buffer);
            //清空首部空白行
            response.reset();
            //设置文件下载后的指定文件名
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"), "ISO8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            //response.getOutputStream() 获得字节流，通过该字节流的write(byte[] bytes)可以向response缓冲区中写入字节，再由Tomcat服务器将字节内容组成Http响应返回给浏览器。
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            //将buffer 个字节从指定的 byte 数组写入此输出流。
            toClient.write(buffer);
            //刷新此缓冲的输出流。这迫使所有缓冲的输出字节被写出到底层输出流中。 把缓存区的数据全部写出
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                //关闭流
                if (fis != null) {
                    fis.close();
                }
                if (toClient != null) {
                    //关闭缓冲输出流
                    toClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void downloadFile(File file, HttpServletResponse response, RangeEntity rangeInfo) {
        InputStream fis = null;
        OutputStream toClient = null;
        try {

            //获取文件名
            String filename = file.getName();
            //取得文件的后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            //以流的形式下载文件
            fis = new BufferedInputStream(new FileInputStream(file));
            //创建一个和文件一样大小的缓存区
            Long startByte = rangeInfo.getStartByte();
            Long endByte = rangeInfo.getEndByte();
            Long size = endByte-startByte;
            byte[] buffer = new byte[fis.available()];
            //读取流
            fis.read(buffer);
            //清空首部空白行
            response.reset();
            //设置文件下载后的指定文件名
            response.addHeader("Content-Length", "" + file.length());
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"), "ISO8859-1"));
            //response.getOutputStream() 获得字节流，通过该字节流的write(byte[] bytes)可以向response缓冲区中写入字节，再由Tomcat服务器将字节内容组成Http响应返回给浏览器。
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            //将buffer 个字节从指定的 byte 数组写入此输出流。
            toClient.write(buffer,Integer.parseInt(startByte.toString()),Integer.parseInt(size.toString()));
            //刷新此缓冲的输出流。这迫使所有缓冲的输出字节被写出到底层输出流中。 把缓存区的数据全部写出
            toClient.flush();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                //关闭流
                if (fis != null) {
                    fis.close();
                }
                if (toClient != null) {
                    //关闭缓冲输出流
                    toClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 下载文件
     * NIO
     *
     * @param file:    要下在文件
     * @param response 响应
     * @throws IOException ioexception
     */
    public static void downloadFileByNio(File file,HttpServletResponse response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
        //设置响应头
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", "attachment;filename="+ new String(file.getName().getBytes(StandardCharsets.UTF_8),"ISO8859-1"));
        response.setContentLength((int) file.length());
        //获取文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //获取输出流通道
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        FileChannel fileChannel = fileInputStream.getChannel();
        //采用零拷贝的方式实现文件的下载
        fileChannel.transferTo(0,fileChannel.size(),writableByteChannel);
        //关闭对应的资源
        fileChannel.close();
        outputStream.flush();
        writableByteChannel.close();
    }

}
