package servidor;

import gflip7.comun.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class ServidorJuego {

    private static final int PUERTO = 5555;
    private ServerSocket socketServidor;
    private Map<Integer, ManejadorCliente> todosClientes = new ConcurrentHashMap<>();
    private Map<String, SalaJuego> salas = new ConcurrentHashMap<>();
    private Map<String, Set<ManejadorCliente>> miembrosSalas = new ConcurrentHashMap<>();
    private Map<String, Set<String>> jugadoresListos = new ConcurrentHashMap<>();
    private ExecutorService ejecutor = Executors.newCachedThreadPool();
    private boolean ejecutando;
    private int siguienteIdCliente = 0;
    private int siguienteIdSala = 0;

    public void iniciar() {
        try {
            socketServidor = new ServerSocket(PUERTO);
            ejecutando = true;

            System.out.println("========================================");
            System.out.println("   SERVIDOR FLIP 7 - Puerto " + PUERTO);
            System.out.println("========================================");

            while (ejecutando) {
                try {
                    Socket cliente = socketServidor.accept();
                    System.out.println("+ Conexión: " + cliente.getInetAddress());

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

    public void registrarCliente(ManejadorCliente manejador) {
        todosClientes.put(manejador.obtenerIdCliente(), manejador);
        System.out.println("[Servidor] Clientes conectados: " + todosClientes.size());
    }

    public void desregistrarCliente(int idCliente) {
        todosClientes.remove(idCliente);
        System.out.println("- Cliente desconectado: " + idCliente);
        System.out.println("[Servidor] Clientes conectados: " + todosClientes.size());
    }

    public synchronized SalaJuego crearSala(String nombreSala, String nombreHost, 
                                           int idHost, int maxJugadores, ManejadorCliente creador) {
        String idSala = "SALA_" + (siguienteIdSala++);
        
        SalaJuego nuevaSala = new SalaJuego(idSala, nombreSala, nombreHost, idHost, maxJugadores);
        salas.put(idSala, nuevaSala);
        
        Set<ManejadorCliente> miembros = new HashSet<>();
        miembros.add(creador);
        miembrosSalas.put(idSala, miembros);
        
        jugadoresListos.put(idSala, new HashSet<>());
        
        System.out.println("[Servidor] Sala creada: " + nombreSala + " (" + idSala + ")");
        return nuevaSala;
    }

    public synchronized String unirseASala(String idSala, String nombreJugador, int idJugador,
                                          boolean esEspectador, ManejadorCliente cliente) {
        SalaJuego sala = salas.get(idSala);
        
        if (sala == null) {
            return "La sala no existe";
        }
        
        if (sala.isJuegoIniciado()) {
            return "La partida ya ha comenzado";
        }
        
        if (!esEspectador && sala.estaLlena()) {
            return "La sala está llena";
        }
        
        if (esEspectador) {
            sala.agregarEspectador(nombreJugador);
        } else {
            sala.agregarJugador(nombreJugador);
        }
        
        Set<ManejadorCliente> miembros = miembrosSalas.get(idSala);
        if (miembros != null) {
            miembros.add(cliente);
        }
        
        System.out.println("[Servidor] " + nombreJugador + " se unió a " + idSala + 
                         (esEspectador ? " (espectador)" : ""));
        
        return null;
    }

    public synchronized void salirDeSala(String idSala, String nombreJugador, ManejadorCliente cliente) {
        SalaJuego sala = salas.get(idSala);
        if (sala == null) return;
        
        sala.removerJugador(nombreJugador);
        sala.removerEspectador(nombreJugador);
        
        Set<ManejadorCliente> miembros = miembrosSalas.get(idSala);
        if (miembros != null) {
            miembros.remove(cliente);
        }
        
        Set<String> listos = jugadoresListos.get(idSala);
        if (listos != null) {
            listos.remove(nombreJugador);
        }
        
        System.out.println("[Servidor] " + nombreJugador + " salió de " + idSala);
        
        if (sala.estaVacia()) {
            salas.remove(idSala);
            miembrosSalas.remove(idSala);
            jugadoresListos.remove(idSala);
            System.out.println("[Servidor] Sala eliminada: " + idSala);
            difundirListaSalas();
        } else {
            actualizarSalaParaMiembros(idSala);
        }
    }

    public synchronized void marcarJugadorListo(String idSala, String nombreJugador) {
        Set<String> listos = jugadoresListos.get(idSala);
        if (listos != null) {
            listos.add(nombreJugador);
            System.out.println("[Servidor] " + nombreJugador + " está listo en " + idSala);
            
            SalaJuego sala = salas.get(idSala);
            if (sala != null) {
                actualizarSalaParaMiembros(idSala);
                
                if (listos.size() == sala.getJugadoresActuales() && sala.getJugadoresActuales() >= 2) {
                    System.out.println("[Servidor] ¡Todos listos! Iniciando juego en " + idSala);
                    iniciarJuego(idSala);
                }
            }
        }
    }

    private void iniciarJuego(String idSala) {
        SalaJuego sala = salas.get(idSala);
        if (sala == null) return;
        
        sala.setJuegoIniciado(true);
        
        List<Jugador> jugadores = new ArrayList<>();
        int id = 0;
        for (String nombre : sala.getNombresJugadores()) {
            jugadores.add(new Jugador(nombre, id++));
        }
        
        Set<ManejadorCliente> miembros = miembrosSalas.get(idSala);
        if (miembros != null) {
            MensajeJuego mensaje = MensajeJuego.juegoInicia(jugadores);
            for (ManejadorCliente cliente : miembros) {
                cliente.enviarMensaje(mensaje);
            }
        }
    }

    public void actualizarSalaParaMiembros(String idSala) {
        SalaJuego sala = salas.get(idSala);
        Set<ManejadorCliente> miembros = miembrosSalas.get(idSala);
        
        if (sala != null && miembros != null) {
            MensajeJuego mensaje = MensajeJuego.salaActualizada(sala);
            for (ManejadorCliente cliente : miembros) {
                cliente.enviarMensaje(mensaje);
            }
        }
    }

    public void difundirListaSalas() {
        List<SalaJuego> listaSalas = obtenerSalas();
        MensajeJuego mensaje = MensajeJuego.listaSalas(listaSalas);
        
        for (ManejadorCliente cliente : todosClientes.values()) {
            cliente.enviarMensaje(mensaje);
        }
    }

    public void difundirChatEnSala(String idSala, int idJugador, String nombreJugador, String mensajeTexto) {
        Set<ManejadorCliente> miembros = miembrosSalas.get(idSala);
        if (miembros != null) {
            MensajeJuego mensajeChat = MensajeJuego.chatDifusion(idJugador, nombreJugador, mensajeTexto);
            
            for (ManejadorCliente cliente : miembros) {
                cliente.enviarMensaje(mensajeChat);
            }
        }
    }

    public List<SalaJuego> obtenerSalas() {
        return new ArrayList<>(salas.values());
    }

    public SalaJuego obtenerSala(String idSala) {
        return salas.get(idSala);
    }

    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
     }
    
    
