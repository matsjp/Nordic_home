import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

public class Scenes {
    private String name;
    private ArrayList <String> addresses;
    private Integer number;

    public Scenes (String name, String address, Integer number) {
        this.name = name;
        this.addresses = new ArrayList<>();
        this.addresses.add(address);
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public ArrayList getAddresses() {
        return addresses;
    }
}