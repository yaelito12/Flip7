package cliente;

import gflip7.comun.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JuegoCliente {

    public interface EscuchaClienteJuego {
        void alConectar(int idJugador, String nombreJugador);
        void alDesconectar();
        void alLoginExitoso(Usuario usuario);
        void alLoginFallido(String razon);
        void alRegistroExitoso(Usuario usuario);
        void alRegistroFallido(String razon);
        void alUnirseJugador(int idJugador, String nombreJugador);
        void alSalirJugador(int idJugador, String nombreJugador);
        void alIniciarJuego(List<Jugador> jugadores);
        void alIniciarRonda(int numeroRonda);
        void alTuTurno(int idJugador);
        void alRepartirCarta(int idJugador, Carta carta);
        void alJugadorEliminado(int idJugador, Carta carta);
        void alJugadorPlantado(int idJugador);
        void alJugadorCongelado(int idJugador);
        void alRobarCartaAccion(int idJugador, Carta carta);
        void alElegirObjetivoAccion(Carta carta, List<Jugador> jugadoresActivos);
        void alFinRonda(List<Jugador> jugadores, int numeroRonda);
        void alFinJuego(List<Jugador> jugadores, int idGanador);
        void alActualizarEstado(EstadoJuego estado);
        void alMensajeChat(int idJugador, String nombreJugador, String mensaje);
        void alError(String mensaje);
        void alListaSalas(List<SalaJuego> salas);
        void alCrearSala(SalaJuego sala, int idJugador);
        void alUnirseSala(SalaJuego sala, int idJugador);
        void alActualizarSala(SalaJuego sala);
        void alErrorSala(String error);
        void alRecibirRankings(List<Usuario> rankings);
    }

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean conectado = false;
    private String nombreJugador;

    private List<EscuchaClienteJuego> escuchas = new ArrayList<>();

    public void agregarEscucha(EscuchaClienteJuego e) {
        escuchas.add(e);
    }

    public boolean estaConectado() {
        return conectado;
    }

    public boolean conectar(String host, int puerto, String nombre) {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            conectado = true;
            this.nombreJugador = nombre;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void desconectar() {
        try {
            conectado = false;
            if (socket != null) socket.close();
        } catch (IOException e) {}
    }

    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

    public void login(String usuario, String contrasena) { }
    public void registrar(String usuario, String contrasena) { }
    public void crearSala(String nombreSala, int max) { }
    public void unirseSala(String idSala) { }
    public void unirseSalaComoEspectador(String idSala) { }
    public void salirSala() { }
    public void solicitarSalas() { }
    public void solicitarRankings() { }
    public void listo() { }
    public void pedir() { }
    public void plantarse() { }
    public void enviarChat(String msg) { }
    public String getIdSalaActual() { return null; }
    public void asignarCartaAccion(int idJugador, Carta carta) { }

    public EstadoJuego getEstadoJuegoActual() { return null; }
}
