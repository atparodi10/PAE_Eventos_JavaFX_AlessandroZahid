package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ni.edu.uam.pae_eventos_javafx.dao.LoteCafeDao;
import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.LoteCafe;

import java.time.LocalDate;
import java.util.Optional;

public class CafeController {

    @FXML private TextField txtCodigoLote;
    @FXML private TextField txtNombreProducto;
    @FXML private TextField txtCodigoProducto;
    @FXML private TextField txtProveedor;
    @FXML private DatePicker dpFechaProduccion;
    @FXML private DatePicker dpFechaVencimiento;
    @FXML private TextField txtCantidad;
    @FXML private TableView<LoteCafe> tablaLotes;

    private IDAO<LoteCafe> loteCafeDao = new LoteCafeDao();
    private ObservableList<LoteCafe> listaObservable;

    @FXML
    public void initialize() {
        aplicarRestriccionesDeEntrada();
        configurarTablaYMenu();
    }

    // Acción del botón Guardar
    @FXML
    public void onGuardarAction(ActionEvent event) {
        procesarGuardadoLote();
    }

    // Evento del MouseEvent para mostrar detalles solicitados en el Reto 2
    @FXML
    public void onTablaMouseClicked(MouseEvent event) {
        mostrarDetallesSeleccionados();
    }

    private void aplicarRestriccionesDeEntrada() {
        // Códigos: 8 dígitos estrictamente numéricos
        txtCodigoLote.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,8}") ? c : null));
        txtCodigoProducto.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,8}") ? c : null));

        // Nombres y proveedor: Solo letras
        txtNombreProducto.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*") ? c : null));
        txtProveedor.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*") ? c : null));

        // Cantidad: Solo números
        txtCantidad.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().matches("\\d*") ? c : null));
    }

    private void configurarTablaYMenu() {
        listaObservable = FXCollections.observableArrayList(loteCafeDao.obtenerTodos());
        tablaLotes.setItems(listaObservable);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemEliminar = new MenuItem("Eliminar Lote");
        itemEliminar.setOnAction(e -> procesarEliminacionLote());
        contextMenu.getItems().add(itemEliminar);
        tablaLotes.setContextMenu(contextMenu);
    }

    private void procesarGuardadoLote() {
        if (!validarCamposTexto() || !validarFechas()) return;

        LoteCafe nuevoLote = new LoteCafe(
                txtCodigoLote.getText(),
                txtNombreProducto.getText(),
                txtCodigoProducto.getText(),
                txtProveedor.getText(),
                dpFechaProduccion.getValue(),
                dpFechaVencimiento.getValue(),
                Integer.parseInt(txtCantidad.getText())
        );

        loteCafeDao.guardar(nuevoLote);
        listaObservable.setAll(loteCafeDao.obtenerTodos());
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Lote guardado.");
        limpiarFormulario();
    }

    private boolean validarCamposTexto() {
        if (txtCodigoLote.getText().length() != 8 || txtCodigoProducto.getText().length() != 8) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Los códigos deben tener 8 dígitos.");
            return false;
        }
        if (txtNombreProducto.getText().isEmpty() || txtProveedor.getText().isEmpty() || txtCantidad.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Complete todos los campos de texto.");
            return false;
        }
        return true;
    }

    private boolean validarFechas() {
        LocalDate produccion = dpFechaProduccion.getValue();
        LocalDate vencimiento = dpFechaVencimiento.getValue();
        LocalDate hoy = LocalDate.now();

        if (produccion == null || vencimiento == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe seleccionar ambas fechas.");
            return false;
        }
        if (produccion.isAfter(hoy)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La fecha de producción no puede ser futura.");
            return false;
        }
        if (vencimiento.isBefore(hoy)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La fecha de vencimiento no puede ser anterior a hoy.");
            return false;
        }
        return true;
    }

    private void procesarEliminacionLote() {
        LoteCafe lote = tablaLotes.getSelectionModel().getSelectedItem();
        if (lote == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar lote " + lote.getCodigoLote() + "?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                loteCafeDao.eliminar(lote);
                listaObservable.setAll(loteCafeDao.obtenerTodos());
            }
        });
    }

    private void mostrarDetallesSeleccionados() {
        LoteCafe lote = tablaLotes.getSelectionModel().getSelectedItem();
        if (lote != null) {
            // Rellena los campos con el elemento clickeado
            txtCodigoLote.setText(lote.getCodigoLote());
            txtNombreProducto.setText(lote.getNombreProducto());
            txtProveedor.setText(lote.getProveedor());
            txtCantidad.setText(String.valueOf(lote.getCantidad()));
        }
    }

    private void limpiarFormulario() {
        txtCodigoLote.clear();
        txtNombreProducto.clear();
        txtCodigoProducto.clear();
        txtProveedor.clear();
        txtCantidad.clear();
        dpFechaProduccion.setValue(null);
        dpFechaVencimiento.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}