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

    private int idJugador;
    private int idJugadorObjetivo;
    private int numeroRonda;
    private String nombreJugador;
    private String mensaje;
    private Carta carta;
    private List<Jugador> jugadores;
    private EstadoJuego estadoJuego;

    
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
}
