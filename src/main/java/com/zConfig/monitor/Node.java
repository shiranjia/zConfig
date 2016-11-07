package com.zConfig.monitor;

/**
 * Created by jiashiran on 2016/11/7.
 */
public class Node {

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
