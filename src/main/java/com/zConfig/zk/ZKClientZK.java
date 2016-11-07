package com.zConfig.zk;

import com.google.common.collect.Lists;
import com.zConfig.monitor.Monitor;
import com.zConfig.monitor.Node;
import com.zConfig.store.Store;
import com.zConfig.utils.AddressUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiashiran on 2016/11/7.
 */
public class ZKClientZK extends ZKClient{

    private int                     soTimeout     = 5000;                                    //zk链接超时时间
    private int                     retryTimes    = 3;                                       //重试链接次数
    private int                     retryInterval = 3000;                                    // 重试的时间间隔，默认5秒
    //private String                  zkURL;                                                 //zk地址

    private ZkClient client;                                                                 //zk客户端


    public ZKClientZK(String zk,String app, Store store){
        client = new ZkClient(zk,soTimeout);
        this.APP_PATH = app;
        this.store = store;
    }


    /**
     * 删除zk中配置
     *
     * @param path
     */
    @Override
    protected void removeRemote(String path) throws Exception {
        client.delete(path);
    }

    /**
     * 更新zk
     *
     * @param path
     * @param value
     */
    @Override
    protected void setRemote(String path, String value) throws Exception {
        boolean exit = client.exists(path);
        if(exit){
            client.writeData(path,value);
        }else {
            client.createPersistent(path,true);
            client.writeData(path,value);
        }
    }

    /**
     * 添加监听
     */
    @Override
    protected void watcher(){
            client.subscribeChildChanges(getAppPath(), new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    log.error("S:{} , list:{}",s,list);
                }
            });
    }

    /**
     * 获得监控信息
     *
     * @return
     */
    @Override
    public Monitor getMonitor() {
        if(monitor == null) {
            monitor = new ZKClientMonitor(client);
        }
        return monitor;
    }

    class ZKClientMonitor implements Monitor{

        private ZkClient client;
        public ZKClientMonitor(ZkClient c){
            this.client = c;
        }

        /**
         * 获得服务器列表
         *
         * @return
         */
        @Override
        public List<String> getServerList() {
            return client.getChildren(getClusterPath());
        }

        /**
         * 获得配置信息列表
         *
         * @return
         */
        @Override
        public List<Node> getConfigList() {
            List<String> child = client.getChildren(getAppPath());
            List<Node> nodes = Lists.newLinkedList();
            for (String s : child) {
                Node node = new Node(s, (String) client.readData(getAppPath() + ZOOKEEPER_SEPARATOR + s));
                nodes.add(node);
            }
            return nodes;
        }
    }

    /**
     * 注册服务ip
     */
    @Override
    public void registeServer() {
        client.createEphemeral(getClusterPath() + ZOOKEEPER_SEPARATOR + AddressUtils.getHostIp());
    }
}
