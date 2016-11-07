package com.zConfig.store;

/**
 * 配置信息存储结构
 * Created by jiashiran on 2016/11/4.
 */
public interface Store {

    /**
     * 设置 更新 数据
     * @param key
     * @param value
     * @return
     */
    String set(String key,String value);


    /**
     * 查询数据
     * @param key
     */
    String get(String key);


    /**
     * 删除元素
     * @param key
     */
    void remove(String key);
}
