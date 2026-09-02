package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MenuController {

    private static final String RUTA_VISTAS =
            "/ni/edu/uam/pae_eventos_javafx/views/";

    @FXML
    private void abrirPulperiaOnAction() {

        abrirVentana(
                "inventario-pulperia-view.fxml",
                "Inventario de pulpería"
        );
    }

    @FXML
    private void abrirCafeOnAction() {

        abrirVentana(
                "recepcion-cafe-view.fxml",
                "Recepción de café"
        );
    }

    @FXML
    private void abrirArtesaniasOnAction() {

        abrirVentana(
                "tienda-artesanias-view.fxml",
                "Tienda de artesanías"
        );
    }

    private void abrirVentana(
            String nombreFXML,
            String titulo) {

        try {

            String rutaCompleta =
                    RUTA_VISTAS + nombreFXML;

            URL recurso = MenuController.class
                    .getResource(rutaCompleta);

            if (recurso == null) {

                mostrarError(
                        "No se encontró el archivo:\n"
                                + rutaCompleta
                );

                return;
            }

            FXMLLoader loader =
                    new FXMLLoader(recurso);

            Parent root = loader.load();

            Stage ventana = new Stage();

            ventana.setTitle(titulo);
            ventana.setScene(new Scene(root));
            ventana.show();
            ventana.centerOnScreen();

        } catch (IOException e) {

            e.printStackTrace();

            mostrarError(
                    "No se pudo abrir la ventana.\n"
                            + e.getMessage()
            );
        }
    }

    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error al abrir la ventana");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}