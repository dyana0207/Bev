package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;


public class GameApplication extends Application {
    @Override
    public void start(final Stage stage) throws IOException {
        Logger.info("Starting application");
        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/sign_up.fxml")
        );        stage.setTitle("Soldier logic game");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
