import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class NetworkTest {
    private Network network = null;
    private String name;
    private String address;



    @BeforeEach
    void setUp() {
        name = "testnetwork";
        address = "testaddress";
        network = new Network(name, address);
        network.addGroup("Bad");
    }


    @Test
    void testGetters(){
        assertEquals("testnetwork", network.getName());
        assertEquals("testaddress", network.getAddress());
        assertEquals("[Bad]", network.getGroups().toString());
        assertEquals("[On, Off, Dimmed]", network.getFullGroups().get("Bad").toString());
    }
}