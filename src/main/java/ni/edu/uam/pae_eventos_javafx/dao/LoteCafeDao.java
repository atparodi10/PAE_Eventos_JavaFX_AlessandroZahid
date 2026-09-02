package ni.edu.uam.pae_eventos_javafx.dao;

import ni.edu.uam.pae_eventos_javafx.interfaces.IDAO;
import ni.edu.uam.pae_eventos_javafx.model.LoteCafe;

import java.util.ArrayList;
import java.util.List;

public class LoteCafeDao implements IDAO<LoteCafe> {

    List<LoteCafe> lotesCafes;

    public LoteCafeDao() {lotesCafes = new ArrayList<LoteCafe>();}


    @Override
    public void guardar(LoteCafe entidad) {
        lotesCafes.add(entidad);
    }

    @Override
    public void eliminar(LoteCafe entidad) {
        lotesCafes.removeIf(lcafe -> lcafe.getCodigoLote().equals(entidad.getCodigoLote()));
    }

    @Override
    public List<LoteCafe> obtenerTodos() {
        return lotesCafes;
    }
}
