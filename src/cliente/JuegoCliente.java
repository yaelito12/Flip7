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
    private String idSalaActual;
    private EstadoJuego estadoActual;

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
            new Thread(() -> escucharServidor()).start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void escucharServidor() {
        try {
            while (conectado) {
                MensajeJuego msg = (MensajeJuego) in.readObject();
                procesarMensaje(msg);
            }
        } catch (Exception e) {
            for (EscuchaClienteJuego esc : escuchas)
                esc.alDesconectar();
        }
        
    }
    
    private void procesarMensaje(MensajeJuego msg) {
        switch (msg.getTipo()) {

            case CONECTADO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alConectar(msg.getIdJugador(), msg.getNombreJugador());
                break;

            case LOGIN_EXITOSO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alLoginExitoso(msg.getUsuario());
                break;

            case LOGIN_FALLIDO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alLoginFallido(msg.getMensaje());
                break;

            case REGISTRO_EXITOSO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alRegistroExitoso(msg.getUsuario());
                break;

            case REGISTRO_FALLIDO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alRegistroFallido(msg.getMensaje());
                break;

            case LISTA_SALAS:
                for (EscuchaClienteJuego e : escuchas)
                    e.alListaSalas(msg.getSalas());
                break;

            case SALA_CREADA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alCrearSala(msg.getSala(), msg.getIdJugador());
                break;

            case SALA_UNIDA:
                idSalaActual = msg.getSala().getIdSala();
                for (EscuchaClienteJuego e : escuchas)
                    e.alUnirseSala(msg.getSala(), msg.getIdJugador());
                break;

            case SALA_ACTUALIZADA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alActualizarSala(msg.getSala());
                break;

            case ERROR_SALA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alErrorSala(msg.getMensaje());
                break;

            case JUGADOR_UNIDO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alUnirseJugador(msg.getIdJugador(), msg.getNombreJugador());
                break;

            case JUGADOR_SALIO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alSalirJugador(msg.getIdJugador(), msg.getNombreJugador());
                break;

            case JUEGO_INICIA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alIniciarJuego(msg.getJugadores());
                break;

            case TU_TURNO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alTuTurno(msg.getIdJugador());
                break;

            case CARTA_REPARTIDA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alRepartirCarta(msg.getIdJugador(), msg.getCarta());
                break;

            case JUGADOR_ELIMINADO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alJugadorEliminado(msg.getIdJugador(), msg.getCarta());
                break;

            case JUGADOR_PLANTADO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alJugadorPlantado(msg.getIdJugador());
                break;

            case JUGADOR_CONGELADO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alJugadorCongelado(msg.getIdJugador());
                break;

            case CARTA_ACCION_ROBADA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alRobarCartaAccion(msg.getIdJugador(), msg.getCarta());
                break;

            case ELEGIR_OBJETIVO_ACCION:
                for (EscuchaClienteJuego e : escuchas)
                    e.alElegirObjetivoAccion(msg.getCarta(), msg.getJugadores());
                break;

            case ESTADO_JUEGO:
                estadoActual = msg.getEstadoJuego();
                for (EscuchaClienteJuego e : escuchas)
                    e.alActualizarEstado(msg.getEstadoJuego());
                break;

            case FIN_RONDA:
                for (EscuchaClienteJuego e : escuchas)
                    e.alFinRonda(msg.getJugadores(), msg.getNumeroRonda());
                break;

            case FIN_JUEGO:
                for (EscuchaClienteJuego e : escuchas)
                    e.alFinJuego(msg.getJugadores(), msg.getIdJugador());
                break;

            case CHAT_DIFUSION:
                for (EscuchaClienteJuego e : escuchas)
                    e.alMensajeChat(msg.getIdJugador(), msg.getNombreJugador(), msg.getMensaje());
                break;

            case RESPUESTA_RANKINGS:
                for (EscuchaClienteJuego e : escuchas)
                    e.alRecibirRankings(msg.getRankings());
                break;
        }
    }

    public void desconectar() {
        try {
            conectado = false;
            if (socket != null) socket.close();
        } catch (IOException e) {}
    }

    public void login(String usuario, String contrasena) {
        enviar(MensajeJuego.login(usuario, contrasena));
    }

    public void registrar(String usuario, String contrasena) {
        enviar(MensajeJuego.registro(usuario, contrasena));
    }

    public void crearSala(String nombreSala, int max) {
        enviar(MensajeJuego.crearSala(nombreSala, nombreJugador, max));
    }

    public void unirseSala(String idSala) {
        enviar(MensajeJuego.unirseSala(idSala, nombreJugador));
    }

    public void unirseSalaComoEspectador(String idSala) {
        enviar(MensajeJuego.unirseSalaComoEspectador(idSala, nombreJugador));
    }

    public void salirSala() {
        enviar(MensajeJuego.salirSala());
        idSalaActual = null;
    }

    public void solicitarSalas() {
        enviar(MensajeJuego.solicitarSalas());
    }

    public void solicitarRankings() {
        enviar(new MensajeJuego(MensajeJuego.TipoMensaje.OBTENER_RANKINGS));
    }

    public void listo() {
        enviar(new MensajeJuego(MensajeJuego.TipoMensaje.LISTO));
    }

    public void pedir() {
        enviar(new MensajeJuego(MensajeJuego.TipoMensaje.PEDIR));
    }

    public void plantarse() {
        enviar(new MensajeJuego(MensajeJuego.TipoMensaje.PLANTARSE));
    }

    public void enviarChat(String msg) {
        MensajeJuego m = new MensajeJuego(MensajeJuego.TipoMensaje.MENSAJE_CHAT);
        m.setNombreJugador(nombreJugador);
        m.setCarta(null);
        m.setIdJugador(-1);
        m.setNombreJugador(nombreJugador);
        m.setCarta(null);
        m.setIdJugador(-1);
        try {
            var field = MensajeJuego.class.getDeclaredField("mensaje");
            field.setAccessible(true);
            field.set(m, msg);
        } catch (Exception ignored) {}
        enviar(m);
    }

    public String getIdSalaActual() {
        return idSalaActual;
    }
    public void setNombreJugador(String nombreJugador) {
    this.nombreJugador = nombreJugador;
}


    public void asignarCartaAccion(int idJugador, Carta carta) {
        MensajeJuego m = new MensajeJuego(MensajeJuego.TipoMensaje.ASIGNAR_CARTA_ACCION);
        try {
            var f1 = MensajeJuego.class.getDeclaredField("idJugadorObjetivo");
            var f2 = MensajeJuego.class.getDeclaredField("carta");
            f1.setAccessible(true);
            f2.setAccessible(true);
            f1.set(m, idJugador);
            f2.set(m, carta);
        } catch (Exception ignored) {}
        enviar(m);
    }

    public EstadoJuego getEstadoJuegoActual() {
        return estadoActual;
    }

    private void enviar(MensajeJuego m) {
        if (!conectado) return; 
        try {
            out.writeObject(m);
            out.flush();
            out.reset();
        } catch (IOException ignored) {}
    }
}

    