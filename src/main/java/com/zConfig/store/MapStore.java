package com.zConfig.store;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 基于map实现存储
 * Created by jiashiran on 2016/11/4.
 */
public class MapStore extends Store{

    private Map<String,String> map = Maps.newHashMap();

    /**
     * 设置 更新 数据
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String set(String key, String value) {
        return map.put(key, value);
    }

    /**
     * 查询数据
     *
     * @param key
     */
    @Override
    public String get(String key) {
        return map.get(key);
    }

    /**
     * 删除元素
     *
     * @param key
     */
    @Override
    public void remove(String key) {
        map.remove(key);
    }

}
