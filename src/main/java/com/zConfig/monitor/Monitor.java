package com.zConfig.monitor;

import java.util.List;

/**
 * 配置信息监控
 * Created by jiashiran on 2016/11/4.
 */
public interface Monitor {

    /**
     * 获得服务器列表
     * @return
     */
    List<String> getServerList();

    /**
     * 获得配置信息列表
     * @return
     */
    List<Node> getConfigList();

}
