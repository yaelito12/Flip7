package flip7.comun;

import java.io.Serializable;
import java.util.List;

public class MensajeJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private TipoMensaje tipo;

    public MensajeJuego(TipoMensaje tipo) {
        this.tipo = tipo;
    }
}