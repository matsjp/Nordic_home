import java.util.Locale;
import java.util.UUID;

public class AppKeys {

    private String name, key, oldKey;
    private Integer index, boundNetKey;

    public AppKeys (String name, String key, String oldKey, Integer index, Integer boundNetKey) {
        this.name = name;
        this.key = key;
        this.oldKey = oldKey;
        this.index = index;
        this.boundNetKey = boundNetKey;
    }
    public AppKeys () {
        this.name = "appKey";
        this.index = 0;
        this.boundNetKey = 0;
        this.key = UUID.randomUUID().toString().toUpperCase(Locale.US).replaceAll("-","");
    }

    //TODO: SCENER SKAL HA HEX
}