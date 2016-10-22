/**
 * Created by Prathieshna.
 */

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import java.util.ArrayList;
import java.util.List;


public interface Kernel32 extends StdCallLibrary {

    public Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);

    public class SYSTEM_POWER_STATUS extends Structure {
        public byte ACLineStatus;
        public byte BatteryFlag;
        public byte BatteryLifePercent;
        public int BatteryLifeTime;
        public int BatteryFullLifeTime;

        @Override
        protected List<String> getFieldOrder() {
            ArrayList<String> fields = new ArrayList<String>();
            fields.add("ACLineStatus");
            fields.add("BatteryFlag");
            fields.add("BatteryLifePercent");
            fields.add("BatteryLifeTime");
            fields.add("BatteryFullLifeTime");
            return fields;
        }

        /**
         * The AC power status
         */
        public String getACLineStatusString() {
            switch (ACLineStatus) {
                case (0): return "Offline";
                case (1): return "Online";
                default: return "Unknown";
            }
        }

        /**
         * The battery charge status
         */
        public String getBatteryFlagString() {
            switch (BatteryFlag) {
                case (1): return "High, more than 66 percent";
                case (2): return "Low, less than 33 percent";
                case (4): return "Critical, less than five percent";
                case (8): return "Charging";
                case ((byte) 128): return "No system battery";
                default: return "Unknown";
            }
        }

        /**
         * The percentage of full battery charge remaining
         */
        public int getBatteryLifePercent() {
            return (BatteryLifePercent == (byte) 255) ? -1 : BatteryLifePercent;
        }

        /**
         * The number of seconds of battery life remaining
         */
        public int getBatteryLifeTime() {
            return (BatteryLifeTime == -1) ? -1 : BatteryLifeTime;
        }

        /**
         * The number of seconds of battery life when at full charge
         */
        public int getBatteryFullLifeTime() {
            return (BatteryFullLifeTime == -1) ? -1 : BatteryFullLifeTime;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
//            sb.append("ACLineStatus: " + getACLineStatusString() + "\n");
//            sb.append("Battery Flag: " + getBatteryFlagString() + "\n");
//            sb.append("Battery Life: " + ((BatteryLifePercent == -1) ? "Unknown" : BatteryLifePercent + "%" )+ "\n");
//            sb.append("Battery Left: " + ((BatteryLifeTime == -1) ? "Unknown" : BatteryLifeTime + " seconds" )+ "\n");
//            sb.append("Battery Full: " + ((BatteryFullLifeTime == -1) ? "Unknown" : BatteryFullLifeTime + " seconds" )+ "\n");
            sb.append(getACLineStatusString()+","+BatteryLifePercent);
            return sb.toString();
        }
    }

    /**
     * Fill the structure.
     */
    public int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);

}