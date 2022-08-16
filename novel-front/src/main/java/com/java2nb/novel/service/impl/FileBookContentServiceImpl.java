package com.java2nb.novel.service.impl;

import com.java2nb.novel.core.utils.FileUtil;
import com.java2nb.novel.entity.BookContent;
import com.java2nb.novel.service.BookContentService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "txt.save", name = "storage", havingValue = "file")
public class FileBookContentServiceImpl implements BookContentService {

    @Value("${txt.save.path}")
    private String fileSavePath;

    @SneakyThrows
    public BookContent queryBookContent_old(Long bookId, Long bookIndexId) {
        BufferedReader in = new BufferedReader(
                new FileReader(fileSavePath + "/" + bookId + "/" + bookIndexId + ".txt"));
        StringBuffer sb = new StringBuffer();
        String str;
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        return new BookContent() {
            {
                setIndexId(bookIndexId);
                setContent(sb.toString());
            }
        };
    }

    @SneakyThrows
    @Override
    public BookContent queryBookContent(Long bookId, Long bookIndexId) {
        File file = new File(fileSavePath + "/" + bookId + "/" + bookIndexId + ".txt");
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // String[] fileContentArr = new String(filecontent).split("\r\n");

        // return fileContentArr;// 返回文件内容,默认编码

        return new BookContent() {
            {
                setIndexId(bookIndexId);
                setContent(new String(filecontent));
            }
        };
    }

}
