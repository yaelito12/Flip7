package flip7.servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServidorJuego {
    private static final int PUERTO = 5555;
    private ServerSocket socketServidor;
    private ExecutorService ejecutor = Executors.newCachedThreadPool();
    private boolean ejecutando;
    
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
                   
                } catch (IOException e) {
                    if (ejecutando) e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
}