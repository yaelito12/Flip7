package servidor;

import java.io.IOException;
import java.net.Socket;

public class ManejadorCliente implements Runnable {

    private Socket socket;
    private ServidorJuego servidor;
    private int idCliente;
    private boolean ejecutando;


    public ManejadorCliente(Socket socket, ServidorJuego servidor, int idCliente) {
        this.socket = socket;
        this.servidor = servidor;
        this.idCliente = idCliente;
        this.ejecutando = true;
    }

    @Override
    public void run() {
        try {
            servidor.registrarCliente(this);

            while (ejecutando && !socket.isClosed()) {
            }

        } catch (Exception e) {
            desconectar();
        }
    }

    public void desconectar() {
        ejecutando = false;
        servidor.desregistrarCliente(idCliente);

        try {
            socket.close();
        } catch (IOException e) {}
    }

    public int obtenerIdCliente() {
        return idCliente;
    }
}