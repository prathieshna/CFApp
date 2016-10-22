/**
 * Created by Prathieshna.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SocketListener implements Runnable {
    ServerSocket listener = null;
    Socket socket = null;
    volatile boolean pause = false;

    public void pause() throws IOException {
        listener.close();
        pause = true;
    }

    public void resume() {
        pause = false;
    }


    public void run() {
        while (true) {
            try {
                try {
                    listener = new ServerSocket(38301);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Keeping the socket Open
                while (!pause) {
                    // Opening a socket on 38301 for communication
                    System.out.println("SERVICE STARTED");
                    // Accepting client Socket
                    String Status = ResourceUsage.getStatus();
                    if (listener != null) {
                        this.socket = listener.accept();
                    }
                    try {
                        String input = null;
                        Scanner sc = null;
                        PrintWriter out = null;
                        String[] params;

                        if (socket != null) {
                            // INPUT Stream from socket
                            sc = new Scanner(socket.getInputStream());
                            // Output Stream for socket
                            out = new PrintWriter(socket.getOutputStream(), true);
                        }
                        if (sc != null && sc.hasNext()) {
                            input = sc.nextLine();
                            params = input.split(",");
                            if (!input.equals("context")) {
                                ADB.execAdb();
                                File file = new File(params[0] + ".apk");
                                String cachedMD5 = Util.crunchifyGetMd5ForFile(file);
                                String androidMD5 = ADB.getMD5(params[0]);
                                if (!file.exists()) {
                                    System.out.println("FILE DOES NOT EXIST");
                                    ADB.getAPK(file, params[0]);
                                    ADB.decompile(params[0]);
                                }
                                else if (!cachedMD5.equals(androidMD5))
                                {
                                    System.out.println("MD5 Don't match");
                                    File file2 = new File(params[0] + ".jar");
                                    boolean bool  = file.delete();
                                    boolean bool2 = file2.delete();
                                    if(bool && bool2)
                                    {
                                        ADB.getAPK(file, params[0]);
                                        ADB.decompile(params[0]);
                                    }
                                    else
                                    {
                                        System.out.println("PROBLEM");
                                    }
                                }
                            }
                        }
                        if (out != null && input != null && !input.equals("context")) {
                            params = input.split(",");
                            out.println(ServiceDriver.worker(System.getProperty("user.dir") + "/" + params[0] + ".jar", params[1], params[2]));
                        } else if (out != null && input != null && input.equals("context")) {
                            out.println(Status + "," + ADB.getCurrent() + "," + ADB.getVoltage() + "," + ADB.getLevel());
                        }

                    } finally {

                        if (socket != null) {
                            socket.close();
                        }
                    }
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                if (listener != null) {
                    try {
                        listener.close();
                    } catch (Exception e) {
//                    e.printStackTrace();
                    }
                }
            }
        }
    }
}
