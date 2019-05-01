import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

public class Scenes {
    private String name;
    private ArrayList <String> addresses;
    private int number;

    public Scenes (String name, String address, int number) {
        this.name = name;
        this.addresses = new ArrayList<>();
        this.addresses.add(address);
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public ArrayList getAddresses() {
        return addresses;
    }
}