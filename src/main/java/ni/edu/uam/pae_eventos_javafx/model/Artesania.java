package ni.edu.uam.pae_eventos_javafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Artesania {
    private String nombreProducto;
    private String productoID;
    private String productoCategoria;
    private double precio;
    private int cantidadDisponible;
    private String rutaImagen;
}

