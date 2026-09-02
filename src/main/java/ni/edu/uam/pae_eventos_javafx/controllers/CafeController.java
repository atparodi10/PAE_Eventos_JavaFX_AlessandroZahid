package ni.edu.uam.pae_eventos_javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import ni.edu.uam.pae_eventos_javafx.dao.LoteCafeDao;
import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.LoteCafe;

import java.time.LocalDate;

public class CafeController {

    @FXML
    private TextField txtCodigoLote;

    @FXML
    private TextField txtNombreProducto;

    @FXML
    private TextField txtCodigoProducto;

    @FXML
    private TextField txtProveedor;

    @FXML
    private DatePicker dpFechaProduccion;

    @FXML
    private DatePicker dpFechaVencimiento;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TableView<LoteCafe> tablaLotes;

    private final IDAO<LoteCafe> loteCafeDao =
            new LoteCafeDao();

    private ObservableList<LoteCafe> listaObservable;

    @FXML
    public void initialize() {

        aplicarRestriccionesDeEntrada();
        configurarTablaYMenu();

        txtCodigoLote.requestFocus();
    }

    @FXML
    public void onGuardarAction(
            ActionEvent event) {

        procesarGuardadoLote();
    }

    @FXML
    public void onTablaMouseClicked(
            MouseEvent event) {

        mostrarDetallesSeleccionados();
    }

