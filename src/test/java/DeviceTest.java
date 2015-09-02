public class DeviceTest extends junit.framework.TestCase {

    public void testInitConnect() throws Exception {
        Device D = Device.getInstance();
        assertTrue(D.initListener());
    }
}