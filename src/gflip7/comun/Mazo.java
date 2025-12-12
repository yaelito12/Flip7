
package gflip7.comun;

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
        
        cartas.add(new Carta(Carta.TipoCarta.NUMERO, 0));
           
        for (int numero = 1; numero <= 12; numero++) {
            for (int cantidad = 0; cantidad < numero; cantidad++) {
                cartas.add(new Carta(Carta.TipoCarta.NUMERO, numero));
            }
        }
          
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 2));   
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 4)); 
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 6));   
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 8));   
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 8));   
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, 10)); 
        cartas.add(new Carta(Carta.TipoCarta.MODIFICADOR, -1)); 
        
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
    
    public Carta robarCarta() { 
        if (cartas.isEmpty()) {
            rebarajarDescarte(); 
        }
        
        if (cartas.isEmpty()) {
            return null;
        }
        
        return cartas.remove(0); 
    }
    
    public void descartar(Carta c) { 
        if (c != null) {
            pilaDescarte.add(c);
        }
    }
    
    public void descartarTodas(List<Carta> cartasADescartar) { 
        if (cartasADescartar != null) {
            pilaDescarte.addAll(cartasADescartar);
        }
    }
    
    private void rebarajarDescarte() { 
        if (!pilaDescarte.isEmpty()) { 
            System.out.println("[MAZO] Barajeando " + pilaDescarte.size() + " cartas del descarte");
            cartas.addAll(pilaDescarte); 
            pilaDescarte.clear(); 
            barajar(); 
            System.out.println("[MAZO] Ahora hay " + cartas.size() + " cartas disponibles");
        }
    }
    
    public int getCartasRestantes() { 
        return cartas.size();
    }
    
    public void reiniciar() { 
        cartas.clear(); 
        pilaDescarte.clear(); 
        inicializarMazo(); 
    }
}
