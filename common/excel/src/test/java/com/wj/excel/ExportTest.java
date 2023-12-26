package com.wj.excel;

import com.wj.execel.entity.User;
import com.wj.execel.util.ExcelFileUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author w
 * {@code @time:} 13:56
 * Description:
 */
public class ExportTest {


    @Test
    public void testExportData() {
        List<User> userList = getUserList(10);
        String path = Objects.requireNonNull(ExportTest.class.getResource("")).getPath();
        String fileName = path + ExcelFileUtil.buildCsvFileName(userList);
        String tableNames = ExcelFileUtil.buildCsvFileTableNames(userList);
        ExcelFileUtil.writeFile(fileName, tableNames);
        String contentBody = ExcelFileUtil.buildCsvFileBodyMap(userList);
        ExcelFileUtil.writeFile(fileName, contentBody);
    }

    private List<User> getUserList(int length) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            User user = new User(i + "", "MIKE" + i, 20 + i, i % 2 == 0 ? "女" : "男");
            userList.add(user);
        }
        return userList;
    }
}
