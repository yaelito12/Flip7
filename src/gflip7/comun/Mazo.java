package flip7.comun;

import java.io.Serializable;
import java.util.*;

public class Mazo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Carta> cartas = new ArrayList<>();
    private List<Carta> pilaDescarte = new ArrayList<>();
    
    public Mazo() { 
        inicializarMazo(); 
    }
}