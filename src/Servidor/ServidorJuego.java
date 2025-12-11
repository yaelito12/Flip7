package flip7.servidor;

import flip7.comun.Jugador;
import flip7.comun.MensajeJuego;
import flip7.comun.Usuario;
import flip7.comun.Carta;
import flip7.comun.SalaJuego;
import flip7.comun.EstadoJuego;
import flip7.juego.LogicaJuego;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServidorJuego {
    private static final int PUERTO = 5555;
    private ServerSocket servidorSocket;
    private Map<Integer, ManejadorCliente> todosClientes = new ConcurrentHashMap<>();
    private Map<String, InstanciaSalaJuego> salas = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ManejadorBaseDatos baseDatos;
    private boolean ejecutando;
    private int siguienteIdCliente = 0;
    
    public void iniciar() {
        try {
            baseDatos = new ManejadorBaseDatos();
            servidorSocket = new ServerSocket(PUERTO);
            ejecutando = true;
            System.out.println("========================================");
            System.out.println("   SERVIDOR VOLTEAR 7 - Puerto " + PUERTO);
            System.out.println("   SQLite + Sistema de Espectadores");
            System.out.println("========================================");
            while (ejecutando) {
                try {
                    Socket cliente = servidorSocket.accept();
                    System.out.println("+ Conexion: " + cliente.getInetAddress());
                    executor.execute(new ManejadorCliente(cliente, this, siguienteIdCliente++));
                } catch (IOException e) {
                    if (ejecutando) e.printStackTrace();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    
    public synchronized void manejarLogin(ManejadorCliente manejador, String nombreUsuario, String contrasena) {
        nombreUsuario = nombreUsuario.trim();
        if (!esUsuarioValido(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Usuario invalido.\nSolo letras y numeros.\nSin espacios.\n3 a 15 caracteres."));
            return;
        }
        if (usuarioYaConectado(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Este usuario ya esta conectado"));
            System.out.println("[LOGIN BLOQUEADO] " + nombreUsuario + " duplicado");
            return;
        }
        Usuario usuario = baseDatos.login(nombreUsuario, contrasena);
        if (usuario != null) {
            manejador.setNombreJugador(nombreUsuario);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.loginExitoso(usuario));
            enviarListaSalas(manejador.getIdCliente());
            System.out.println("[LOGIN] " + nombreUsuario + " conectado (ID: " + usuario.getId() + ")");
        } else {
            manejador.enviarMensaje(MensajeJuego.loginFallido("Usuario o contrasena incorrectos"));
        }
    }
    
    public void manejarRegistro(ManejadorCliente manejador, String nombreUsuario, String contrasena) {
        if (!esUsuarioValido(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.registroFallido("Usuario invalido.\nSolo letras y numeros.\nSin espacios.\n3 a 15 caracteres."));
            return;
        }
        if (contrasena == null || contrasena.length() < 4) {
            manejador.enviarMensaje(MensajeJuego.registroFallido("Contrasena: minimo 4 caracteres"));
            return;
        }
        Usuario usuario = baseDatos.registrar(nombreUsuario.trim(), contrasena);
        if (usuario != null) {
            manejador.setNombreJugador(nombreUsuario);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.registroExitoso(usuario));
            enviarListaSalas(manejador.getIdCliente());
            System.out.println("[REGISTRO] " + nombreUsuario + " registrado (ID: " + usuario.getId() + ")");
        } else {
            manejador.enviarMensaje(MensajeJuego.registroFallido("El usuario ya existe"));
        }
    }
    
    public void registrarCliente(ManejadorCliente manejador) { todosClientes.put(manejador.getIdCliente(), manejador); }
    public void desregistrarCliente(int idCliente) { ManejadorCliente manejador = todosClientes.remove(idCliente); if (manejador != null) { String idSala = manejador.getIdSalaActual(); if (idSala != null) salirSala(idCliente, idSala); } }
    public boolean usuarioYaConectado(String nombreUsuario) { for (ManejadorCliente m : todosClientes.values()) if (m.getNombreJugador() != null && m.getNombreJugador().equalsIgnoreCase(nombreUsuario)) return true; return false; }
    private boolean esUsuarioValido(String nombreUsuario) { if (nombreUsuario == null) return false; if (nombreUsuario.contains(" ")) return false; if (!nombreUsuario.matches("^[a-zA-Z0-9]{3,15}$")) return false; return true; }
    public List<SalaJuego> obtenerTodasLasSalas() { List<SalaJuego> todas = new ArrayList<>(); for (InstanciaSalaJuego sala : salas.values()) todas.add(sala.getSala()); return todas; }
    
    public SalaJuego crearSala(int idCliente, String nombreSala, String nombreJugador, int maxJugadores) {
        ManejadorCliente manejador = todosClientes.get(idCliente);
        if (manejador == null) return null;
        if (manejador.getIdSalaActual() != null) { manejador.enviarMensaje(MensajeJuego.errorSala("Ya estas en una sala")); return null; }
        String idSala = UUID.randomUUID().toString().substring(0, 8);
        SalaJuego sala = new SalaJuego(idSala, nombreSala, nombreJugador, idCliente, maxJugadores);
        InstanciaSalaJuego instancia = new InstanciaSalaJuego(sala, this);
        salas.put(idSala, instancia);
        int idJugador = instancia.agregarJugador(manejador, nombreJugador);
        manejador.setIdSalaActual(idSala);
        manejador.setIdJugador(idJugador);
        manejador.setEspectador(false);
        System.out.println("[SALA] Creada: " + nombreSala + " [" + idSala + "] por " + nombreJugador);
        difundirListaSalas();
        return sala;
    }
    
    public void enviarRankings(int idCliente) { ManejadorCliente manejador = todosClientes.get(idCliente); if (manejador != null) { List<Usuario> rankings = baseDatos.obtenerRankings(100); manejador.enviarMensaje(MensajeJuego.respuestaRankings(rankings)); } }
    
    public boolean unirseSala(int idCliente, String idSala, String nombreJugador, boolean comoEspectador) {
        ManejadorCliente manejador = todosClientes.get(idCliente);
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (manejador == null || instancia == null) { if (manejador != null) manejador.enviarMensaje(MensajeJuego.errorSala("Sala no encontrada")); return false; }
        if (manejador.getIdSalaActual() != null) { manejador.enviarMensaje(MensajeJuego.errorSala("Ya estas en una sala")); return false; }
        SalaJuego sala = instancia.getSala();
        if (comoEspectador) {
            instancia.agregarEspectador(manejador, nombreJugador);
            manejador.setIdSalaActual(idSala);
            manejador.setIdJugador(-1);
            manejador.setEspectador(true);
            System.out.println("[ESPEC] " + nombreJugador + " observando [" + idSala + "]");
            manejador.enviarMensaje(MensajeJuego.salaUnida(sala, -1));
            instancia.difundirActualizacionSala();
            difundirListaSalas();
            if (sala.isJuegoIniciado()) {
                manejador.enviarMensaje(MensajeJuego.juegoInicia(instancia.getLogicaJuego().getEstadoJuego().getJugadores()));
                manejador.enviarMensaje(MensajeJuego.estadoJuego(instancia.getLogicaJuego().getEstadoJuego()));
            }
            return true;
        } else {
            if (sala.estaLlena()) { manejador.enviarMensaje(MensajeJuego.errorSala("Sala llena")); return false; }
            if (sala.isJuegoIniciado()) { manejador.enviarMensaje(MensajeJuego.errorSala("Juego en curso")); return false; }
            int idJugador = instancia.agregarJugador(manejador, nombreJugador);
            manejador.setIdSalaActual(idSala);
            manejador.setIdJugador(idJugador);
            manejador.setEspectador(false);
            System.out.println("[UNIR] " + nombreJugador + " -> [" + idSala + "]");
            manejador.enviarMensaje(MensajeJuego.salaUnida(sala, idJugador));
            instancia.difundirActualizacionSala();
            difundirListaSalas();
            return true;
        }
    }
    
    public void salirSala(int idCliente, String idSala) {
        ManejadorCliente manejador = todosClientes.get(idCliente);
        InstanciaSalaJuego instancia = salas.get(idSala);
        if (manejador == null || instancia == null) return;
        if (manejador.esEspectador()) instancia.removerEspectador(idCliente);
        else instancia.removerJugador(idCliente);
        manejador.setIdSalaActual(null);
        manejador.setIdJugador(-1);
        manejador.setEspectador(false);
        if (instancia.estaVacia()) { salas.remove(idSala); System.out.println("[SALA] Eliminada: [" + idSala + "]"); }
        else instancia.difundirActualizacionSala();
        difundirListaSalas();
    }
    
    public void jugadorListo(int idCliente, String idSala) { InstanciaSalaJuego instancia = salas.get(idSala); if (instancia != null) instancia.jugadorListo(idCliente); }
    public void jugadorPide(int idCliente, String idSala) { InstanciaSalaJuego instancia = salas.get(idSala); if (instancia != null) instancia.jugadorPide(idCliente); }
    public void jugadorSePlanta(int idCliente, String idSala) { InstanciaSalaJuego instancia = salas.get(idSala); if (instancia != null) instancia.jugadorSePlanta(idCliente); }
    public void asignarCartaAccion(int idCliente, String idSala, int idObjetivo, Carta carta) { InstanciaSalaJuego instancia = salas.get(idSala); if (instancia != null) instancia.asignarCartaAccion(idCliente, idObjetivo, carta); }
    public void difundirChat(int idCliente, String idSala, String mensaje) { InstanciaSalaJuego instancia = salas.get(idSala); ManejadorCliente manejador = todosClientes.get(idCliente); if (instancia != null && manejador != null) instancia.difundirChat(manejador.getIdJugador(), manejador.getNombreJugador(), mensaje); }
    public void enviarListaSalas(int idCliente) { ManejadorCliente manejador = todosClientes.get(idCliente); if (manejador != null) manejador.enviarMensaje(MensajeJuego.listaSalas(obtenerTodasLasSalas())); }
    private void difundirListaSalas() { List<SalaJuego> todas = obtenerTodasLasSalas(); for (ManejadorCliente manejador : todosClientes.values()) if (manejador.getIdSalaActual() == null) manejador.enviarMensaje(MensajeJuego.listaSalas(todas)); }
    public void alFinJuego(String idSala) { InstanciaSalaJuego instancia = salas.get(idSala); if (instancia != null) { instancia.getSala().setJuegoIniciado(false); difundirListaSalas(); } }
    public ManejadorBaseDatos getBaseDatos() { return baseDatos; }
    
    public static void main(String[] args) { new ServidorJuego().iniciar(); }
}
