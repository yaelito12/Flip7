package flip7.comun;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private int partidasJugadas;
    private int partidasGanadas;
    private int puntajeTotal;
    
    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.puntajeTotal = 0;
    }
}