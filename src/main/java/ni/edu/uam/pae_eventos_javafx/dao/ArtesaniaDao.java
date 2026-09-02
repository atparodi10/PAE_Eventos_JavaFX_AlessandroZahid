package ni.edu.uam.pae_eventos_javafx.dao;

import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.Artesania;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtesaniaDao implements IDAO<Artesania> {

    private static final String RUTA_IMAGENES =
            "/ni/edu/uam/pae_eventos_javafx/images/";

    private static final List<Artesania> artesanias =
            new ArrayList<>();

    /*
     * Este bloque se ejecuta una sola vez cuando Java
     * utiliza ArtesaniaDao por primera vez.
     */
    static {
        cargarProductosIniciales();
    }

    private static void cargarProductosIniciales() {

        Artesania hamaca = new Artesania(
                "Hamaca nicaragüense",
                "ART-001",
                "Textil",
                1200.00,
                5,
                obtenerRutaImagen("Hamacas.png")
        );

        Artesania jarron = new Artesania(
                "Jarrón de barro",
                "ART-002",
                "Cerámica",
                650.00,
                8,
                obtenerRutaImagen(
                        "Jarrones de barro.png"
                )
        );

        Artesania mascara = new Artesania(
                "Máscara del Güegüense",
                "ART-003",
                "Decoración",
                900.00,
                6,
                obtenerRutaImagen(
                        "Mascaras del Gueguense.png"
                )
        );

        artesanias.add(hamaca);
        artesanias.add(jarron);
        artesanias.add(mascara);
    }

    private static String obtenerRutaImagen(
            String nombreImagen) {

        URL recurso = ArtesaniaDao.class
                .getResource(
                        RUTA_IMAGENES + nombreImagen
                );

        if (recurso == null) {

            System.err.println(
                    "No se encontró la imagen: "
                            + RUTA_IMAGENES
                            + nombreImagen
            );

            return null;
        }

        return recurso.toExternalForm();
    }

    @Override
    public void guardar(Artesania artesania) {

        if (artesania == null) {
            throw new IllegalArgumentException(
                    "La artesanía no puede ser nula."
            );
        }

        if (buscarPorId(
                artesania.getProductoID()
        ) != null) {

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
        return new ArrayList<>(artesanias);
    }

    public Artesania buscarPorId(
            String productoId) {

        if (productoId == null
                || productoId.isBlank()) {

            return null;
        }

        for (Artesania artesania : artesanias) {

            if (artesania
                    .getProductoID()
                    .equalsIgnoreCase(
                            productoId.trim()
                    )) {

                return artesania;
            }
        }

        return null;
    }

    public Artesania buscar(String criterio) {

        if (criterio == null
                || criterio.isBlank()) {

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