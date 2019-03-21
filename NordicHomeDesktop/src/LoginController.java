import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;

public class LoginController {

    @FXML
    private JFXButton btnNextPage;

    @FXML
    private AnchorPane loginScene;


    /**
     * SendToNetworksPage: Method that sends the user to the next page, the Networks page
     * @author Julie and Kaja
     */
    public void SendToNetworksPage(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("resources/Networks.fxml"));
        loginScene.getChildren().setAll(pane);
    }

}