    private void aplicarRestriccionesDeEntrada() {

        txtCodigoLote.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText()
                                .matches("\\d{0,8}")
                                ? c
                                : null
                )
        );

        txtCodigoProducto.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText()
                                .matches("\\d{0,8}")
                                ? c
                                : null
                )
        );

        txtNombreProducto.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText().matches(
                                "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"
                        )
                                ? c
                                : null
                )
        );

        txtProveedor.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText().matches(
                                "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*"
                        )
                                ? c
                                : null
                )
        );

        txtCantidad.setTextFormatter(
                new TextFormatter<>(c ->
                        c.getControlNewText()
                                .matches("\\d*")
                                ? c
                                : null
                )
        );
    }

    private void configurarTablaYMenu() {

        listaObservable =
                FXCollections.observableArrayList(
                        loteCafeDao.obtenerTodos()
                );

        tablaLotes.setItems(listaObservable);

        ContextMenu contextMenu =
                new ContextMenu();

        MenuItem itemEliminar =
                new MenuItem("Eliminar lote");

        itemEliminar.setOnAction(event ->
                procesarEliminacionLote()
        );

        contextMenu
                .getItems()
                .add(itemEliminar);

        tablaLotes.setContextMenu(contextMenu);
    }

    private void procesarGuardadoLote() {

        if (!validarCamposTexto()
                || !validarFechas()) {

            return;
        }

        try {

            int cantidad =
                    Integer.parseInt(
                            txtCantidad.getText().trim()
                    );

            if (cantidad <= 0) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Cantidad incorrecta",
                        "La cantidad debe ser mayor que cero."
                );

                return;
            }

            String codigoLote =
                    txtCodigoLote
                            .getText()
                            .trim();

            if (buscarLotePorCodigo(
                    codigoLote
            ) != null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código repetido",
                        "Ya existe un lote con el código "
                                + codigoLote + "."
                );

                return;
            }

            LoteCafe nuevoLote =
                    new LoteCafe(
                            codigoLote,
                            txtNombreProducto
                                    .getText()
                                    .trim(),
                            txtCodigoProducto
                                    .getText()
                                    .trim(),
                            txtProveedor
                                    .getText()
                                    .trim(),
                            dpFechaProduccion
                                    .getValue(),
                            dpFechaVencimiento
                                    .getValue(),
                            cantidad
                    );

            loteCafeDao.guardar(nuevoLote);

            actualizarTabla();

            /*
             * Se limpian los campos solamente
             * después de guardar correctamente.
             */
            limpiarFormulario();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Lote guardado",
                    "El lote fue registrado correctamente."
            );

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Cantidad incorrecta",
                    "Ingrese una cantidad numérica válida."
            );
        }
    }

    private void procesarEliminacionLote() {

        LoteCafe loteSeleccionado =
                tablaLotes
                        .getSelectionModel()
                        .getSelectedItem();

        if (loteSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un lote",
                    "Debe seleccionar el lote que desea eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle("Eliminar lote");
        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Desea eliminar el lote "
                        + loteSeleccionado
                        .getCodigoLote()
                        + "?"
        );

        confirmacion.showAndWait()
                .ifPresent(respuesta -> {

                    if (respuesta == ButtonType.OK) {

                        loteCafeDao.eliminar(
                                loteSeleccionado
                        );

                        actualizarTabla();
                        limpiarFormulario();

                        mostrarAlerta(
                                Alert.AlertType.INFORMATION,
                                "Lote eliminado",
                                "El lote fue eliminado correctamente."
                        );
                    }
                });
    }

    private void mostrarDetallesSeleccionados() {

        LoteCafe loteSeleccionado =
                tablaLotes
                        .getSelectionModel()
                        .getSelectedItem();

        if (loteSeleccionado == null) {
            return;
        }

        txtCodigoLote.setText(
                loteSeleccionado.getCodigoLote()
        );

        txtNombreProducto.setText(
                loteSeleccionado.getNombreProducto()
        );

        txtCodigoProducto.setText(
                loteSeleccionado.getCodigoProducto()
        );

        txtProveedor.setText(
                loteSeleccionado.getProveedor()
        );

        dpFechaProduccion.setValue(
                loteSeleccionado.getFechaProduccion()
        );

        dpFechaVencimiento.setValue(
                loteSeleccionado.getFechaVencimiento()
        );

        txtCantidad.setText(
                String.valueOf(
                        loteSeleccionado.getCantidad()
                )
        );
    }

    private LoteCafe buscarLotePorCodigo(
            String codigo) {

        return loteCafeDao
                .obtenerTodos()
                .stream()
                .filter(lote ->
                        lote
                                .getCodigoLote()
                                .equals(codigo)
                )
                .findFirst()
                .orElse(null);
    }

    private boolean validarCamposTexto() {

        boolean camposVacios =
                txtCodigoLote.getText().isBlank()
                        || txtNombreProducto
                        .getText()
                        .isBlank()
                        || txtCodigoProducto
                        .getText()
                        .isBlank()
                        || txtProveedor
                        .getText()
                        .isBlank()
                        || txtCantidad
                        .getText()
                        .isBlank();

        if (camposVacios) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Campos incompletos",
                    "Debe completar todos los campos."
            );

            return false;
        }

        if (txtCodigoLote.getText().length() != 8) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Código incorrecto", "El código del lote debe tener exactamente 8 dígitos."
            );

            return false;
        }

        if (txtCodigoProducto.getText().length() != 8) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Código incorrecto", "El código del producto debe tener exactamente 8 dígitos."
            );

            return false;
        }

        return true;
    }

    private boolean validarFechas() {

        LocalDate produccion = dpFechaProduccion.getValue();

        LocalDate vencimiento = dpFechaVencimiento.getValue();

        LocalDate hoy = LocalDate.now();

        if (produccion == null || vencimiento == null) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Fechas incompletas", "Debe seleccionar las dos fechas.");

            return false;
        }

        if (produccion.isAfter(hoy)) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Fecha incorrecta", "La fecha de producción no puede ser futura."
            );

            return false;
        }

        if (vencimiento.isBefore(hoy)) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Fecha incorrecta", "La fecha de vencimiento no puede ser anterior a hoy."
            );

            return false;
        }

        if (vencimiento.isBefore(produccion)) {

            mostrarAlerta(
                    Alert.AlertType.ERROR, "Fechas incorrectas", "El vencimiento no puede ser anterior a la producción."
            );

            return false;
        }

        return true;
    }

    private void actualizarTabla() {

        listaObservable.setAll(loteCafeDao.obtenerTodos()
        );
    }

    private void limpiarFormulario() {

        txtCodigoLote.clear();
        txtNombreProducto.clear();
        txtCodigoProducto.clear();
        txtProveedor.clear();
        txtCantidad.clear();

        dpFechaProduccion.setValue(null);
        dpFechaVencimiento.setValue(null);

        tablaLotes.getSelectionModel().clearSelection();

        txtCodigoLote.requestFocus();
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