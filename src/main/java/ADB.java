import java.io.*;
import java.util.Scanner;

/**
 * Created by Prathieshna.
 */
public class ADB {
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

    public static Double getCurrent()
    {
        String current = "0.0";
        ProcessBuilder pb = new ProcessBuilder("adb","shell","dumpsys","batteryproperties","|","grep","\\\"current now\\\"");
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            current = reader.readLine().split(" ")[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.parseDouble(current)/1000000.0;
    }

    public static String getVoltage()
    {
        String voltage = "";
        ProcessBuilder  pb = new ProcessBuilder("adb","shell","dumpsys","battery","|","grep","voltage");
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            voltage = reader.readLine().split(" +")[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voltage;
    }

    public static String getLevel()
    {
        String level = "";
        ProcessBuilder pb = new ProcessBuilder("adb","shell","dumpsys","battery","|","grep","level");
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            level = reader.readLine().split(" +")[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    public static void getAPK(File file, String packageName)
    {
        String line = "";
        ProcessBuilder pb = new ProcessBuilder("adb", "shell", "pm", "path", packageName);
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
            File apk = new File("base.apk");
            boolean success = apk.renameTo(file);
            if (!success) {
                System.err.println("FILE RENAMING FAILED!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decompile(String packageName)
    {
        ProcessBuilder pb = new ProcessBuilder("sh", "d2j-dex2jar.sh", "-f", "-o", packageName + ".jar", packageName + ".apk").inheritIO();
        try {
            Process pc = pb.start();
            pc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getMD5(String packageName)
    {
        String line = "";
        ProcessBuilder pb = new ProcessBuilder("adb", "shell", "pm", "path", packageName);
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        pb = new ProcessBuilder("adb", "shell","md5sum", line.split(":")[1]);
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line.split(" ")[0];
    }

}
