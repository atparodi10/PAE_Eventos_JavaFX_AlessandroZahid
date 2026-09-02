package ni.edu.uam.pae_eventos_javafx.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoteCafe {
    private String codigoLote;
    private String nombreProducto;
    private String codigoProducto;
    private String proveedor;
    private LocalDate fechaProduccion, fechaVencimiento;
    private int cantidad;

}
