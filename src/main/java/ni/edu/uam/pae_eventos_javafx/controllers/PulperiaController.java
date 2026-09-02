package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ni.edu.uam.pae_eventos_javafx.dao.ProductoDao;
import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.Producto;

public class PulperiaController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtCantidad;

    private final IDAO<Producto> productoDao =
            new ProductoDao();

    @FXML
    public void initialize() {

        aplicarRestriccionesDeEntrada();

        txtCodigo.requestFocus();
    }

    @FXML
    public void onGuardarAction(
            ActionEvent event) {

        procesarGuardado();
    }

    @FXML
    public void onBuscarKey(
            KeyEvent event) {

        if (event.getCode() == KeyCode.ENTER) {
            ejecutarBusqueda();
        }
    }

    private void aplicarRestriccionesDeEntrada() {

        /*
         * Código:
         * solamente números y máximo 8 dígitos.
         */
        txtCodigo.setTextFormatter(
                new TextFormatter<>(change -> {

                    String nuevoTexto =
                            change.getControlNewText();

                    if (nuevoTexto.matches("\\d{0,8}")) {
                        return change;
                    }

                    return null;
                })
        );

        /*
         * Nombre:
         * solamente letras y espacios.
         */
        txtNombre.setTextFormatter(
                new TextFormatter<>(change -> {

                    String nuevoTexto =
                            change.getControlNewText();

                    if (nuevoTexto.matches(
                            "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"
                    )) {
                        return change;
                    }

                    return null;
                })
        );

        /*
         * Precio:
         * números y solamente un punto decimal.
         */
        txtPrecio.setTextFormatter(
                new TextFormatter<>(change -> {

                    String nuevoTexto =
                            change.getControlNewText();

                    if (nuevoTexto.matches(
                            "\\d*(\\.\\d*)?"
                    )) {
                        return change;
                    }

                    return null;
                })
        );

        /*
         * Cantidad:
         * solamente números enteros.
         */
        txtCantidad.setTextFormatter(
                new TextFormatter<>(change -> {

                    String nuevoTexto =
                            change.getControlNewText();

                    if (nuevoTexto.matches("\\d*")) {
                        return change;
                    }

                    return null;
                })
        );
    }

    private void procesarGuardado() {

        if (!validarCamposCompletos()) {
            return;
        }

        String codigo =
                txtCodigo.getText().trim();

        if (!validarLongitudCodigo(codigo)) {
            return;
        }

        try {

            double precio =
                    Double.parseDouble(
                            txtPrecio.getText().trim()
                    );

            int cantidad =
                    Integer.parseInt(
                            txtCantidad.getText().trim()
                    );

            if (precio <= 0) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Precio incorrecto",
                        "El precio debe ser mayor que cero."
                );

                return;
            }

            if (cantidad < 0) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Cantidad incorrecta",
                        "La cantidad no puede ser negativa."
                );

                return;
            }

            if (buscarProductoPorCodigo(codigo) != null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código repetido",
                        "Ya existe un producto con el código "
                                + codigo + "."
                );

                return;
            }

            Producto nuevoProducto =
                    new Producto(
                            codigo,
                            txtNombre.getText().trim(),
                            precio,
                            cantidad
                    );

            productoDao.guardar(nuevoProducto);

            /*
             * Los campos se limpian solamente
             * si el producto fue guardado.
             */
            limpiarCampos();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Producto registrado",
                    "El producto fue guardado correctamente."
            );

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Datos incorrectos",
                    "El precio y la cantidad deben ser números válidos."
            );
        }
    }

    private void ejecutarBusqueda() {

        String codigoBuscado =
                txtCodigo.getText().trim();

        if (codigoBuscado.isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código requerido",
                    "Ingrese el código del producto que desea buscar."
            );

            return;
        }

        if (!validarLongitudCodigo(codigoBuscado)) {
            return;
        }

        Producto productoEncontrado =
                buscarProductoPorCodigo(
                        codigoBuscado
                );

        if (productoEncontrado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no encontrado",
                    "No existe ningún producto con el código "
                            + codigoBuscado + "."
            );

            return;
        }

        txtNombre.setText(
                productoEncontrado.getNombre()
        );

        txtPrecio.setText(
                String.valueOf(
                        productoEncontrado.getPrecio()
                )
        );

        txtCantidad.setText(
                String.valueOf(
                        productoEncontrado.getCantidad()
                )
        );

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Producto encontrado",
                "Los datos del producto fueron cargados."
        );
    }

    private Producto buscarProductoPorCodigo(
            String codigoBuscado) {

        return productoDao
                .obtenerTodos()
                .stream()
                .filter(producto ->
                        producto
                                .getCodigo()
                                .equals(codigoBuscado)
                )
                .findFirst()
                .orElse(null);
    }

    private boolean validarCamposCompletos() {

        boolean camposVacios =
                txtCodigo.getText().isBlank()
                        || txtNombre.getText().isBlank()
                        || txtPrecio.getText().isBlank()
                        || txtCantidad.getText().isBlank();

        if (camposVacios) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Campos incompletos",
                    "Todos los campos son obligatorios."
            );

            return false;
        }

        return true;
    }

    private boolean validarLongitudCodigo(
            String codigo) {

        if (codigo.length() != 8) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Código incorrecto",
                    "El código debe tener exactamente 8 dígitos."
            );

            return false;
        }

        return true;
    }

    private void limpiarCampos() {

        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtCantidad.clear();

        txtCodigo.requestFocus();
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