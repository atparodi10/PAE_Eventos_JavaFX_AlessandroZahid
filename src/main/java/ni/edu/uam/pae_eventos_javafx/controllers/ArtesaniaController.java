package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ni.edu.uam.pae_eventos_javafx.dao.ArtesaniaDao;
import ni.edu.uam.pae_eventos_javafx.model.Artesania;

import java.io.File;
import java.util.Optional;

public class ArtesaniaController {

    private final ArtesaniaDao artesaniaDao =
            new ArtesaniaDao();

    private String rutaImagenSeleccionada;

    @FXML
    private TextField txtProductoId;

    @FXML
    private TextField txtNombreProducto;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtCantidad;

    @FXML
    private ImageView imgVistaPrevia;

    @FXML
    private TableView<Artesania> tblArtesanias;

    @FXML
    private TableColumn<Artesania, ImageView> colImagen;

    @FXML
    private TableColumn<Artesania, String> colProductoId;

    @FXML
    private TableColumn<Artesania, String> colNombreProducto;

    @FXML
    private TableColumn<Artesania, String> colCategoria;

    @FXML
    private TableColumn<Artesania, Double> colPrecio;

    @FXML
    private TableColumn<Artesania, Integer> colCantidad;

    @FXML
    public void initialize() {

        configurarColumnas();

        actualizarTabla();

        txtProductoId.requestFocus();
    }

    private void configurarColumnas() {

        colProductoId.setCellValueFactory(
                dato ->
                        new ReadOnlyStringWrapper(
                                dato.getValue()
                                        .getProductoID()
                        )
        );

        colNombreProducto.setCellValueFactory(
                dato ->
                        new ReadOnlyStringWrapper(
                                dato.getValue()
                                        .getNombreProducto()
                        )
        );

        colCategoria.setCellValueFactory(
                dato ->
                        new ReadOnlyStringWrapper(
                                dato.getValue()
                                        .getProductoCategoria()
                        )
        );

        colPrecio.setCellValueFactory(
                dato ->
                        new ReadOnlyObjectWrapper<>(
                                dato.getValue()
                                        .getPrecio()
                        )
        );

        colCantidad.setCellValueFactory(
                dato ->
                        new ReadOnlyObjectWrapper<>(
                                dato.getValue()
                                        .getCantidadDisponible()
                        )
        );

        colImagen.setCellValueFactory(
                dato ->
                        new ReadOnlyObjectWrapper<>(
                                crearVistaImagen(
                                        dato.getValue()
                                                .getRutaImagen()
                                )
                        )
        );
    }

    @FXML
    private void nuevoOnAction(
            ActionEvent event) {

        limpiarFormulario();

        tblArtesanias
                .getSelectionModel()
                .clearSelection();
    }

    @FXML
    private void guardarOnAction(
            ActionEvent event) {

        if (!formularioValido()) {
            return;
        }

        try {

            double precio =
                    Double.parseDouble(
                            txtPrecio
                                    .getText()
                                    .trim()
                    );

            int cantidad =
                    Integer.parseInt(
                            txtCantidad
                                    .getText()
                                    .trim()
                    );

            if (precio <= 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio debe ser mayor que cero."
                );

                return;
            }

            if (cantidad < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Cantidad incorrecta",
                        "La cantidad no puede ser negativa."
                );

                return;
            }

            Artesania artesania =
                    new Artesania(
                            txtNombreProducto
                                    .getText()
                                    .trim(),
                            txtProductoId
                                    .getText()
                                    .trim(),
                            txtCategoria
                                    .getText()
                                    .trim(),
                            precio,
                            cantidad,
                            rutaImagenSeleccionada
                    );

            artesaniaDao.guardar(artesania);

            actualizarTabla();

            /*
             * El formulario y la imagen
             * se limpian después de guardar.
             */
            limpiarFormulario();

