import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

public class Groups {

    private String name, address, parentAddress;

    public Groups (String name, String address, String parentAddress) {
        this.name = name;
        this.address = address;
        this.parentAddress = parentAddress;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getParentAddress() {
        return parentAddress;
    }

}