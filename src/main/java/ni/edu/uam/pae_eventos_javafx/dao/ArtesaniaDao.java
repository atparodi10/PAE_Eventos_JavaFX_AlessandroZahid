package ni.edu.uam.pae_eventos_javafx.dao;

import ni.edu.uam.pae_eventos_javafx.model.Artesania;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtesaniaDao {

    private final List<Artesania> artesanias = new ArrayList<>();

    public boolean agregar(Artesania artesania) {

        if (buscarPorId(artesania.getProductoID()) != null) {
            return false;
        }

        artesanias.add(artesania);
        return true;
    }

    public Artesania buscarPorId(String productoId) {

        for (Artesania artesania : artesanias) {

            if (artesania.getProductoID()
                    .equalsIgnoreCase(productoId)) {

                return artesania;
            }
        }

        return null;
    }

    public Artesania buscar(String criterio) {

        String textoBuscado = criterio
                .trim()
                .toLowerCase(Locale.ROOT);

        for (Artesania artesania : artesanias) {

            boolean coincideId = artesania
                    .getProductoID()
                    .toLowerCase(Locale.ROOT)
                    .contains(textoBuscado);

            boolean coincideNombre = artesania
                    .getNombreProducto()
                    .toLowerCase(Locale.ROOT)
                    .contains(textoBuscado);

            if (coincideId || coincideNombre) {
                return artesania;
            }
        }

        return null;
    }

    public List<Artesania> obtenerTodos() {
        return new ArrayList<>(artesanias);
    }
}