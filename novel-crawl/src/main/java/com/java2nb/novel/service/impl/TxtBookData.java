package com.java2nb.novel.service.impl;

import lombok.Data;

// {"url":"http://m.kenshuzw.com/xiaoshuo/198773/",
// "author":"笑看尼姑",
//"title":"神魔之恋我在这里等你",
// "txtUrl":"http://txt.kenshuzw.com/modules/article/txtarticlee.php?id=198773&fname=神魔之恋我在这里等你",
// "code":"198773",
// "type":0}

@Data
public class TxtBookData {
    public String url;
    public String author;
    public String title;
    public String txtUrl;
    public String code;
    public Integer type;

    public String imgUrl;
    public String desc;
    public String catName;
    public String complete;



}
