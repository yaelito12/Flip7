package flip7.comun;

import gflip7.comun.SalaJuego;
import java.io.Serializable;
import java.util.List;

public class MensajeJuego implements Serializable {
    private static final long serialVersionUID = 1L;

    private TipoMensaje tipo;

    public MensajeJuego(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    public enum TipoMensaje { 
        LOGIN, 
        REGISTRO, 
        LOGIN_EXITOSO, 
        LOGIN_FALLIDO, 
        REGISTRO_EXITOSO, 
        REGISTRO_FALLIDO,

        CONECTAR, 
        DESCONECTAR, 
        CONECTADO,

        CREAR_SALA, 
        UNIRSE_SALA, 
        SALIR_SALA, 
        OBTENER_SALAS, 
        LISTA_SALAS, 
        SALA_CREADA, 
        SALA_UNIDA, 
        SALA_ACTUALIZADA, 
        SALA_SALIDA, 
        ERROR_SALA,

        JUGADOR_UNIDO, 
        JUGADOR_SALIO,
        LISTO,
        JUGADORES_LISTOS,

        JUEGO_INICIA, 
        RONDA_INICIA, 
        TU_TURNO, 
        FIN_RONDA, 
        FIN_JUEGO,

        PEDIR, 
        PLANTARSE, 
        ASIGNAR_CARTA_ACCION,

        CARTA_REPARTIDA, 
        JUGADOR_ELIMINADO, 
        JUGADOR_PLANTADO, 
        JUGADOR_CONGELADO, 
        CARTA_ACCION_ROBADA, 
        ELEGIR_OBJETIVO_ACCION,

        ESTADO_JUEGO, 
        MENSAJE_CHAT, 
        CHAT_DIFUSION,
        ERROR,

        OBTENER_RANKINGS,
        RESPUESTA_RANKINGS
    }

    // Campos de jugador y estado
    private int idJugador;
    private int idJugadorObjetivo;
    private int numeroRonda;
    private String nombreJugador;
    private String mensaje;
    private Carta carta;
    private List<Jugador> jugadores;
    private EstadoJuego estadoJuego;

    // Campos de salas, rankings y autenticación
    private String idSala;
    private String nombreSala;
    private int maxJugadores;
    private boolean esEspectador;
    private List<SalaJuego> salas;
    private SalaJuego sala;
    private Usuario usuario;
    private List<Usuario> rankings;
    private List<String> jugadoresListos;
    private String nombreUsuario;
    private String contrasena;

    public static MensajeJuego login(String nombreUsuario, String contrasena) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.LOGIN);
        m.nombreUsuario = nombreUsuario;
        m.contrasena = contrasena;
        return m;
    }

    public static MensajeJuego registro(String nombreUsuario, String contrasena) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.REGISTRO);
        m.nombreUsuario = nombreUsuario;
        m.contrasena = contrasena;
        return m;
    }

    public static MensajeJuego loginExitoso(Usuario usuario) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.LOGIN_EXITOSO);
        m.usuario = usuario;
        m.nombreJugador = usuario.getNombreUsuario();
        return m;
    }

    public static MensajeJuego loginFallido(String razon) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.LOGIN_FALLIDO);
        m.mensaje = razon;
        return m;
    }

    public static MensajeJuego registroExitoso(Usuario usuario) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.REGISTRO_EXITOSO);
        m.usuario = usuario;
        m.nombreJugador = usuario.getNombreUsuario();
        return m;
    }

    public static MensajeJuego registroFallido(String razon) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.REGISTRO_FALLIDO);
        m.mensaje = razon;
        return m;
    }

    public static MensajeJuego conectar(String nombre) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.CONECTAR);
        m.nombreJugador = nombre;
        return m;
    }

    public static MensajeJuego conectado(int id, String nombre) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.CONECTADO);
        m.idJugador = id;
        m.nombreJugador = nombre;
        return m;
    }
}
