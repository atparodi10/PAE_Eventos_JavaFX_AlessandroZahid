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

        colProductoId.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().getProductoID()
                )
        );

        colNombreProducto.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().getNombreProducto()
                )
        );

        colCategoria.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(
                        dato.getValue().getProductoCategoria()
                )
        );

        colPrecio.setCellValueFactory(dato ->
                new ReadOnlyObjectWrapper<>(
                        dato.getValue().getPrecio()
                )
        );

        colCantidad.setCellValueFactory(dato ->
                new ReadOnlyObjectWrapper<>(
                        dato.getValue().getCantidadDisponible()
                )
        );

        colImagen.setCellValueFactory(dato ->
                new ReadOnlyObjectWrapper<>(
                        crearVistaImagen(
                                dato.getValue().getRutaImagen()
                        )
                )
        );

        /*
         * Cuando se abre la ventana desde el menú,
         * carga en la tabla los productos del DAO.
         */
        actualizarTabla();
    }

    @FXML
    private void nuevoOnAction(ActionEvent event) {

        limpiarFormulario();

        tblArtesanias
                .getSelectionModel()
                .clearSelection();
    }

    @FXML
    private void guardarOnAction(ActionEvent event) {

        if (!formularioValido()) {
            return;
        }

        try {

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );

            int cantidad = Integer.parseInt(
                    txtCantidad.getText().trim()
            );

            if (precio <= 0 || cantidad < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Valores incorrectos",
                        "El precio debe ser mayor que cero "
                                + "y la cantidad no puede ser negativa."
                );

                return;
            }

            Artesania artesania = new Artesania(
                    txtNombreProducto.getText().trim(),
                    txtProductoId.getText().trim(),
                    txtCategoria.getText().trim(),
                    precio,
                    cantidad,
                    rutaImagenSeleccionada
            );

            /*
             * guardar() viene de IDAO<Artesania>.
             */
            artesaniaDao.guardar(artesania);

            /*
             * La tabla vuelve a consultar los registros del DAO.
             */
            actualizarTabla();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Producto guardado",
                    "La artesanía fue agregada al catálogo."
            );

            limpiarFormulario();

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incorrectos",
                    "Ingrese un precio y una cantidad válidos."
            );

        } catch (IllegalArgumentException e) {

            /*
             * Aquí se muestra, por ejemplo, el error
             * producido por un código repetido.
             */
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se pudo guardar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void buscarOnAction(ActionEvent event) {

        TextInputDialog dialogo =
                new TextInputDialog();

        dialogo.setTitle("Buscar artesanía");
        dialogo.setHeaderText(
                "Buscar por código o nombre"
        );
        dialogo.setContentText("Criterio:");

        Optional<String> resultado =
                dialogo.showAndWait();

        if (resultado.isEmpty()
                || resultado.get().isBlank()) {

            return;
        }

        Artesania encontrada =
                artesaniaDao.buscar(resultado.get());

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

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Imágenes",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File archivo = selector.showOpenDialog(
                imgVistaPrevia
                        .getScene()
                        .getWindow()
        );

        if (archivo != null) {

            rutaImagenSeleccionada =
                    archivo.toURI().toString();

            imgVistaPrevia.setImage(
                    new Image(rutaImagenSeleccionada)
            );
        }
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
                    "Seleccione una artesanía de la tabla."
            );

            return;
        }

        TextInputDialog dialogo =
                new TextInputDialog("1");

        dialogo.setTitle("Registrar venta");
        dialogo.setHeaderText(
                seleccionada.getNombreProducto()
        );
        dialogo.setContentText("Cantidad vendida:");

        Optional<String> resultado =
                dialogo.showAndWait();

        if (resultado.isEmpty()) {
            return;
        }

        try {

            int cantidadVendida =
                    Integer.parseInt(
                            resultado.get().trim()
                    );

            if (cantidadVendida <= 0
                    || cantidadVendida
                    > seleccionada.getCantidadDisponible()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Cantidad no disponible",
                        "Ingrese una cantidad entre 1 y "
                                + seleccionada
                                .getCantidadDisponible()
                );

                return;
            }

            seleccionada.setCantidadDisponible(
                    seleccionada.getCantidadDisponible()
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
                            "Total de la venta: C$ %.2f",
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
                Guardar agrega una artesanía.
                Buscar localiza por código o nombre.
                Ventas descuenta productos del inventario.
                """
        );
    }

    private void actualizarTabla() {

        /*
         * obtenerTodos() viene de IDAO<Artesania>.
         */
        tblArtesanias
                .getItems()
                .setAll(
                        artesaniaDao.obtenerTodos()
                );
    }

    private boolean formularioValido() {

        boolean hayCamposVacios =
                txtProductoId.getText().isBlank()
                        || txtNombreProducto.getText().isBlank()
                        || txtCategoria.getText().isBlank()
                        || txtPrecio.getText().isBlank()
                        || txtCantidad.getText().isBlank()
                        || rutaImagenSeleccionada == null;

        if (hayCamposVacios) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Complete todos los campos "
                            + "y seleccione una imagen."
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

        ImageView vista = new ImageView();

        vista.setFitWidth(65);
        vista.setFitHeight(50);
        vista.setPreserveRatio(true);

        if (rutaImagen != null
                && !rutaImagen.isBlank()) {

            vista.setImage(
                    new Image(rutaImagen, true)
            );
        }

        return vista;
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}