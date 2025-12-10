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

   
    private void inicializarMazo() {
        cartas.clear();
        
        // Carta 0 (única)
        cartas.add(new Carta(Carta.TipoCarta.NUMERO, 0));
        
        // Cartas 1-12 (cantidad = número)
        for (int numero = 1; numero <= 12; numero++) {
            for (int cantidad = 0; cantidad < numero; cantidad++) {
                cartas.add(new Carta(Carta.TipoCarta.NUMERO, numero));
            }
        }
        
        // Cartas modificadoras
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 2));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 4));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 6));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 8));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 8));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 10));
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, -1));
        
        // Cartas de acción (3 de cada una)
        for (int i = 0; i < 3; i++) {
            cartas.add(new Carta(Carta.TipoCarta.CONGELAR, 0));
            cartas.add(new Carta(Carta.TipoCarta.VOLTEAR_TRES, 0));
            cartas.add(new Carta(Carta.TipoCarta.SEGUNDA_OPORTUNIDAD, 0));
        }
        
        System.out.println("[MAZO] Inicializado con " + cartas.size() + " cartas");
        barajar();
    }
    
    public void barajar() { 
        Collections.shuffle(cartas);
        System.out.println("[MAZO] Barajado");
    }
}