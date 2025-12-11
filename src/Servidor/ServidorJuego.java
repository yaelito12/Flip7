package flip7.servidor;

import flip7.comun.*;
import flip7.juego.LogicaJuego;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class ServidorJuego {

    private static final int PUERTO = 5555;

    private ServerSocket socketServidor;
    private Map<Integer, ManejadorCliente> todosClientes = new ConcurrentHashMap<>();
    private Map<String, InstanciaSalaJuego> salas = new ConcurrentHashMap<>();

    private ExecutorService ejecutor = Executors.newCachedThreadPool();
    private boolean ejecutando;
    private int siguienteIdCliente = 0;

    private ManejadorBaseDatos baseDatos;

    public void iniciar() {
        try {
            baseDatos = new ManejadorBaseDatos();
            socketServidor = new ServerSocket(PUERTO);
            ejecutando = true;

            System.out.println("========================================");
            System.out.println(" SERVIDOR FLIP 7 - Puerto " + PUERTO);
            System.out.println(" Base de datos lista");
            System.out.println("========================================");

            while (ejecutando) {
                try {
                    Socket cliente = socketServidor.accept();

                    ejecutor.execute(
                        new ManejadorCliente(cliente, this, siguienteIdCliente++)
                    );

                } catch (IOException e) {
                    if (ejecutando) e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void manejarLogin(ManejadorCliente manejador, String nombre, String contrasena) {

        if (!esUsuarioValido(nombre)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Usuario invalido"));
            return;
        }

        if (usuarioYaConectado(nombre)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Usuario ya conectado"));
            return;
        }

        Usuario usuario = baseDatos.login(nombre, contrasena);

        if (usuario != null) {
            manejador.setNombreJugador(nombre);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.loginExitoso(usuario));
            enviarListaSalas(manejador.obtenerIdCliente());
        } else {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Credenciales incorrectas"));
        }
    }

    public void manejarRegistro(ManejadorCliente manejador, String nombre, String contrasena) {

        if (!esUsuarioValido(nombre)) {
            manejador.enviarMensaje(MensajeJuego.registroFallido("Usuario invalido"));
            return;
        }

        if (contrasena == null || contrasena.length() < 4) {
            manejador.enviarMensaje(MensajeJuego.registroFallido("Contrasena muy corta"));
            return;
        }

        Usuario usuario = baseDatos.registrar(nombre.trim(), contrasena);

        if (usuario != null) {
            manejador.setNombreJugador(nombre);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.registroExitoso(usuario));
            enviarListaSalas(manejador.obtenerIdCliente());
        } else {
            manejador.enviarMensaje(MensajeJuego.registroFallido("Usuario ya existe"));
        }
    }

    private boolean esUsuarioValido(String nombre) {
        if (nombre == null) return false;
        if (nombre.contains(" ")) return false;
        return nombre.matches("^[a-zA-Z0-9]{3,15}$");
    }

    private boolean usuarioYaConectado(String nombre) {
        for (ManejadorCliente c : todosClientes.values()) {
            if (nombre.equalsIgnoreCase(c.getNombreJugador())) return true;
        }
        return false;
    }

    public void registrarCliente(ManejadorCliente manejador) {
        todosClientes.put(manejador.obtenerIdCliente(), manejador);
    }

    public void desregistrarCliente(int idCliente) {
        ManejadorCliente manejador = todosClientes.remove(idCliente);
        
        if (manejador != null && manejador.getIdSalaActual() != null) {
            salirSala(idCliente, manejador.getIdSalaActual());
        }
    }

    public List<SalaJuego> obtenerTodasLasSalas() {
        List<SalaJuego> lista = new ArrayList<>();
        for (InstanciaSalaJuego i : salas.values()) lista.add(i.getSala());
        return lista;
    }

    public SalaJuego crearSala(int idCliente, String nombreSala, String nombreJugador, int maxJugadores) {

        ManejadorCliente manejador = todosClientes.get(idCliente);
        if (manejador == null) return null;

        if (manejador.getIdSalaActual() != null) {
            manejador.enviarMensaje(MensajeJuego.errorSala("Ya estas en una sala"));
            return null;
        }

        String idSala = UUID.randomUUID().toString().substring(0, 8);
        SalaJuego sala = new SalaJuego(idSala, nombreSala, nombreJugador, idCliente, maxJugadores);

        InstanciaSalaJuego instancia = new InstanciaSalaJuego(sala, this);
        salas.put(idSala, instancia);

        int idJugador = instancia.agregarJugador(manejador, nombreJugador);
        manejador.setIdSalaActual(idSala);
        manejador.setIdJugador(idJugador);

        difundirListaSalas();
        return sala;
    }

    public boolean unirseSala(int idCliente, String idSala, String nombreJugador) {

        ManejadorCliente manejador = todosClientes.get(idCliente);
        InstanciaSalaJuego instancia = salas.get(idSala);

        if (manejador == null || instancia == null) return false;

        SalaJuego sala = instancia.getSala();

        if (sala.estaLlena()) return false;
        if (sala.isJuegoIniciado()) return false;

        int idJugador = instancia.agregarJugador(manejador, nombreJugador);
        manejador.setIdSalaActual(idSala);
        manejador.setIdJugador(idJugador);

        manejador.enviarMensaje(MensajeJuego.salaUnida(sala, idJugador));
        instancia.difundirActualizacionSala();
        difundirListaSalas();
        return true;
    }

    public void salirSala(int idCliente, String idSala) {
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (instancia == null) return;

        instancia.removerJugador(idCliente);

        ManejadorCliente manejador = todosClientes.get(idCliente);
        if (manejador != null) {
            manejador.setIdSalaActual(null);
            manejador.setIdJugador(-1);
        }

        if (instancia.estaVacia()) {
            salas.remove(idSala);
        } else {
            instancia.difundirActualizacionSala();
        }

        difundirListaSalas();
    }

    public void enviarListaSalas(int idCliente) {
        ManejadorCliente manejador = todosClientes.get(idCliente);
        if (manejador != null) {
            manejador.enviarMensaje(MensajeJuego.listaSalas(obtenerTodasLasSalas()));
        }
    }

    private void difundirListaSalas() {
        List<SalaJuego> todas = obtenerTodasLasSalas();
        for (ManejadorCliente m : todosClientes.values()) {
            if (m.getIdSalaActual() == null) {
                m.enviarMensaje(MensajeJuego.listaSalas(todas));
            }
        }
    }

    public void jugadorListo(int idCliente, String idSala) {
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (instancia != null) instancia.jugadorListo(idCliente);
    }

    public void jugadorPide(int idCliente, String idSala) {
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (instancia != null) instancia.jugadorPide(idCliente);
    }

    public void jugadorSePlanta(int idCliente, String idSala) {
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (instancia != null) instancia.jugadorSePlanta(idCliente);
    }

    public ManejadorBaseDatos getBaseDatos() { return baseDatos; }

    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
}
class InstanciaSalaJuego implements LogicaJuego.EscuchaEventosJuego {

    private SalaJuego sala;
    private LogicaJuego logica;
    private ServidorJuego servidor;

    private Map<Integer, ManejadorCliente> jugadores = new ConcurrentHashMap<>();
    private Map<Integer, Integer> clienteAIdJugador = new ConcurrentHashMap<>();

    private Set<Integer> jugadoresListos = new HashSet<>();

    public InstanciaSalaJuego(SalaJuego sala, ServidorJuego servidor) {
        this.sala = sala;
        this.servidor = servidor;
        this.logica = new LogicaJuego();
        this.logica.agregarEscucha(this);
    }

    public SalaJuego getSala() { return sala; }

    public boolean estaVacia() { return jugadores.isEmpty(); }

    public int agregarJugador(ManejadorCliente manejador, String nombre) {

        int idJugador = logica.agregarJugador(nombre);

        jugadores.put(manejador.obtenerIdCliente(), manejador);
        clienteAIdJugador.put(manejador.obtenerIdCliente(), idJugador);

        sala.agregarJugador(nombre);

        return idJugador;
    }

    public void removerJugador(int idCliente) {

        ManejadorCliente manejador = jugadores.remove(idCliente);
        Integer idJugador = clienteAIdJugador.remove(idCliente);

        if (manejador == null || idJugador == null) return;

        sala.removerJugador(manejador.getNombreJugador());
        logica.removerJugador(idJugador);
    }

    public void difundirActualizacionSala() {
        MensajeJuego msg = MensajeJuego.salaActualizada(sala);
        for (ManejadorCliente m : jugadores.values()) m.enviarMensaje(msg);
    }


    public void jugadorListo(int idCliente) {
        jugadoresListos.add(idCliente);

        if (jugadoresListos.size() == jugadores.size() && jugadores.size() >= 2) {
            sala.setJuegoIniciado(true);
            logica.iniciarJuego();
        }
    }

    public void jugadorPide(int idCliente) {
        Integer id = clienteAIdJugador.get(idCliente);
        if (id != null) logica.jugadorPide(id);
    }

    public void jugadorSePlanta(int idCliente) {
        Integer id = clienteAIdJugador.get(idCliente);
        if (id != null) logica.jugadorSePlanta(id);
    }

    public void alRepartirCarta(int id, Carta c) {}
    public void alEliminarJugador(int id, Carta c) {}
    public void alPlantarseJugador(int id) {}
    public void alCongelarJugador(int id) {}
    public void alRobarCartaAccion(int id, Carta c) {}
    public void alCambiarTurno(int id) {}
    public void alActualizarEstado(EstadoJuego e) {}
    public void alNecesitarObjetivoAccion(int id, Carta c, List<Jugador> candidatos) {}
    public void alFinRonda(List<Jugador> jugadores, int ronda) {}
    public void alFinJuego(Jugador ganador) {}
}
