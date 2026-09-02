package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private void abrirPulperiaOnAction(
            ActionEvent event) {

        abrirVentana(
                event,
                "inventario-pulperia-view.fxml",
                "Inventario de pulpería"
        );
    }

    @FXML
    private void abrirCafeOnAction(
            ActionEvent event) {

        abrirVentana(
                event,
                "recepcion-cafe-view.fxml",
                "Recepción de café"
        );
    }

    @FXML
    private void abrirArtesaniasOnAction(
            ActionEvent event) {

        abrirVentana(
                event,
                "tienda-artesanias-view.fxml",
                "Tienda de artesanías"
        );
    }

    private void abrirVentana(
            ActionEvent event,
            String nombreFXML,
            String titulo) {

        try {

            String rutaCompleta =
                    RUTA_VISTAS + nombreFXML;

            URL recurso =
                    MenuController.class
                            .getResource(
                                    rutaCompleta
                            );

            if (recurso == null) {

                mostrarError(
                        "No se encontró el archivo:\n"
                                + rutaCompleta
                );

                return;
            }

            FXMLLoader loader =
                    new FXMLLoader(recurso);

            Parent root =
                    loader.load();

            Stage ventanaEjercicio =
                    new Stage();

            ventanaEjercicio.setTitle(titulo);

            ventanaEjercicio.setScene(
                    new Scene(root)
            );

            ventanaEjercicio.show();
            ventanaEjercicio.centerOnScreen();

            /*
             * Se cierra el menú solamente después
             * de abrir correctamente el ejercicio.
             */
            cerrarVentanaMenu(event);

        } catch (IOException e) {

            e.printStackTrace();

            mostrarError(
                    "No se pudo abrir la ventana.\n"
                            + e.getMessage()
            );
        }
    }

    private void cerrarVentanaMenu(
            ActionEvent event) {

        if (event.getSource()
                instanceof Node nodo) {

            Stage ventanaMenu =
                    (Stage) nodo
                            .getScene()
                            .getWindow();

            ventanaMenu.close();
        }
    }

    private void mostrarError(
            String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alerta.setTitle(
                "Error al abrir la ventana"
        );

        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}