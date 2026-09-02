package ni.edu.uam.pae_eventos_javafx.dao;

import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.Artesania;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtesaniaDao implements IDAO<Artesania> {

    /*
     * Es static para conservar los productos mientras la aplicación
     * permanezca abierta, aunque cierres y abras nuevamente la ventana.
     */

    private static final List<Artesania> artesanias = new ArrayList<>();

    @Override
    public void guardar(Artesania artesania) {

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "La artesanía no puede ser nula."
            );
        }

        if (buscarPorId(artesania.getProductoID()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una artesanía con ese código."
            );
        }

        artesanias.add(artesania);
    }

    @Override
    public void eliminar(Artesania artesania) {
        artesanias.remove(artesania);
    }

    @Override
    public List<Artesania> obtenerTodos() {

        /*
         * Devuelve una copia para evitar que otra clase
         * modifique directamente la lista del DAO.
         */

        return new ArrayList<>(artesanias);
    }

    public Artesania buscarPorId(String productoId) {

        if (productoId == null || productoId.isBlank()) {
            return null;
        }

        for (Artesania artesania : artesanias) {

            if (artesania.getProductoID()
                    .equalsIgnoreCase(productoId.trim())) {

                return artesania;
            }
        }

        return null;
    }

    public Artesania buscar(String criterio) {

        if (criterio == null || criterio.isBlank()) {
            return null;
        }

        String textoBuscado = criterio
                .trim()
                .toLowerCase(Locale.ROOT);

        for (Artesania artesania : artesanias) {

            String id = artesania
                    .getProductoID()
                    .toLowerCase(Locale.ROOT);

            String nombre = artesania
                    .getNombreProducto()
                    .toLowerCase(Locale.ROOT);

            if (id.contains(textoBuscado)
                    || nombre.contains(textoBuscado)) {

                return artesania;
            }
        }

        return null;
    }
}