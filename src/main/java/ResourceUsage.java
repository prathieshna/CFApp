import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Prathieshna.
 */

public class ResourceUsage {

    public static String batteryStatus() {
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        return batteryStatus.toString();
    }

    public static String getStatus(){
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        String [] results = batteryStatus.toString().split(",");
        String line = "";
        StringBuffer stringBuffer = new StringBuffer("");
        int percentage = -1;
        ProcessBuilder pb = new ProcessBuilder("wmic", "cpu","get","loadpercentage");
        try {
            Process pc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            pc.waitFor();
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append(",");
            }
            percentage = Integer.parseInt(stringBuffer.toString().split(",")[2].trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String status = results[0]+","+results[1]+","+getCPUload(percentage)+","+(getFreeMemory()/(1024*1024));
        return  results[0]+","+results[1]+","+getCPUload(percentage)+","+(getFreeMemory()/(1024*1024))+","+Index(status);
    }

    public static int getBatteryLifePercent() {
        return (int) new Kernel32.SYSTEM_POWER_STATUS().BatteryLifePercent;
    }


    public static int getBatteryLifeTime() {
        return new Kernel32.SYSTEM_POWER_STATUS().getBatteryLifeTime();
    }


    public static int getBatteryFullLifeTime() {
        return new Kernel32.SYSTEM_POWER_STATUS().getBatteryFullLifeTime();
    }

    public static String getACLineStatusString() {
        return new Kernel32.SYSTEM_POWER_STATUS().getACLineStatusString();
    }


    public static int getNumnberOfCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static String getCPUload(int load)
    {
        if( load < 33)
        {
            return "LOW";
        }
        else if (load < 66)
        {
            return "MEDIUM";
        }
        else
        {
            return "HIGH";
        }

    }

    public static int  Index(String str) {
       int Index = 0;
       String values[] = str.split(",");
       if (values[0].equals("Online"))
       {
           Index += 3;
       }else if (values[0].equals("Offline"))
       {
           Index += (Integer.parseInt(values[1]))/100*3;
       }
       if  (values[2].equals("MEDIUM"))
       {
           Index += 1;
       }
       else  if  (values[2].equals("LOW"))
       {
           Index += 2;
       }
       if (Integer.parseInt(values[3])>200)
       {
           Index += 2;
       }
       else
       {
           Index += (Integer.parseInt(values[3]))/200*2;
       }
       if (getNumnberOfCores() < 16)
       {
           Index += getNumnberOfCores()/16*3;
       }
       else
       {
           Index += 3;
       }
       return Index;
    }
}
