public class NetKeys {

    private String name, key, oldKey, minSecurity, timestamp;
    private Integer index, phase;

    public NetKeys (String name, Integer index, String key, String oldKey, Integer phase, String minSecurity, String timestamp) {
        this.name = name;
        this.index = index;
        this.key = key;
        this.oldKey = oldKey;
        this.phase = phase;
        this.minSecurity = minSecurity;
        this.timestamp = timestamp;

    }
}