package ni.edu.uam.pae_eventos_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage)
            throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        MainApplication.class.getResource(
                                "views/menu-principal-view.fxml"
                        )
                );

        Scene scene =
                new Scene(loader.load());

        stage.setTitle(
                "Ejercicios de eventos JavaFX"
        );

        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}