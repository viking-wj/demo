package com.wj.execel.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author w
 * {@code @time:} 11:23
 * Description: excel导出工具
 * 字符串读入转CSV格式
 * 文件写出
 * 构建文件名称
 * 构建行标题
 * 构建execl内容
 * 类转Map
 */
@Slf4j
public class ExcelFileUtil {
    public static final String FILE_SUFFIX = ".csv";
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_TAIL = "\r\n";
    protected static final String DATE_STR_FILE_NAME = "yyyyMMddHHmmssSSS";

    /**
     * @param savePath   保存路径
     * @param contextStr 写入文件内容
     * @throws IOException IO异常
     */
    public static void createCsvFile(String savePath, String contextStr) throws IOException {
        // 根据路径创建文件
        File file = new File(savePath);
        boolean newFile = file.createNewFile();

        if (newFile) {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(contextStr.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
        } else {
            log.error("文件创建失败!");
        }


    }

    /**
     * 写入文件
     * @param fileName 文件名称
     * @param content 文件内容
     */
    public static void writeFile(String fileName, String content) {
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            fos = new FileOutputStream(fileName, true);
            writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            log.error("文件写入异常", e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }

    }
    /**
     * 构建文件名称
     * @param dataList 数据
     * @return 文件名
     */
    public static String buildCsvFileName(List<?> dataList) {
        return dataList.get(0).getClass().getSimpleName() + new SimpleDateFormat(DATE_STR_FILE_NAME).format(new Date()) + FILE_SUFFIX;
    }


    public static String buildCsvFileTableNames(List<?> dataList) {
        StringBuilder tableNames = new StringBuilder();
        Map<String, Object> map = toMap(dataList.get(0));
        for (String key : map.keySet()) {
            tableNames.append(key).append(ExcelFileUtil.CSV_DELIMITER);
        }
        return tableNames.append(ExcelFileUtil.CSV_TAIL).toString();
    }

    public static String buildCsvFileBodyMap(List<?> dataLists) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        for (Object o : dataLists) {
            mapList.add(toMap(o));
        }
        StringBuilder lineBuilder = new StringBuilder();
        for (Map<String, Object> rowData : mapList) {
            for (String key : rowData.keySet()) {
                Object value = rowData.get(key);
                if (Objects.nonNull(value)) {
                    lineBuilder.append(value).append(ExcelFileUtil.CSV_DELIMITER);
                } else {
                    lineBuilder.append("--").append(ExcelFileUtil.CSV_DELIMITER);
                }
            }
            lineBuilder.append(ExcelFileUtil.CSV_TAIL);
        }
        return lineBuilder.toString();
    }

    public static <T> Map<String, Object> toMap(T entity) {
        // 获取属性名称
        Class<?> aClass = entity.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Map<String, Object> map = new HashMap<>(declaredFields.length);
        for (Field field : declaredFields) {
            // 排除序列化id
            String name = field.getName();
            try {
                if (!"serialVersionUID".equals(name)) {
                    String methodName = "get"
                            + name.substring(0, 1).toUpperCase()
                            + name.substring(1);
                    Method method = aClass.getMethod(methodName);
                    Object invoke = method.invoke(entity);
                    map.put(name, invoke);
                }
            } catch (NoSuchMethodException e) {
                log.error("找不到对应的方法", e);
            } catch (IllegalAccessException e) {
                log.error("获取方法异常", e);
            } catch (InvocationTargetException e) {
                log.error("方法执行异常", e);
            }
        }
        return map;
    }
}
