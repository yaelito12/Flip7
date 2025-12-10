package flip7.comun;

import java.io.Serializable;
import java.util.*;

public class SalaJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String idSala;
    private String nombreSala;
    private String nombreHost;
    private int idHost;
    private int maxJugadores;
    private boolean juegoIniciado;
    private long tiempoCreacion;
    
    public SalaJuego(String idSala, String nombreSala, String nombreHost, 
                     int idHost, int maxJugadores) {
        this.idSala = idSala;
        this.nombreSala = nombreSala;
        this.nombreHost = nombreHost;
        this.idHost = idHost;
        this.maxJugadores = maxJugadores;
        this.tiempoCreacion = System.currentTimeMillis();
    }
}
