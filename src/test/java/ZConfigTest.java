import com.zConfig.ZConfig;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiashiran on 2016/11/4.
 */
public class ZConfigTest {

    private static ZConfig config;
    private static String zk = "192.168.150.119,192.168.150.120,192.168.150.121:2181";
    private static String app = "test";

    @Before
    public void before(){
        config = config.newCuratorClientConfig(zk,app);
    }

    @Test
    public void testSet(){
        config.set("123","dddsa");
    }

    @Test
    public void testGet(){
        String v = config.get("123");
        Assert.assertEquals( "dddsa" , v);
    }

    @Test
    public void test(){
        Assert.assertEquals("123","123");
    }

    public static void main(String[] args) {
        config = config.newCuratorClientConfig(zk,app);
        config.set("123","阿克苏粉色发");
        String v = config.get("123");
        config.remove("123");
        //Assert.assertEquals( "dddsa" , v);
    }
}
