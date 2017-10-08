package com.itheima.quickindex11.bean;

import com.itheima.quickindex11.utils.PinyinUtils;

/**
 * Created by Administrator on 2016/12/27.
 */
public class Contact {

    /** 中文名字 */
    public String name;

    /** 中文转拼音字符串 */
    public String pingying;

    /** 拼音首字母  */
    public String firstLetter;

    public Contact(String name) {
        // 中国
        this.name = name;
        // zhongguo
        pingying = PinyinUtils.getPinyin(name);
        // z
        firstLetter = pingying.charAt(0) + "";
    }

}
