package cliente;

import java.io.*;
import java.net.Socket;

public class JuegoCliente {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            return true;
        } catch (IOException e) {
            connected = false;
            return false;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {}
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }
}