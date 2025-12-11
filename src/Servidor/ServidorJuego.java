package flip7.servidor;

import flip7.comun.MensajeJuego;
import flip7.comun.Usuario;

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

    private ManejadorBaseDatos baseDatos;  

    public void iniciar() {
        try {
            baseDatos = new ManejadorBaseDatos();
            socketServidor = new ServerSocket(PUERTO);
            ejecutando = true;

            System.out.println("========================================");
            System.out.println("   SERVIDOR FLIP 7 - Puerto " + PUERTO);
            System.out.println("   Base de datos SQLite lista");
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


    public synchronized void manejarLogin(
            ManejadorCliente manejador,
            String nombreUsuario,
            String contrasena) {

        nombreUsuario = nombreUsuario.trim();

        if (!esUsuarioValido(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido(
                "Usuario inválido.\nSolo letras/números, sin espacios, 3-15 chars."
            ));
            return;
        }

        if (usuarioYaConectado(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.loginFallido(
                "Este usuario ya está conectado."
            ));
            return;
        }

        Usuario usuario = baseDatos.login(nombreUsuario, contrasena);

        if (usuario != null) {
            manejador.setNombreJugador(nombreUsuario);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.loginExitoso(usuario));

            System.out.println("[LOGIN] Usuario OK: " + nombreUsuario);
        } else {
            manejador.enviarMensaje(MensajeJuego.loginFallido(
                "Usuario o contraseña incorrectos."
            ));
        }
    }

    public void manejarRegistro(
            ManejadorCliente manejador,
            String nombreUsuario,
            String contrasena) {

        if (!esUsuarioValido(nombreUsuario)) {
            manejador.enviarMensaje(MensajeJuego.registroFallido(
                "Usuario inválido. Debe tener 3-15 caracteres sin espacios."
            ));
            return;
        }

        if (contrasena == null || contrasena.length() < 4) {
            manejador.enviarMensaje(MensajeJuego.registroFallido(
                "La contraseña debe tener mínimo 4 caracteres."
            ));
            return;
        }

        Usuario usuario = baseDatos.registrar(nombreUsuario.trim(), contrasena);

        if (usuario != null) {
            manejador.setNombreJugador(nombreUsuario);
            manejador.setIdUsuario(usuario.getId());
            manejador.enviarMensaje(MensajeJuego.registroExitoso(usuario));

            System.out.println("[REGISTRO] Nuevo usuario: " + nombreUsuario);
        } else {
            manejador.enviarMensaje(MensajeJuego.registroFallido(
                "Este nombre de usuario ya existe."
            ));
        }
    }

    private boolean esUsuarioValido(String nombre) {
        if (nombre == null) return false;
        if (nombre.contains(" ")) return false;
        return nombre.matches("^[a-zA-Z0-9]{3,15}$");
    }

    public boolean usuarioYaConectado(String nombre) {
        for (ManejadorCliente c : todosClientes.values()) {
            if (nombre.equalsIgnoreCase(c.getNombreJugador())) return true;
        }
        return false;
    }

    public void registrarCliente(ManejadorCliente manejador) {
        todosClientes.put(manejador.obtenerIdCliente(), manejador);
    }

    public void desregistrarCliente(int idCliente) {
        todosClientes.remove(idCliente);
        System.out.println("- Cliente desconectado: " + idCliente);
    }

    public ManejadorBaseDatos getBaseDatos() { return baseDatos; }

    public static void main(String[] args) {
        new ServidorJuego().iniciar();
    }
}
