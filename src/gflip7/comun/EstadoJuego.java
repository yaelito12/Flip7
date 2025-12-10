package gflip7.comun;

import java.io.Serializable;
import java.util.*;

public class EstadoJuego implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Fase { 
        ESPERANDO_JUGADORES, 
        REPARTIENDO, 
        JUGANDO, 
        FIN_RONDA, 
        FIN_JUEGO 
    }
    
    private List<Jugador> jugadores = new ArrayList<>();
    private int indiceJugadorActual;
    private int indiceRepartidor;
    private int numeroRonda = 1;
    private int tamanoMazo;
    private int puntajeGanador = 200;
    private Fase fase = Fase.ESPERANDO_JUGADORES;
    
    // ✅ GETTERS Y SETTERS BÁSICOS (COMMIT 1)

    public List<Jugador> getJugadores() { 
        return jugadores; 
    }
    
    public int getIndiceJugadorActual() { 
        return indiceJugadorActual; 
    }
    
    public void setIndiceJugadorActual(int i) { 
        indiceJugadorActual = i; 
    }
    
    public int getIndiceRepartidor() { 
        return indiceRepartidor; 
    }
    
    public void setIndiceRepartidor(int i) { 
        indiceRepartidor = i; 
    }
    
    public int getNumeroRonda() { 
        return numeroRonda; 
    }
    
    public void setNumeroRonda(int r) { 
        numeroRonda = r; 
    }
    
    public Fase getFase() { 
        return fase; 
    }
    
    public void setFase(Fase p) { 
        fase = p; 
    }
    
    public int getTamanoMazo() { 
        return tamanoMazo; 
    }
    
    public void setTamanoMazo(int s) { 
        tamanoMazo = s; 
    }
    
    public int getPuntajeGanador() { 
        return puntajeGanador; 
    }

    // ✅ LÓGICA DEL ESTADO DEL JUEGO (COMMIT 2)

    public Jugador getJugadorActual() { 
        if (indiceJugadorActual >= 0 && indiceJugadorActual < jugadores.size()) {
            return jugadores.get(indiceJugadorActual);
        }
        return null; 
    }
    
    public Jugador getJugadorPorId(int id) { 
        for (Jugador j : jugadores) {
            if (j.getId() == id) {
                return j;
            }
        }
        return null; 
    }
    
    public List<Jugador> getJugadoresActivos() { 
        List<Jugador> activos = new ArrayList<>(); 
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                activos.add(j);
            }
        }
        return activos; 
    }
    
    public boolean esFinRonda() { 
        for (Jugador j : jugadores) {
            if (j.estaActivo()) {
                return false;
            }
        }
        return true; 
    }
    
    public boolean esFinJuego() { 
        for (Jugador j : jugadores) {
            if (j.getPuntajeTotal() >= puntajeGanador) {
                return true;
            }
        }
        return false; 
    }
    
    public Jugador getGanador() { 
        Jugador ganador = null; 
        int maxPuntaje = -1; 
        
        for (Jugador j : jugadores) {
            if (j.getPuntajeTotal() > maxPuntaje) { 
                maxPuntaje = j.getPuntajeTotal(); 
                ganador = j; 
            }
        }
        
        return ganador; 
    }
}