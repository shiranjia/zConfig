package com.zConfig.zk;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.zConfig.monitor.Monitor;
import com.zConfig.monitor.Node;
import com.zConfig.store.Store;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.io.EOFException;
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
        init();
        Monitor monitor = getMonitor();
        List<Node> configs = monitor.getConfigList();

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
     * 监听数据节点
     * @param path
     */
    private void subscribeDataChanges(String path){
        client.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                store.set(getNodeKey(dataPath),(String)data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                store.remove(getNodeKey(dataPath));
            }
        });
    }

    /**
     * 添加监听
     */
    @Override
    protected void watcher(){
        client.subscribeChildChanges(getDatePath(), new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    log.error("S:{} , list:{}",s,list);
                    Monitor monitor = getMonitor();
                    List<Node> configs = monitor.getConfigList();
                    for (Node n : configs){
                        store.set(n.getKey(),n.getValue());
                    }
                }
        });
        Monitor monitor = getMonitor();
        List<Node> configs = monitor.getConfigList();
        for (Node n : configs){
            subscribeDataChanges(getNodePath(n.getKey()));
        }
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
            List<String> child = client.getChildren(getDatePath());
            List<Node> nodes = Lists.newLinkedList();
            for (String s : child) {
                try {
                    Optional<Object> data = Optional.fromNullable(client.readData(getNodePath(s)));
                    Node node = new Node(s, (String) data.or(""));
                    nodes.add(node);
                }catch (ZkMarshallingError e){
                    Node node = new Node(s, "");
                    nodes.add(node);
                }
            }
            return nodes;
        }
    }

    /**
     * 注册服务ip
     */
    @Override
    public void registeServer() {
        try {
            client.createEphemeral(host());
        }catch (ZkNodeExistsException e){}
    }
}
