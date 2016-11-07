package com.zConfig.store;

import java.util.LinkedList;

/**
 * 基于链式存储
 * Created by jiashiran on 2016/11/7.
 */
public class LinkListStore extends LinkedList<String> implements Store{

    public LinkListStore(){
        super();
    }


    /**
     * 设置 更新 数据
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String set(String key, String value) {
        return null;
    }

    /**
     * 查询数据
     *
     * @param key
     */
    @Override
    public String get(String key) {
        return null;
    }

    /**
     * 删除元素
     *
     * @param key
     */
    @Override
    public void remove(String key) {

    }
}
