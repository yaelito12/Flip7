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

    // =========================================================================
    // LOGIN / REGISTRO
    // =========================================================================
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

        Usuario usuario = baseDatos.registrar(nombre, contrasena);
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
        return nombre != null &&
               !nombre.contains(" ") &&
               nombre.matches("^[a-zA-Z0-9]{3,15}$");
    }

    private boolean usuarioYaConectado(String nombre) {
        for (ManejadorCliente c : todosClientes.values()) {
            if (nombre.equalsIgnoreCase(c.getNombreJugador())) return true;
        }
        return false;
    }

    // =========================================================================
    // CLIENTES
    // =========================================================================

    public void registrarCliente(ManejadorCliente manejador) {
        todosClientes.put(manejador.obtenerIdCliente(), manejador);
    }

    public void desregistrarCliente(int idCliente) {
        ManejadorCliente manejador = todosClientes.remove(idCliente);
        if (manejador != null && manejador.getIdSalaActual() != null) {
            salirSala(idCliente, manejador.getIdSalaActual());
        }
    }

    // =========================================================================
    // SALAS
    // =========================================================================

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
        manejador.setEspectador(false);

        difundirListaSalas();
        return sala;
    }

    public boolean unirseSala(int idCliente, String idSala, String nombreJugador, boolean comoEspectador) {

        ManejadorCliente manejador = todosClientes.get(idCliente);
        InstanciaSalaJuego instancia = salas.get(idSala);

        if (manejador == null || instancia == null) return false;

        SalaJuego sala = instancia.getSala();
        if (!comoEspectador && sala.estaLlena()) return false;

        if (!comoEspectador) {
            int idJugador = instancia.agregarJugador(manejador, nombreJugador);
            manejador.setIdSalaActual(idSala);
            manejador.setIdJugador(idJugador);
            manejador.setEspectador(false);
            manejador.enviarMensaje(MensajeJuego.salaUnida(sala, idJugador));
        } else {
            instancia.agregarEspectador(manejador, nombreJugador);
            manejador.setIdSalaActual(idSala);
            manejador.setEspectador(true);
            manejador.enviarMensaje(MensajeJuego.salaUnida(sala, -1));
        }

        instancia.difundirActualizacionSala();
        difundirListaSalas();
        return true;
    }

    public void salirSala(int idCliente, String idSala) {

        InstanciaSalaJuego instancia = salas.get(idSala);
        if (instancia == null) return;

        ManejadorCliente manejador = todosClientes.get(idCliente);

        if (manejador != null && manejador.esEspectador()) instancia.removerEspectador(idCliente);
        else instancia.removerJugador(idCliente);

        manejador.setIdSalaActual(null);
        manejador.setIdJugador(-1);

        if (instancia.estaVacia()) salas.remove(idSala);
        else instancia.difundirActualizacionSala();

        difundirListaSalas();
    }

    // CHAT
    public void difundirChat(int idCliente, String idSala, String mensaje) {
        InstanciaSalaJuego instancia = salas.get(idSala);
        ManejadorCliente cliente = todosClientes.get(idCliente);
        if (instancia != null && cliente != null) {
            instancia.difundirChat(cliente.getIdJugador(), cliente.getNombreJugador(), mensaje);
        }
    }

    public void enviarListaSalas(int idCliente) {
        ManejadorCliente m = todosClientes.get(idCliente);
        if (m != null) m.enviarMensaje(MensajeJuego.listaSalas(obtenerTodasLasSalas()));
    }

    private void difundirListaSalas() {
        List<SalaJuego> todas = obtenerTodasLasSalas();
        for (ManejadorCliente m : todosClientes.values()) {
            if (m.getIdSalaActual() == null) {
                m.enviarMensaje(MensajeJuego.listaSalas(todas));
            }
        }
    }

    public ManejadorBaseDatos getBaseDatos() { return baseDatos; }

    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
}

// ============================================================================
// INSTANCIA DE SALA (ESPECTADORES + CHAT)
// ============================================================================

class InstanciaSalaJuego implements LogicaJuego.EscuchaEventosJuego {

    private SalaJuego sala;
    private LogicaJuego logica;
    private ServidorJuego servidor;

    private Map<Integer, ManejadorCliente> jugadores = new ConcurrentHashMap<>();
    private Map<Integer, ManejadorCliente> espectadores = new ConcurrentHashMap<>();
    private Map<Integer, Integer> clienteAIdJugador = new ConcurrentHashMap<>();
    private Set<Integer> jugadoresListos = new HashSet<>();

