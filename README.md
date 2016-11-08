# zConfig 
a Uniform configuration tool by zk

##example
String zk = "192.168.150.119,192.168.150.120,192.168.150.121:2181";
String app = "test";
ZConfig config = config.newCuratorClientConfig(zk,app);
config.set("t1","config1");
Monitor monitor = config.getMonitor();
List<Node> nodes = monitor.getConfigList();
List<String> servers = monitor.getServerList();

