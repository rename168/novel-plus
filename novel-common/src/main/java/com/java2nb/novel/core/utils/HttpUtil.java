package com.java2nb.novel.core.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
public class HttpUtil {

    private static RestTemplate restTemplate = RestTemplateUtil.getInstance("utf-8");

    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String getByHttpClient(String url) {
        try {

            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            if (forEntity.getStatusCode() == HttpStatus.OK) {
                return forEntity.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Map<String, Long > urlMap = new ConcurrentHashMap<>();

    private static long reqCount = 0 ;
    public static String getByHttpClientWithChrome(String url) {
        try {
            Long lasttime = urlMap.get(url) ;
            if( lasttime !=null  && System.currentTimeMillis()-lasttime<3000 ){
                logger.error(String.format( " 已经正在请求 reqCount %d, url: %s  time:%d" ,reqCount++ , url , System.currentTimeMillis()-lasttime)) ;
                // Throwable t =  new Throwable(url) ;
                // t.printStackTrace();
                // return null ;
            }else {
                logger.info(String.format( "getByHttpClientWithChrome reqCount %d,  url: %s ",reqCount++ , url)) ;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("user-agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36");
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            urlMap.put(url, System.currentTimeMillis() );
            ResponseEntity<String> forEntity = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, String.class);

            if (forEntity.getStatusCode() == HttpStatus.OK) {
                return forEntity.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(String.format( " 请求失败 url: %s  Message:%s" , url ,e.getMessage())) ;

            e.printStackTrace();
            return null;
        }
    }
}
