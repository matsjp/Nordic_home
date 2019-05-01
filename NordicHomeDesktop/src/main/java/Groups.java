import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

public class Groups {

    private String name;
    private int address, parentAddress;

    public Groups (String name, int address, int parentAddress) {
        this.name = name;
        this.address = address;
        this.parentAddress = parentAddress;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public int getParentAddress() {
        return parentAddress;
    }

}