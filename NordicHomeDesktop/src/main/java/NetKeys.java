import com.google.api.services.drive.Drive;

import java.util.Locale;
import java.util.UUID;


public class NetKeys {

    private String name, key, minSecurity, StringTimestamp;
    private Integer index, phase;
    private Long timestamp;

    public NetKeys (String name, Integer index, String key, Integer phase, String minSecurity, String timestamp) {
        this.name = name;
        this.index = index;
        this.key = key;
        this.phase = phase;
        this.minSecurity = minSecurity;
        this.StringTimestamp = timestamp;


    }
    public NetKeys () {
        this.name = "netKey";
        this.index = 0;
        this.key = UUID.randomUUID().toString().toUpperCase(Locale.US).replaceAll("-", "");
        this.phase = 0;
        this.minSecurity = "low";
        this.timestamp = DriveQuickstart.getInternationalAtomicTime(System.currentTimeMillis());
    }
}