    public InstanciaSalaJuego(SalaJuego sala, ServidorJuego servidor) {
        this.sala = sala;
        this.servidor = servidor;
        this.logica = new LogicaJuego();
        this.logica.agregarEscucha(this);
    }

    public SalaJuego getSala() { return sala; }

    public boolean estaVacia() {
        return jugadores.isEmpty() && espectadores.isEmpty();
    }

    // ========================================================
    // NOMBRES ÚNICOS EN LA SALA
    // ========================================================

    private String generarNombreUnico(String nombre) {
        Set<String> existentes = new HashSet<>();

        for (Jugador j : logica.getEstadoJuego().getJugadores())
            existentes.add(j.getNombre().toLowerCase());

        if (!existentes.contains(nombre.toLowerCase())) return nombre;

        int n = 2;
        while (existentes.contains((nombre + n).toLowerCase())) n++;
        return nombre + n;
    }

    // ========================================================
    // AGREGAR JUGADORES / ESPECTADORES
    // ========================================================

    public int agregarJugador(ManejadorCliente manejador, String nombre) {

        String nombreUnico = generarNombreUnico(nombre);

        manejador.setNombreJugador(nombreUnico);

        int idJugador = logica.agregarJugador(nombreUnico);
        jugadores.put(manejador.obtenerIdCliente(), manejador);
        clienteAIdJugador.put(manejador.obtenerIdCliente(), idJugador);

        sala.agregarJugador(nombreUnico);

        difundir(MensajeJuego.jugadorUnido(idJugador, nombreUnico));
        return idJugador;
    }

    public void agregarEspectador(ManejadorCliente manejador, String nombre) {
        manejador.setNombreJugador(nombre + " (espec)");
        espectadores.put(manejador.obtenerIdCliente(), manejador);
        sala.agregarEspectador(nombre);
    }

    // ========================================================
    // REMOVER
    // ========================================================

    public void removerJugador(int idCliente) {
        ManejadorCliente manejador = jugadores.remove(idCliente);
        Integer idJugador = clienteAIdJugador.remove(idCliente);

        if (manejador == null || idJugador == null) return;

        sala.removerJugador(manejador.getNombreJugador());
        logica.removerJugador(idJugador);

        difundir(MensajeJuego.jugadorSalio(idJugador, manejador.getNombreJugador()));
    }

    public void removerEspectador(int idCliente) {
        ManejadorCliente m = espectadores.remove(idCliente);
        if (m != null) sala.removerEspectador(m.getNombreJugador());
    }

    // ========================================================
    // CHAT
    // ========================================================

    public void difundirChat(int idJugador, String nombre, String mensaje) {
        difundir(MensajeJuego.chat(idJugador, nombre, mensaje));
    }

    // ========================================================
    // DIFUSIÓN GENERAL
    // ========================================================

    public void difundir(MensajeJuego msg) {
        for (ManejadorCliente m : jugadores.values()) m.enviarMensaje(msg);
        for (ManejadorCliente m : espectadores.values()) m.enviarMensaje(msg);
    }

    public void difundirActualizacionSala() {
        MensajeJuego msg = MensajeJuego.salaActualizada(sala);
        difundir(msg);
    }

    // ========================================================
    // LOGICA DE JUGADORES LISTOS (igual que commit 3)
    // ========================================================

    public void jugadorListo(int idCliente) {
        jugadoresListos.add(idCliente);
        if (jugadoresListos.size() == jugadores.size() && jugadores.size() >= 2) {
            sala.setJuegoIniciado(true);
            logica.iniciarJuego();
        }
    }

    @Override
    public void alRepartirCarta(int id, Carta c) {}
    @Override
    public void alEliminarJugador(int id, Carta c) {}
    @Override
    public void alPlantarseJugador(int id) {}
    @Override
    public void alCongelarJugador(int id) {}
    @Override
    public void alRobarCartaAccion(int id, Carta c) {}
    @Override
    public void alCambiarTurno(int id) {}
    @Override
    public void alActualizarEstado(EstadoJuego e) {}
    @Override
    public void alNecesitarObjetivoAccion(int id, Carta c, List<Jugador> candidatos) {}
    @Override
    public void alFinRonda(List<Jugador> jugadores, int ronda) {}
    @Override
    public void alFinJuego(Jugador ganador) {}
}
