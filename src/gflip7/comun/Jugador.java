package flip7.comun;

import java.io.Serializable;
import java.util.*;

public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private int id;
    private List<Carta> cartasNumero = new ArrayList<>();
    private List<Carta> cartasModificador = new ArrayList<>();
    private List<Carta> cartasAccion = new ArrayList<>();
    private Carta cartaSegundaOportunidad;
    private int puntajeTotal;
    private int puntajeRonda;
    private boolean eliminado;
    private boolean plantado;
    private boolean congelado;
    private boolean conectado = true;
    
    public Jugador(String nombre, int id) { 
        this.nombre = nombre; 
        this.id = id; 
    }
    
    public String getNombre() { return nombre; }
    public int getId() { return id; }
    public int getPuntajeTotal() { return puntajeTotal; }
    public int getPuntajeRonda() { return puntajeRonda; }
}