package com.java2nb.novel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 */
@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@ServletComponentScan
@MapperScan(basePackages = {"com.java2nb.novel.mapper"})
public class CrawlNovelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlNovelApplication.class);
        test();

    }
    private static void test(){
        Pattern indexIdPatten = Pattern.compile( "<a href=\'/book/\\d+/(\\d+)\\.html\'>[^/]+</a>");
        Matcher indexIdMatch = indexIdPatten.matcher( "<p><a href='/book/55327/23014917.html'>bbb！</a></p><p>");

        boolean isFindIndex = indexIdMatch.find() ;
        log.info(" indexIdPatten %b"  ,isFindIndex) ;

    }

}
