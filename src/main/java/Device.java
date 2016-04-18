import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Prathieshna.
 */

public class Device {

    public static void execAdb() {
        try {
            Process p = Runtime.getRuntime().exec("adb.exe forward tcp:38300 tcp:38300");
            Scanner sc = new Scanner(p.getErrorStream());
            if (sc.hasNext()) {
                while (sc.hasNext()) System.out.println(sc.next());
                System.err.println("Cannot start the Android debug bridge");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final Thread cyberForagingWorkerThread;
        final InitializeConnection worker = new InitializeConnection();
        cyberForagingWorkerThread = new Thread(worker);

        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported.");
            return;
        }

        SystemTray systemTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(Device.class.getResource("pause.png"));

        final TrayIcon trayIcon = new TrayIcon(image);

        final PopupMenu trayPopupMenu = new PopupMenu();

        MenuItem startService = new MenuItem("Start Service");
        startService.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Service Started", "Surrogate Service", JOptionPane.INFORMATION_MESSAGE);
                try {
                    cyberForagingWorkerThread.start();
                    Image image = Toolkit.getDefaultToolkit().getImage(Device.class.getResource("cyber.gif"));
                    trayIcon.setImage(image);
                } catch (Exception err) {
                    Image image = Toolkit.getDefaultToolkit().getImage(Device.class.getResource("cyber.gif"));
                    trayIcon.setImage(image);
                    worker.resume();
                }
            }
        });
        trayPopupMenu.add(startService);

        MenuItem action = new MenuItem("Stop Service");
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Service Stopped", "Surrogate Service", JOptionPane.INFORMATION_MESSAGE);
                try {
                    worker.pause();
                    Image image = Toolkit.getDefaultToolkit().getImage(Device.class.getResource("pause.png"));
                    trayIcon.setImage(image);
                } catch (Exception e1) {
                    System.err.println("Service has not stared yet");
                }
            }
        });
        trayPopupMenu.add(action);

        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayPopupMenu.add(close);

        trayIcon.setPopupMenu(trayPopupMenu);

        trayIcon.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                trayIcon.setToolTip(ResourceUsage.batteryStatus());
            }
        });
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }
    }

    public static Object getClassNames(String pathToJar, String className, String methodName) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jarFile != null) {
            Enumeration e = jarFile.entries();
            while (e.hasMoreElements()) {
                try {
                URL[] urls;
                urls = new URL[]{new URL("jar:file:" + pathToJar + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);
                JarEntry je = (JarEntry) e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }

                // -6 because of .class
                String tempClassName = je.getName().substring(0, je.getName().length() - 6);
                tempClassName = tempClassName.replace('/', '.');

                    if (className.equals(tempClassName)) {
                        System.out.println("CLASS FOUND");
                        Class<?> myClass = cl.loadClass(className);
                        Object whatInstance = myClass.newInstance();
                        Method myMethod = myClass.getMethod(methodName, new Class[]{});
                        return myMethod.invoke(whatInstance);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException| MalformedURLException | IllegalAccessException | InvocationTargetException e1) {
                    System.err.println(e1);
                }
            }
        }
        return null;
    }
}
