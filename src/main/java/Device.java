import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        ProcessBuilder pb = new ProcessBuilder("adb", "pull", "/data/app/com.iit.prathieshna.myapplication-1/base.apk").inheritIO();
        try {
            Process pc = pb.start();
            pc.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("FETCHED APK");

        pb = new ProcessBuilder("sh", "d2j-dex2jar.sh", "-f", "-o", "output.jar", "base.apk").inheritIO();
        try {
            Process pc = pb.start();
            pc.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("DE-COMPILATION FINISHED");

        Device.getClasseNames("output.jar", "com.iit.prathieshna.myapplication.HelloWorld");
    }

    public static void getClasseNames(String pathToJar, String className1) {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert jarFile != null;

        Enumeration e = jarFile.entries();

        URL[] urls = new URL[0];

        try {
            urls = new URL[]{new URL("jar:file:" + pathToJar + "!/")};
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = (JarEntry) e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }

            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            try {
                if (className.equals(className1)) {
                    System.out.println("CLASS FOUND");
                    Class <?> myClass = cl.loadClass(className);
                    Object whatInstance = myClass.newInstance();
                    Method myMethod = myClass.getMethod("helloworld", new Class[]{});
                    myMethod.invoke(whatInstance);
                }

            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
    }
}
