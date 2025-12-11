
package servidor;

import Servidor.ManejadorBaseDatos;
import gflip7.comun.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ManejadorCliente implements Runnable {

    private Socket socket;
    private ServidorJuego servidor;
    private int idCliente;
    private boolean ejecutando;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Usuario usuarioActual;
    private ManejadorBaseDatos baseDatos;
    private String salaActual;

    public ManejadorCliente(Socket socket, ServidorJuego servidor, int idCliente) {
        this.socket = socket;
        this.servidor = servidor;
        this.idCliente = idCliente;
        this.ejecutando = true;
        this.baseDatos = new ManejadorBaseDatos();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            servidor.registrarCliente(this);
            System.out.println("[Cliente " + idCliente + "] Conectado");

            while (ejecutando && !socket.isClosed()) {
                try {
                    MensajeJuego mensaje = (MensajeJuego) in.readObject();
                    procesarMensaje(mensaje);
                } catch (EOFException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("[Cliente " + idCliente + "] Clase no encontrada: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[Cliente " + idCliente + "] Error IO: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    private void procesarMensaje(MensajeJuego mensaje) {
        try {
            switch (mensaje.getTipo()) {
                case LOGIN:
                    procesarLogin(mensaje);
                    break;
                    
                case REGISTRO:
                    procesarRegistro(mensaje);
                    break;
                    
                case OBTENER_SALAS:
                    procesarObtenerSalas();
                    break;
                    
                case CREAR_SALA:
                    procesarCrearSala(mensaje);
                    break;
                    
                case UNIRSE_SALA:
                    procesarUnirseSala(mensaje);
                    break;
                    
                case SALIR_SALA:
                    procesarSalirSala();
                    break;
                    
                case OBTENER_RANKINGS:
                    procesarObtenerRankings();
                    break;
                    
                case LISTO:
                    procesarListo();
                    break;
                    
                case PEDIR:
                    procesarPedir();
                    break;
                    
                case PLANTARSE:
                    procesarPlantarse();
                    break;
                    
                case MENSAJE_CHAT:
                    procesarChat(mensaje);
                    break;
                    
                case ASIGNAR_CARTA_ACCION:
                    procesarAsignarCartaAccion(mensaje);
                    break;
                    
                default:
                    System.out.println("[Cliente " + idCliente + "] Mensaje no manejado: " + mensaje.getTipo());
            }
        } catch (Exception e) {
            System.err.println("[Cliente " + idCliente + "] Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarLogin(MensajeJuego mensaje) {
        String usuario = mensaje.getNombreUsuario();
        String contrasena = mensaje.getContrasena();
        
        Usuario usuarioEncontrado = baseDatos.login(usuario, contrasena);
        
        if (usuarioEncontrado != null) {
            usuarioActual = usuarioEncontrado;
            enviarMensaje(MensajeJuego.loginExitoso(usuarioEncontrado));
            System.out.println("[Cliente " + idCliente + "] Login exitoso: " + usuario);
        } else {
            enviarMensaje(MensajeJuego.loginFallido("Usuario o contraseña incorrectos"));
            System.out.println("[Cliente " + idCliente + "] Login fallido: " + usuario);
        }
    }

    private void procesarRegistro(MensajeJuego mensaje) {
        String usuario = mensaje.getNombreUsuario();
        String contrasena = mensaje.getContrasena();
        
        if (usuario.length() < 3) {
            enviarMensaje(MensajeJuego.registroFallido("El usuario debe tener al menos 3 caracteres"));
            return;
        }
        
        if (contrasena.length() < 4) {
            enviarMensaje(MensajeJuego.registroFallido("La contraseña debe tener al menos 4 caracteres"));
            return;
        }
        
        Usuario nuevoUsuario = baseDatos.registrar(usuario, contrasena);
        
        if (nuevoUsuario != null) {
            usuarioActual = nuevoUsuario;
            enviarMensaje(MensajeJuego.registroExitoso(nuevoUsuario));
            System.out.println("[Cliente " + idCliente + "] Registro exitoso: " + usuario);
        } else {
            enviarMensaje(MensajeJuego.registroFallido("El usuario ya existe"));
            System.out.println("[Cliente " + idCliente + "] Registro fallido: " + usuario);
        }
    }

    private void procesarObtenerSalas() {
        List<SalaJuego> salas = servidor.obtenerSalas();
        enviarMensaje(MensajeJuego.listaSalas(salas));
        System.out.println("[Cliente " + idCliente + "] Enviadas " + salas.size() + " salas");
    }

    private void procesarCrearSala(MensajeJuego mensaje) {
        if (usuarioActual == null) {
            enviarMensaje(MensajeJuego.errorSala("Debes iniciar sesión primero"));
            return;
        }
        
        String nombreSala = mensaje.getNombreSala();
        int maxJugadores = mensaje.getMaxJugadores();
        
        if (nombreSala == null || nombreSala.trim().isEmpty()) {
            enviarMensaje(MensajeJuego.errorSala("El nombre de la sala no puede estar vacío"));
            return;
        }
        
        if (maxJugadores < 2 || maxJugadores > 6) {
            enviarMensaje(MensajeJuego.errorSala("El número de jugadores debe estar entre 2 y 6"));
            return;
        }
        
        SalaJuego nuevaSala = servidor.crearSala(nombreSala, usuarioActual.getNombreUsuario(), 
                                                  usuarioActual.getId(), maxJugadores, this);
        
        if (nuevaSala != null) {
            salaActual = nuevaSala.getIdSala();
            enviarMensaje(MensajeJuego.salaCreada(nuevaSala, usuarioActual.getId()));
            System.out.println("[Cliente " + idCliente + "] Sala creada: " + nombreSala);
            
            servidor.difundirListaSalas();
        } else {
            enviarMensaje(MensajeJuego.errorSala("No se pudo crear la sala"));
        }
    }

    private void procesarUnirseSala(MensajeJuego mensaje) {
        if (usuarioActual == null) {
            enviarMensaje(MensajeJuego.errorSala("Debes iniciar sesión primero"));
            return;
        }
        
        String idSala = mensaje.getIdSala();
        boolean esEspectador = mensaje.isEsEspectador();
        
        String resultado = servidor.unirseASala(idSala, usuarioActual.getNombreUsuario(), 
                                                usuarioActual.getId(), esEspectador, this);
        
        if (resultado == null) {
            salaActual = idSala;
            SalaJuego sala = servidor.obtenerSala(idSala);
            if (sala != null) {
                enviarMensaje(MensajeJuego.salaUnida(sala, esEspectador ? -1 : usuarioActual.getId()));
                System.out.println("[Cliente " + idCliente + "] Unido a sala: " + idSala + 
                                 (esEspectador ? " (espectador)" : ""));
                
                servidor.actualizarSalaParaMiembros(idSala);
            }
        } else {
            enviarMensaje(MensajeJuego.errorSala(resultado));
        }
    }

    private void procesarSalirSala() {
        if (salaActual != null) {
            servidor.salirDeSala(salaActual, usuarioActual.getNombreUsuario(), this);
            salaActual = null;
            System.out.println("[Cliente " + idCliente + "] Salió de la sala");
        }
    }

    private void procesarObtenerRankings() {
        List<Usuario> rankings = baseDatos.obtenerRankings(20);
        MensajeJuego respuesta = MensajeJuego.respuestaRankings(rankings);
        enviarMensaje(respuesta);
        System.out.println("[Cliente " + idCliente + "] Rankings enviados: " + rankings.size());
    }

    private void procesarListo() {
        if (salaActual != null) {
            servidor.marcarJugadorListo(salaActual, usuarioActual.getNombreUsuario());
            System.out.println("[Cliente " + idCliente + "] Marcado como listo");
        }
    }

    private void procesarPedir() {
        System.out.println("[Cliente " + idCliente + "] Pedir carta");
    }

    private void procesarPlantarse() {
        System.out.println("[Cliente " + idCliente + "] Plantarse");
    }

    private void procesarChat(MensajeJuego mensaje) {
        if (salaActual != null) {
            servidor.difundirChatEnSala(salaActual, usuarioActual.getId(), 
                                       usuarioActual.getNombreUsuario(), mensaje.getMensaje());
        }
    }

    private void procesarAsignarCartaAccion(MensajeJuego mensaje) {
        System.out.println("[Cliente " + idCliente + "] Asignar carta acción");
    }

    public synchronized void enviarMensaje(MensajeJuego mensaje) {
        try {
            if (out != null) {
                out.writeObject(mensaje);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[Cliente " + idCliente + "] Error enviando mensaje: " + e.getMessage());
            desconectar();
        }
    }

    public void desconectar() {
        ejecutando = false;
        
        if (salaActual != null) {
            servidor.salirDeSala(salaActual, usuarioActual != null ? usuarioActual.getNombreUsuario() : null, this);
        }
        
        servidor.desregistrarCliente(idCliente);

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {}
        
        System.out.println("[Cliente " + idCliente + "] Desconectado");
    }

    public int obtenerIdCliente() {
        return idCliente;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public String getSalaActual() {
        return salaActual;
    }
    }

