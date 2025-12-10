/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package juego;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gerth
 */
public class LogicaJuego {
    
     private EstadoJuego estadoJuego = new EstadoJuego();
    private Mazo mazo = new Mazo();
    private List<EscuchaEventosJuego> escuchas = new ArrayList<>();
    private boolean esperandoObjetivoAccion = false;
    private int jugadorEsperandoAccion = -1;
    private Carta cartaAccionPendiente = null;
    
    private boolean procesandoVoltear3 = false;
    private int idJugadorVoltear3 = -1;
    private int cartasRestantesVoltear3 = 0;
    
    public interface EscuchaEventosJuego {
        void alRepartirCarta(int idJugador, Carta carta);
        void alEliminarJugador(int idJugador, Carta carta);
        void alPlantarseJugador(int idJugador);
        void alCongelarJugador(int idJugador);
        void alRobarCartaAccion(int idJugador, Carta carta);
        void alFinRonda(List<Jugador> jugadores, int numeroRonda);
        void alFinJuego(Jugador ganador);
        void alCambiarTurno(int idJugador);
        void alActualizarEstado(EstadoJuego estado);
        void alNecesitarObjetivoAccion(int idJugador, Carta carta, List<Jugador> jugadoresActivos);
    }
    
     public void agregarEscucha(EscuchaEventosJuego e) { escuchas.add(e); }
    
    public int agregarJugador(String nombre) {
        int id = estadoJuego.getJugadores().size();
        estadoJuego.getJugadores().add(new Jugador(nombre, id));
        return id;
    }
    
    public void removerJugador(int id) {
        Jugador j = estadoJuego.getJugadorPorId(id);
        if (j != null) j.setConectado(false);
    }
    
    public boolean puedeIniciarJuego() { return estadoJuego.getJugadores().size() >= 2; }
    
    public void iniciarJuego() {
        if (!puedeIniciarJuego()) return;
        estadoJuego.setFase(EstadoJuego.Fase.REPARTIENDO);
        estadoJuego.setNumeroRonda(1);
        mazo.reiniciar();
        iniciarRonda();
    }
    
    public void iniciarRonda() {
        for (Jugador j : estadoJuego.getJugadores()) j.reiniciarParaNuevaRonda();
        estadoJuego.setFase(EstadoJuego.Fase.REPARTIENDO);
        
        esperandoObjetivoAccion = false;
        jugadorEsperandoAccion = -1;
        cartaAccionPendiente = null;
        procesandoVoltear3 = false;
        idJugadorVoltear3 = -1;
        cartasRestantesVoltear3 = 0;
        
        repartirCartasIniciales();
        
        estadoJuego.setFase(EstadoJuego.Fase.JUGANDO);
        
        int indiceInicio = estadoJuego.getIndiceRepartidor();
        Jugador jugadorInicio = estadoJuego.getJugadores().get(indiceInicio);
        if (!jugadorInicio.estaConectado()) {
            indiceInicio = getSiguienteIndiceJugadorActivo(indiceInicio);
            if (indiceInicio == -1) indiceInicio = 0;
        }
        
        estadoJuego.setIndiceJugadorActual(indiceInicio);
        notificarCambioTurno();
        notificarActualizacionEstado();
    }
    
    private void repartirCartasIniciales() {
        List<Jugador> jugadores = estadoJuego.getJugadores();
        int inicio = (estadoJuego.getIndiceRepartidor() + 1) % jugadores.size();
        
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get((inicio + i) % jugadores.size());
            if (!jugador.estaConectado()) continue;
            
            Carta carta = mazo.robarCarta();
            if (carta != null) {
                if (carta.esCartaAccion()) {
                    mazo.descartar(carta);
                    i--;
                } else {
                    boolean exito = jugador.agregarCarta(carta);
                    if (exito) {
                        notificarCartaRepartida(jugador.getId(), carta);
                    }
                }
            }
        }
        actualizarInfoMazo();
    }
    
}
