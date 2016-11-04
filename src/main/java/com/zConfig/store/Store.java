package com.zConfig.store;

/**
 * 配置信息存储结构
 * Created by jiashiran on 2016/11/4.
 */
public abstract class Store {

    /**
     * 设置 更新 数据
     * @param key
     * @param value
     * @return
     */
    public abstract String set(String key,String value);


    /**
     * 查询数据
     * @param key
     */
    public abstract String get(String key);


    /**
     * 删除元素
     * @param key
     */
    public abstract void remove(String key);
}
