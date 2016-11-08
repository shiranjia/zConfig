package com.zConfig;

import com.zConfig.monitor.Monitor;
import com.zConfig.store.MapStore;
import com.zConfig.store.Store;
import com.zConfig.zk.CuratorZK;
import com.zConfig.zk.ZKClient;
import com.zConfig.zk.ZKClientZK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一配置工具
 * Created by jiashiran on 2016/11/4.
 */
public class ZConfig {
    private static final    Logger              logger                              = LoggerFactory.getLogger(ZConfig.class);

    private static          ZKClient            client;

    private static          ZConfig             zConfig                             = new ZConfig();

    private ZConfig(){}

    /**
     * 构造函数 store 默认为map zk客户端使用Curator
     * @param url   zk地址
     * @param app   应用名
     * @return
     */
    public static ZConfig newCuratorClientConfig(String url , String app){
        Store store = new MapStore();
        client = new CuratorZK(url , app , store);
        return zConfig;
    }

    /**
     * 构造函数 store 默认为map zk客户端使用Curator
     * @param url   zk地址
     * @param app   应用名
     * @param refreshFromRemote 是否从zk同步配置到本地
     * @return
     */
    public static ZConfig newCuratorClientConfig(String url , String app , boolean refreshFromRemote){
        Store store = new MapStore();
        client = new CuratorZK(url , app , store);
        if(refreshFromRemote){
            client.refreshFromRemote();
        }
        return zConfig;
    }

    /**
     * 自定义 store 存储 构造函数 zk客户端使用Curator
     * @param url   zk地址
     * @param app   应用名
     * @param store 自定义存储结构
     * @return
     */
    public static ZConfig newCuratorClientConfig(String url , String app,Store store){
        client = new CuratorZK(url , app , store);
        return zConfig;
    }

    /**
     * 自定义 store 存储 构造函数 zk客户端使用Curator
     * @param url   zk地址
     * @param app   应用名
     * @param store 自定义存储结构
     * @param refreshFromRemote  是否从zk同步配置到本地
     * @return
     */
    public static ZConfig newCuratorClientConfig(String url , String app,Store store, boolean refreshFromRemote){
        client = new CuratorZK(url , app , store);
        if(refreshFromRemote){
            client.refreshFromRemote();
        }
        return zConfig;
    }

    /**
     * 使用ZKClient构造ZConfig
     * @param url
     * @param app
     * @return
     */
    @Deprecated
    public static ZConfig newZKClientConfig(String url , String app){
        client = new ZKClientZK(url,app,new MapStore());
        return zConfig;
    }

    /**
     * 使用ZKClient构造ZConfig
     * @param url
     * @param app
     * @param refreshFromRemote 是否从zk同步配置到本地
     * @return
     */
    @Deprecated
    public static ZConfig newZKClientConfig(String url , String app , boolean refreshFromRemote){
        client = new ZKClientZK(url,app,new MapStore());
        if(refreshFromRemote){
            client.refreshFromRemote();
        }
        return zConfig;
    }

    /**
     * 设置配置信息
     * @param key
     * @param value
     */
    public void set(String key,String value){
        try {
            client.set(key, value);
        } catch (Exception e) {
            logger.error("ZConfig.set.exception : " ,e);
            e.printStackTrace();
        }
    }

    /**
     * 查询配置信息
     * @param key
     * @return
     */
    public String get(String key){
        String v = null;
        try {
            v = client.get(key);
        } catch (Exception e) {
            logger.error("ZConfig.get.exception : " ,e);
            e.printStackTrace();
        }
        return v;
    }

    /**
     * 删除配置信息
     * @param key
     */
    public void remove(String key){
        try {
            client.remove(key);
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("ZConfig.remove.exception : " ,e);
        }
    }

    public Monitor getMonitor(){
        return client.getMonitor();
    }
}
