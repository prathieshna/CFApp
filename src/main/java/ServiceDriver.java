/**
 * Created by Prathieshna.
 */

import android.bluetooth.BluetoothClass;
import sun.nio.cs.Surrogate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ServiceDriver {
    public static void main(String[] args) {
        System.out.println(ResourceUsage.getStatus());
        final String ipAddress = Util.getIp();
        final Thread listener;
        final SocketListener socketListener = new SocketListener();
        listener = new Thread(socketListener);
        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported.");
            return;
        }

        SystemTray systemTray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().getImage(ServiceDriver.class.getResource("pause.png"));

        final TrayIcon trayIcon = new TrayIcon(image);

        final PopupMenu trayPopupMenu = new PopupMenu();

        MenuItem startService = new MenuItem("Start Service");
        startService.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Service Started", "Surrogate Service", JOptionPane.INFORMATION_MESSAGE);
                try {
                    listener.start();
                    Image image = Toolkit.getDefaultToolkit().getImage(ServiceDriver.class.getResource("cyber.gif"));
                    trayIcon.setImage(image);
                } catch (Exception err) {
                    Image image = Toolkit.getDefaultToolkit().getImage(ServiceDriver.class.getResource("cyber.gif"));
                    trayIcon.setImage(image);
                    socketListener.resume();
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
                    socketListener.pause();
                    Image image = Toolkit.getDefaultToolkit().getImage(ServiceDriver.class.getResource("pause.png"));
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
                trayIcon.setToolTip(Util.getStatus(ipAddress));
            }
        });
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }
    }

    public static Object worker(String pathToJar, String className, String methodName) {
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
                        Class<?> myClass = cl.loadClass(className);
                        Object whatInstance = myClass.newInstance();
                        Method myMethod = myClass.getMethod(methodName, new Class[]{});
                        return myMethod.invoke(whatInstance);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | MalformedURLException | IllegalAccessException | InvocationTargetException e1) {
                    System.err.println(e1);
                } finally {
                    try {
                        jarFile.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
