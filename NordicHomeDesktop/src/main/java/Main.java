import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = new File("src/main/java/resources/Login.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        //Parent root = FXMLLoader.load(getClass().getResource("resources/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        //scene.getStylesheets().add(getClass().getResource("resources/styling.css").toExternalForm());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
