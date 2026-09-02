package ni.edu.uam.pae_eventos_javafx.dao;

import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoDao implements IDAO<Producto> {

    List<Producto> productos;

    public ProductoDao() {productos = new ArrayList<>();}

    @Override
    public void guardar(Producto entidad) {
        productos.add(entidad);

    }

    @Override
    public void eliminar(Producto entidad) {
        productos.removeIf(p -> p.getCodigo().equals(entidad.getCodigo()));
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productos;
    }
}
