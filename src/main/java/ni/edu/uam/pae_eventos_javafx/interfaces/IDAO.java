package ni.edu.uam.pae_eventos_javafx.interfaces;

import java.util.List;

public interface IDAO<T> {
    void guardar(T entidad);
    void eliminar(T entidad);
    List<T> obtenerTodos();
}
