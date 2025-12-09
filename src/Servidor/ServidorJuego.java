package flip7.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

public class ServidorJuego {

    private static final int PUERTO = 5555;
    private ServerSocket socketServidor;
    private Map<Integer, ManejadorCliente> todosClientes = new ConcurrentHashMap<>();
    private ExecutorService ejecutor = Executors.newCachedThreadPool();
    private boolean ejecutando;
    private int siguienteIdCliente = 0;

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
                    System.out.println("+ Conexion: " + cliente.getInetAddress());

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
    }

    public void desregistrarCliente(int idCliente) {
        todosClientes.remove(idCliente);
        System.out.println("- Cliente desconectado: " + idCliente);
    }

    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
}