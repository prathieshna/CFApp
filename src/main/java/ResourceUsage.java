/**
 * Created by Prathieshna.
 */
public class ResourceUsage {

    public static String batteryStatus() {
        Kernel32.SYSTEM_POWER_STATUS batteryStatus = new Kernel32.SYSTEM_POWER_STATUS();
        Kernel32.INSTANCE.GetSystemPowerStatus(batteryStatus);
        return batteryStatus.toString();
    }

    public int getBatteryLifePercent() {
        return new Kernel32.SYSTEM_POWER_STATUS().getBatteryLifePercent();
    }


    public int getBatteryLifeTime() {
        return new Kernel32.SYSTEM_POWER_STATUS().getBatteryLifeTime();
    }


    public int getBatteryFullLifeTime() {
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

}
