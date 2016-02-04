import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

/**
 * Created by Prathieshna.
 */

public class Device {

    //Singleton
    private static Device instance = null;

    private Device() {
        // Exists only to defeat instantiation.
    }

    public static Device getInstance() {
        if (instance == null) {
            instance = new Device();
        }
        return instance;
    }

    public static boolean initListener() {
        //Initializes the ddm library.
        //This must be called once before any call to createBridge(java.lang.String,boolean).
        //The library only monitors devices. The applications are left untouched, letting other tools built on ddmlib
        //to connect a debugger to them.
        AndroidDebugBridge.init(false);

        //Creates a new debug bridge from the location of the command line tool.
        //Any existing server will be disconnected, unless the location is the same and forceNewBridge is set to false.
        AndroidDebugBridge debugBridge = AndroidDebugBridge.createBridge("D:\\adb.exe", true);

        //NULL Check for debugBridge
        if (debugBridge == null) {
            System.err.println("Invalid ADB location.");
            return false;
        }

        //Classes which implement this interface provide methods that deal with IDevice addition, deletion, and changes.
        AndroidDebugBridge.IDeviceChangeListener myListener = new AndroidDebugBridge.IDeviceChangeListener() {


            public void deviceChanged(IDevice device, int arg1) {
                // not implemented
            }


            public void deviceConnected(IDevice device) {
                System.out.println(String.format("%s connected", device.getSerialNumber()));
            }


            public void deviceDisconnected(IDevice device) {
                System.out.println(String.format("%s disconnected", device.getSerialNumber()));

            }

        };

        //Adds the listener to the collection of listeners who will be notified when a Client property changed,
        //by sending it one of the messages defined in the AndroidDebugBridge.IClientChangeListener interface.
        AndroidDebugBridge.addDeviceChangeListener(myListener);
        return true;
    }

    public static void main(String[] args) {
        Device.initListener();

    }
}