            tblArtesanias
                    .getSelectionModel()
                    .clearSelection();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Producto guardado",
                    "La artesanía fue agregada al catálogo."
            );

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incorrectos",
                    "El precio y la cantidad deben ser números válidos."
            );

        } catch (IllegalArgumentException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se pudo guardar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void buscarOnAction(
            ActionEvent event) {

        TextInputDialog dialogo =
                new TextInputDialog();

        dialogo.setTitle("Buscar artesanía");
        dialogo.setHeaderText(
                "Buscar por código o nombre"
        );
        dialogo.setContentText(
                "Ingrese el criterio:"
        );

        Optional<String> resultado =
                dialogo.showAndWait();

        if (resultado.isEmpty()
                || resultado.get().isBlank()) {

            return;
        }

        Artesania encontrada =
                artesaniaDao.buscar(
                        resultado.get()
                );

        if (encontrada == null) {

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Sin resultados",
                    "No se encontró ninguna artesanía."
            );

            return;
        }

        tblArtesanias
                .getSelectionModel()
                .select(encontrada);

        tblArtesanias.scrollTo(encontrada);
    }

    @FXML
    private void seleccionarImagenOnAction(
            ActionEvent event) {

        FileChooser selector =
                new FileChooser();

        selector.setTitle(
                "Seleccionar imagen de la artesanía"
        );

        selector
                .getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Imágenes",
                                "*.png",
                                "*.jpg",
                                "*.jpeg"
                        )
                );

        File archivo =
                selector.showOpenDialog(
                        imgVistaPrevia
                                .getScene()
                                .getWindow()
                );

        if (archivo == null) {
            return;
        }

        rutaImagenSeleccionada =
                archivo
                        .toURI()
                        .toString();

        imgVistaPrevia.setImage(
                new Image(
                        rutaImagenSeleccionada
                )
        );
    }

    @FXML
    private void registrarVentaOnAction(
            ActionEvent event) {

        Artesania seleccionada =
                tblArtesanias
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un producto",
                    "Debe seleccionar una artesanía de la tabla."
            );

            return;
        }

        if (seleccionada
                .getCantidadDisponible() == 0) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto agotado",
                    "La artesanía seleccionada no tiene existencias."
            );

            return;
        }

        TextInputDialog dialogo =
                new TextInputDialog("1");

        dialogo.setTitle("Registrar venta");

        dialogo.setHeaderText(
                seleccionada.getNombreProducto()
        );

        dialogo.setContentText(
                "Cantidad vendida:"
        );

        Optional<String> resultado =
                dialogo.showAndWait();

        if (resultado.isEmpty()
                || resultado.get().isBlank()) {

            return;
        }

        try {

            int cantidadVendida =
                    Integer.parseInt(
                            resultado
                                    .get()
                                    .trim()
                    );

            if (cantidadVendida <= 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Cantidad incorrecta",
                        "La cantidad debe ser mayor que cero."
                );

                return;
            }

            if (cantidadVendida
                    > seleccionada
                    .getCantidadDisponible()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Existencias insuficientes",
                        "Solamente hay "
                                + seleccionada
                                .getCantidadDisponible()
                                + " unidades disponibles."
                );

                return;
            }

            seleccionada.setCantidadDisponible(
                    seleccionada
                            .getCantidadDisponible()
                            - cantidadVendida
            );

            tblArtesanias.refresh();

            double total =
                    seleccionada.getPrecio()
                            * cantidadVendida;

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Venta registrada",
                    String.format(
                            "Cantidad vendida: %d%n"
                                    + "Total: C$ %.2f",
                            cantidadVendida,
                            total
                    )
            );

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cantidad incorrecta",
                    "Ingrese un número entero válido."
            );
        }
    }

    @FXML
    private void mostrarAyudaOnAction(
            ActionEvent event) {

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Ayuda",
                """
                Nuevo limpia el formulario.

                Guardar agrega una artesanía al catálogo.

                Buscar localiza un producto por código o nombre.

                Registrar venta descuenta unidades del inventario.

                Para agregar un producto debe seleccionar una imagen.
                """
        );
    }

    private void actualizarTabla() {

        tblArtesanias
                .getItems()
                .setAll(
                        artesaniaDao.obtenerTodos()
                );
    }

    private boolean formularioValido() {

        boolean camposVacios =
                txtProductoId
                        .getText()
                        .isBlank()
                        || txtNombreProducto
                        .getText()
                        .isBlank()
                        || txtCategoria
                        .getText()
                        .isBlank()
                        || txtPrecio
                        .getText()
                        .isBlank()
                        || txtCantidad
                        .getText()
                        .isBlank();

        if (camposVacios) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Debe completar todos los campos."
            );

            return false;
        }

        if (rutaImagenSeleccionada == null
                || rutaImagenSeleccionada.isBlank()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Imagen requerida",
                    "Debe seleccionar una imagen para la artesanía."
            );

            return false;
        }

        return true;
    }

    private void limpiarFormulario() {

        txtProductoId.clear();
        txtNombreProducto.clear();
        txtCategoria.clear();
        txtPrecio.clear();
        txtCantidad.clear();

        rutaImagenSeleccionada = null;

        imgVistaPrevia.setImage(null);

        txtProductoId.requestFocus();
    }

    private ImageView crearVistaImagen(
            String rutaImagen) {

        ImageView vista =
                new ImageView();

        vista.setFitWidth(65);
        vista.setFitHeight(50);
        vista.setPreserveRatio(true);

        if (rutaImagen != null
                && !rutaImagen.isBlank()) {

            vista.setImage(
                    new Image(
                            rutaImagen,
                            true
                    )
            );
        }

        return vista;
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}