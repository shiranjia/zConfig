package com.zConfig.zk;

import com.google.common.base.Optional;
import com.zConfig.monitor.Monitor;
import com.zConfig.monitor.Node;
import com.zConfig.store.Store;
import com.zConfig.utils.AddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jiashiran on 2016/11/4.
 */
public abstract class ZKClient {
    protected   final       Logger      log                                          = LoggerFactory.getLogger(this.getClass());
    protected   final       String      ZOOKEEPER_SEPARATOR                          = "/";
    private     final       String      ROOT_PATH                                    = "/xConfig";                   //配置根目录
    protected               String      APP_PATH;                                                                    //应用目录
    private     final       String      DATA_PATH                                    = "data";                       //数据存放目录
    private     final       String      SERVER_PATH                                  = "cluster";                    //服务存放目录
    //private     volatile    boolean     watched                                      = false;                        //是否监听数据变化
    protected               Store       store;                                                                       //存储数据结构
    protected               Monitor     monitor;

    /**
     * 初始化方法，注册监听服务等
     */
    protected void init(){
        watcher();
        registeServer();
    }
    /**
     * 设置数据
     * @param key
     * @param value
     */
    public void set(String key,String value) throws Exception {
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
     * @param path
     */
    protected abstract void removeRemote(String path) throws Exception;

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
    protected abstract void watcher();

    /**
     * 获得数据
     *
     * @param key
     */
    public String get(String key) {
        Optional<String> optional = Optional.fromNullable(this.store.get(key));
        return optional.orNull();
    }

    /**
     * 获得应用路径
     * @return
     */
    protected String getAppPath(){
        return ROOT_PATH  + ZOOKEEPER_SEPARATOR + APP_PATH;
    }

    /**
     * 获得数据存放路径
     * @return
     */
    protected String getDatePath(){
        return getAppPath() + ZOOKEEPER_SEPARATOR + DATA_PATH;
    }

    protected String getClusterPath(){
        return getAppPath() + ZOOKEEPER_SEPARATOR + SERVER_PATH;
    }

    /**
     * 获得服务器ip路径
     * @return
     */
    protected String host(){
        return getAppPath() + ZOOKEEPER_SEPARATOR + SERVER_PATH + ZOOKEEPER_SEPARATOR + AddressUtils.getHostIp();
    }

    /**
     * 根据zk路径得到数据key
     * @param path
     * @return
     */
    protected String getNodeKey(String path){
        return path.replace(getDatePath() + ZOOKEEPER_SEPARATOR,"");
    }

    /**
     * 构造配置数据节点路径
     * @param key
     * @return
     */
    protected String getNodePath(String key){
        return getDatePath() + ZOOKEEPER_SEPARATOR + key;
    }

    /**
     * 获得监控信息
     * @return
     */
    public abstract Monitor getMonitor();

    /**
     * 从zk同步配置到本地
     */
    public void refreshFromRemote(){
        Monitor monitor = getMonitor();
        List<Node> list = monitor.getConfigList();
        for (Node n : list){
            store.set(n.getKey() , n.getValue());
        }
    }

    /**
     * 注册服务ip
     */
    public abstract void registeServer();

}
