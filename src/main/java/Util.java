import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by Prathieshna.
 */
public class Util {

    public static String getIp(){
        try {
            Socket s = new Socket("google.com", 80);
            String ip = s.getLocalAddress().getHostAddress();
            s.close();
            return ip;
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public static String getStatus(String ipAddress)
    {
        StringBuilder s = new StringBuilder();
        s.append("Surrogate Service Running \n");
        s.append("IP: "+ipAddress+"\n");
        s.append("PORT: 38301");
        s.append("\n");
        s.append("AC Status and Battery Percentage: "+ResourceUsage.batteryStatus()+"\n");
        s.append("Free Memory Available: "+ResourceUsage.getFreeMemory()+"MB\n");
        s.append("Total Active Cores: "+ResourceUsage.getNumnberOfCores()+"\n");
        return s.toString();
    }

    public static String crunchifyGetMd5ForFile(File crunchifyFile) {
        String crunchifyValue = null;
        FileInputStream crunchifyInputStream = null;
        try {
            crunchifyInputStream = new FileInputStream(crunchifyFile);
            crunchifyValue = DigestUtils.md5Hex(IOUtils.toByteArray(crunchifyInputStream));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            IOUtils.closeQuietly(crunchifyInputStream);
        }
        return crunchifyValue;
    }
}

