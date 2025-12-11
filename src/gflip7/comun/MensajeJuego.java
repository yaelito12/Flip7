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

    public static MensajeJuego crearSala(String nombreSala, String nombreJugador, int maxJugadores) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.CREAR_SALA);
        m.nombreSala = nombreSala;
        m.nombreJugador = nombreJugador;
        m.maxJugadores = maxJugadores;
        return m;
    }

    public static MensajeJuego unirseSala(String idSala, String nombreJugador) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.UNIRSE_SALA);
        m.idSala = idSala;
        m.nombreJugador = nombreJugador;
        m.esEspectador = false;
        return m;
    }

    public static MensajeJuego unirseSalaComoEspectador(String idSala, String nombreJugador) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.UNIRSE_SALA);
        m.idSala = idSala;
        m.nombreJugador = nombreJugador;
        m.esEspectador = true;
        return m;
    }

    public static MensajeJuego salirSala() {
        return new MensajeJuego(TipoMensaje.SALIR_SALA);
    }

    public static MensajeJuego solicitarSalas() {
        return new MensajeJuego(TipoMensaje.OBTENER_SALAS);
    }

    public static MensajeJuego listaSalas(List<SalaJuego> listaSalas) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.LISTA_SALAS);
        m.salas = listaSalas;
        return m;
    }

    public static MensajeJuego salaCreada(SalaJuego sala, int idJugador) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.SALA_CREADA);
        m.sala = sala;
        m.idJugador = idJugador;
        return m;
    }

    public static MensajeJuego salaUnida(SalaJuego sala, int idJugador) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.SALA_UNIDA);
        m.sala = sala;
        m.idJugador = idJugador;
        return m;
    }

    public static MensajeJuego salaActualizada(SalaJuego sala) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.SALA_ACTUALIZADA);
        m.sala = sala;
        return m;
    }

    public static MensajeJuego errorSala(String error) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.ERROR_SALA);
        m.mensaje = error;
        return m;
    }

    public static MensajeJuego jugadorUnido(int id, String nombre) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.JUGADOR_UNIDO);
        m.idJugador = id;
        m.nombreJugador = nombre;
        return m;
    }

    public static MensajeJuego juegoInicia(List<Jugador> jugadores) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.JUEGO_INICIA);
        m.jugadores = jugadores;
        return m;
    }

    public static MensajeJuego tuTurno(int id) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.TU_TURNO);
        m.idJugador = id;
        return m;
    }

    public static MensajeJuego cartaRepartida(int id, Carta carta) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.CARTA_REPARTIDA);
        m.idJugador = id;
        m.carta = carta;
        return m;
    }

    public static MensajeJuego jugadorEliminado(int id, Carta carta) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.JUGADOR_ELIMINADO);
        m.idJugador = id;
        m.carta = carta;
        return m;
    }

    public static MensajeJuego elegirObjetivoAccion(Carta carta, List<Jugador> activos) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.ELEGIR_OBJETIVO_ACCION);
        m.carta = carta;
        m.jugadores = activos;
        return m;
    }

    public static MensajeJuego finRonda(List<Jugador> jugadores, int numeroRonda) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.FIN_RONDA);
        m.jugadores = jugadores;
        m.numeroRonda = numeroRonda;
        return m;
    }

    public static MensajeJuego finJuego(List<Jugador> jugadores, int idGanador) {
        MensajeJuego m = new MensajeJuego(TipoMensaje.FIN_JUEGO);
        m.jugadores = jugadores;
        m.idJugador = idGanador;
        return m;
    }
    public TipoMensaje getTipo() { 
    return tipo; 
}

public int getIdJugador() { 
    return idJugador; 
}

public void setIdJugador(int id) { 
    this.idJugador = id; 
}

public int getIdJugadorObjetivo() { 
    return idJugadorObjetivo; 
}

public int getNumeroRonda() { 
    return numeroRonda; 
}

public String getNombreJugador() { 
    return nombreJugador; 
}

public String getMensaje() { 
    return mensaje; 
}

public Carta getCarta() { 
    return carta; 
}

public List<Jugador> getJugadores() { 
    return jugadores; 
}

public EstadoJuego getEstadoJuego() { 
    return estadoJuego; 
}

public String getIdSala() { 
    return idSala; 
}

public String getNombreSala() { 
    return nombreSala; 
}

public int getMaxJugadores() { 
    return maxJugadores; 
}

public boolean isEsEspectador() { 
    return esEspectador; 
}

public List<SalaJuego> getSalas() { 
    return salas; 
}

public SalaJuego getSala() { 
    return sala; 
}

public Usuario getUsuario() { 
    return usuario; 
}

public List<Usuario> getRankings() { 
    return rankings; 
}

public List<String> getJugadoresListos() { 
    return jugadoresListos; 
}

public String getNombreUsuario() { 
    return nombreUsuario; 
}

public String getContrasena() { 
    return contrasena; 
}
}
