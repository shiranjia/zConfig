## zConfig 
##### a Uniform configuration tool by zk

____________________________________________________________________________________________________________________________________
### example
##### String zk = "192.168.150.119,192.168.150.120,192.168.150.121:2181"; <br/>
##### String app = "test";  <br/>
##### ZConfig config = config.newCuratorClientConfig(zk,app); <br/>
##### config.set("t1","config1"); <br/>
##### Monitor monitor = config.getMonitor();  <br/>
##### List\<Node\>  nodes = monitor.getConfigList(); <br/>
##### List\<String\> servers = monitor.getServerList(); <br/>

