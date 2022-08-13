package com.java2nb.novel.service.impl;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ChapterData {
    public String chapterName;
    public ArrayList<String> content;

    public Integer type;

}
