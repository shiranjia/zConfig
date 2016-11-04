package com.zConfig.zk;

import com.zConfig.monitor.Monitor;
import com.zConfig.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiashiran on 2016/11/4.
 */
public abstract class ZKClient {
    protected   final     Logger        log                                          = LoggerFactory.getLogger(this.getClass());
    public      final       String      ZOOKEEPER_SEPARATOR                          = "/";
    private     final       String      ROOT_PATH                                    = "/xConfig";                   //配置根目录
    protected               String      APP_PATH;                                                                    //应用目录
    private     volatile    boolean     watched                                      = false;                        //是否监听数据变化
    protected               Store       store;                                                                       //存储数据结构


    /**
     * 设置数据
     * @param key
     * @param value
     */
    public void set(String key,String value) throws Exception {
        if( !watched ){
            watched = true;
            watcher();
        }
        setLocal(key,value);
        setRemote(getNodePath(key), value);
    }

    /**
     * 删除配置
     * @param key
     */
    public void remove(String key)  throws Exception{
        store.remove(key);
        removeRemote(getNodePath(key));
    }

    /**
     * 删除zk中配置
     * @param key
     */
    protected abstract void removeRemote(String key) throws Exception;

    /**
     * 更新本地数据
     *
     * @param key
     * @param value
     */
    protected void setLocal(String key, String value) {
        this.store.set(key,value);
    }

    /**
     * 更新zk
     * @param path
     * @param value
     */
    protected abstract void setRemote(String path,String value) throws Exception;

    /**
     * 添加监听
     */
    protected abstract void watcher() throws Exception;

    /**
     * 获得数据
     *
     * @param key
     */
    public String get(String key) {
        return this.store.get(key);
    }

    /**
     * 获得应用路径
     * @return
     */
    protected String getAppPath(){
        return ROOT_PATH  + ZOOKEEPER_SEPARATOR + APP_PATH;
    }

    /**
     * 根据zk路径得到数据key
     * @param path
     * @return
     */
    protected String getNodeKey(String path){
        return path.replace(getAppPath(),"");
    }

    /**
     * 构造配置数据节点路径
     * @param key
     * @return
     */
    private String getNodePath(String key){
        return ROOT_PATH  + ZOOKEEPER_SEPARATOR + APP_PATH + ZOOKEEPER_SEPARATOR + key;
    }

    /**
     * 获得监控信息
     * @return
     */
    public abstract Monitor getMonitor();

}
