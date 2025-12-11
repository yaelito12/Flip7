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
    
    public static void main(String[] args) { new ServidorJuego().iniciar(); }
}