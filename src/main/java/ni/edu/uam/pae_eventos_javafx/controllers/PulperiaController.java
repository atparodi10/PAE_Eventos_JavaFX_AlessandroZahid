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

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;

    private IDAO<Producto> productoDao = new ProductoDao();

    @FXML
    public void initialize() {
        aplicarRestriccionesDeEntrada();
    }

    // El evento del botón solo llama al método orquestador
    @FXML
    public void onGuardarAction(ActionEvent event) {
        procesarGuardado();
    }

    // El evento del teclado solo llama al método orquestador
    @FXML
    public void onBuscarKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ejecutarBusqueda();
        }
    }

    private void aplicarRestriccionesDeEntrada() {
        // Código: Solo números, máximo 8 dígitos
        txtCodigo.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0,8}") ? change : null));

        // Nombre: Solo letras y espacios
        txtNombre.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*") ? change : null));

        // Precio: Solo números y un punto decimal
        txtPrecio.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*(\\.\\d*)?") ? change : null));

        // Cantidad: Solo números enteros
        txtCantidad.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null));
    }

    private void procesarGuardado() {
        if (!validarCamposCompletos() || !validarLongitudCodigo(txtCodigo.getText())) return;

        Producto nuevoProducto = new Producto(
                txtCodigo.getText(),
                txtNombre.getText(),
                Double.parseDouble(txtPrecio.getText()),
                Integer.parseInt(txtCantidad.getText())
        );

        productoDao.guardar(nuevoProducto);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto registrado.");
        limpiarCampos();
    }

    private void ejecutarBusqueda() {
        String codigoBuscado = txtCodigo.getText();
        if (codigoBuscado.isEmpty()) return;

        Producto encontrado = productoDao.obtenerTodos().stream()
                .filter(p -> p.getCodigo().equals(codigoBuscado))
                .findFirst()
                .orElse(null);

        if (encontrado != null) {
            txtNombre.setText(encontrado.getNombre());
            txtPrecio.setText(String.valueOf(encontrado.getPrecio()));
            txtCantidad.setText(String.valueOf(encontrado.getCantidad()));
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Producto no encontrado.");
        }
    }

    private boolean validarCamposCompletos() {
        if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                txtPrecio.getText().isEmpty() || txtCantidad.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios.");
            return false;
        }
        return true;
    }

    private boolean validarLongitudCodigo(String codigo) {
        if (codigo.length() != 8) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El código debe tener exactamente 8 dígitos.");
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtCantidad.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}