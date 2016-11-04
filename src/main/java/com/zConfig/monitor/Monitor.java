package com.zConfig.monitor;

import java.util.List;

/**
 * 配置信息监控
 * Created by jiashiran on 2016/11/4.
 */
public abstract class Monitor {

    /**
     * 获得服务器列表
     * @return
     */
    public abstract List<String> getServerList();

    /**
     * 获得配置信息列表
     * @return
     */
    public abstract List<Node> getConfigList();

    /**
     * 节点数据
     */
    protected final class Node{
        private String key;
        private String value;
        public Node(String k ,String v){
            this.key = k;
            this.value = v;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
