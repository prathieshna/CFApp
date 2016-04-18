import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Prathieshna.
 */
public class InitializeConnection implements Runnable {
    ServerSocket listener = null;
    Socket socket = null;
    volatile boolean  pause = false;

    public void pause() throws IOException {
        listener.close();
        pause = true;
    }

    public void resume() {
        pause = false;
    }


    public void run() {

        while (true){

        // Opening a socket on 38301 for communication
        try {
            listener = new ServerSocket(38301);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Keeping the socket Open

            while (!pause) {
                System.out.println("SERVICE STARTED");
                // Accepting client Socket

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
                            Device.execAdb();
                            String line = "";
                            File file = new File(params[0] + ".apk");
                            if (!file.exists()) {
                                ProcessBuilder pb = new ProcessBuilder("adb", "shell", "pm", "path", params[0]);
                                try {
                                    Process pc = pb.start();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
                                    pc.waitFor();
                                    line = reader.readLine();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                pb = new ProcessBuilder("adb", "pull", line.split(":")[1]).inheritIO();
                                try {
                                    Process pc = pb.start();
                                    pc.waitFor();
                                    System.out.println("FETCHED APK");
                                    File apk = new File("base.apk");
                                    boolean success = apk.renameTo(file);
                                    if (!success) {
                                        System.err.println("FILE RENAMING FAILED!");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                pb = new ProcessBuilder("sh", "d2j-dex2jar.sh", "-f", "-o", params[0] + ".jar", params[0] + ".apk").inheritIO();
                                try {
                                    Process pc = pb.start();
                                    pc.waitFor();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                System.out.println("DE-COMPILATION FINISHED");
                            }
                        }

                    }

                    if (out != null && input != null && !input.equals("context")) {
                        params = input.split(",");
                        out.println(Device.getClassNames(System.getProperty("user.dir") +"/" +params[0] + ".jar", params[1], params[2]));
                    } else if (out != null && input != null && input.equals("context")) {
                        out.println(ResourceUsage.batteryStatus());
                    }

                } finally {

                    if (socket != null) {
                        socket.close();
                    }
                }
            }

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
