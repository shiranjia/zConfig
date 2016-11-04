package com.zConfig.zk;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.zConfig.monitor.Monitor;
import com.zConfig.store.Store;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * Created by jiashiran on 2016/11/4.
 */
public class CuratorZK extends ZKClient{
    private int                     soTimeout     = 5000;                                    //zk链接超时时间
    private int                     retryTimes    = 3;                                       //重试链接次数
    private int                     retryInterval = 3000;                                    // 重试的时间间隔，默认5秒
    private String                  zkURL;                                                   //zk地址

    private CuratorFramework        client;                                                  //zk客户端

    private Monitor                 monitor;

    public CuratorZK(String url, String path, Store store){
        this.zkURL = url;
        this.APP_PATH = path;
        this.client = CuratorFrameworkFactory.builder().connectString(zkURL)
                .sessionTimeoutMs(soTimeout)
                .retryPolicy(new ExponentialBackoffRetry(retryTimes,retryInterval))
                .build();
        this.store = store;
        client.start();
    }


    /**
     * 删除zk中配置
     *
     * @param path
     */
    @Override
    protected void removeRemote(String path) throws Exception {
        Optional<Stat> stat = Optional.fromNullable(client.checkExists().forPath(path));
        if(stat.isPresent()){
            this.client.delete().guaranteed().withVersion(stat.get().getVersion()).forPath(path);
        }
    }

    /**
     * 更新zk
     *
     * @param path
     * @param value
     */
    @Override
    protected void setRemote(String path, String value) throws Exception {
        Optional<Stat> stat = Optional.fromNullable(client.checkExists().forPath(path));
        if(stat.isPresent()){//has path
            client.setData().withVersion(stat.get().getVersion()).forPath(path,value.getBytes(Charsets.UTF_8));

        }else {//no path
            client.create().creatingParentsIfNeeded().forPath(path,value.getBytes(Charsets.UTF_8));
        }
    }

    /**
     * 添加监听
     */
    @Override
    protected void watcher() throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client,getAppPath(),true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                ChildData data = event.getData();
                switch (event.getType()){
                    case CHILD_ADDED:{
                        log.error("CHILD_ADDED:",data);
                        store.set(getNodeKey(data.getPath()),new String(data.getData(),Charsets.UTF_8));
                        break;
                    }
                    case CHILD_UPDATED:{
                        log.error("CHILD_UPDATED:",data);
                        store.set(getNodeKey(data.getPath()),new String(data.getData(),Charsets.UTF_8));
                        break;
                    }
                    case CHILD_REMOVED:{
                        log.error("CHILD_REMOVED:",data);
                        remove(getNodeKey(data.getPath()));
                        break;
                    }
                    default:log.info("default:",event.getData());break;
                }
            }
        });
    }

    /**
     * 获得监控信息
     * @return
     */
    @Override
    public Monitor getMonitor() {
        if(monitor == null){
            monitor = new Monitor() {
                @Override
                public List<String> getServerList() {
                    try {
                        return client.getChildren().forPath(getAppPath());
                    } catch (Exception e) {
                        //e.printStackTrace();
                        log.error("getMonitor.getServerList.exception : " ,e);
                        return null;
                    }
                }

                @Override
                public List<Monitor.Node> getConfigList() {
                    try {
                        List<String> children = client.getChildren().forPath(getAppPath());
                        List<Monitor.Node> nodes = Lists.newArrayList();
                        for (String s : children){
                            String value = new String(client.getData().forPath(get(s)),Charsets.UTF_8);
                            nodes.add(new Node(s,value));
                        }
                        return nodes;
                    } catch (Exception e) {
                        //e.printStackTrace();
                        log.error("getMonitor.getConfigList.exception : " ,e);
                        return null;
                    }
                }
            };
        }
        return monitor;
    }
}
