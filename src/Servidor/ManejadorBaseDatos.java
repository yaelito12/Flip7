
package Servidor;

import flip7.comun.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gerth
 */
public class ManejadorBaseDatos {
     private static final String ARCHIVO_BD = "voltear7.db";
    private Connection conexion;
    
    public ManejadorBaseDatos() { inicializarBaseDatos(); }
    
    private void inicializarBaseDatos() {
        try {
            Class.forName("org.sqlite.JDBC");
            conexion = DriverManager.getConnection("jdbc:sqlite:" + ARCHIVO_BD);
            Statement stmt = conexion.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre_usuario TEXT UNIQUE NOT NULL, contrasena TEXT NOT NULL, partidas_jugadas INTEGER DEFAULT 0, partidas_ganadas INTEGER DEFAULT 0, puntaje_total INTEGER DEFAULT 0, creado_en DATETIME DEFAULT CURRENT_TIMESTAMP)");
            stmt.close();
            Statement contarStmt = conexion.createStatement();
            ResultSet rs = contarStmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            int cantidad = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            contarStmt.close();
            System.out.println("[BD] SQLite inicializada - " + cantidad + " usuarios");
        } catch (ClassNotFoundException e) {
            System.err.println("[BD] ERROR: Driver SQLite no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[BD] ERROR SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public synchronized Usuario registrar(String nombreUsuario, String contrasena) {
        if (conexion == null) return null;
        try {
            PreparedStatement verificarStmt = conexion.prepareStatement("SELECT id FROM usuarios WHERE LOWER(nombre_usuario) = LOWER(?)");
            verificarStmt.setString(1, nombreUsuario);
            ResultSet rs = verificarStmt.executeQuery();
            if (rs.next()) { rs.close(); verificarStmt.close(); return null; }
            rs.close();
            verificarStmt.close();
            PreparedStatement insertarStmt = conexion.prepareStatement("INSERT INTO usuarios (nombre_usuario, contrasena) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            insertarStmt.setString(1, nombreUsuario);
            insertarStmt.setString(2, contrasena);
            insertarStmt.executeUpdate();
            ResultSet claves = insertarStmt.getGeneratedKeys();
            int id = claves.next() ? claves.getInt(1) : -1;
            claves.close();
            insertarStmt.close();
            System.out.println("[BD] Registrado: " + nombreUsuario + " (ID: " + id + ")");
            Usuario usuario = new Usuario(nombreUsuario, contrasena);
            usuario.setId(id);
            return usuario;
        } catch (SQLException e) {
            System.err.println("[BD] Error registro: " + e.getMessage());
            return null;
        }
    }
    
    public synchronized Usuario login(String nombreUsuario, String contrasena) {
        if (conexion == null) return null;
        try {
            PreparedStatement stmt = conexion.prepareStatement("SELECT id, nombre_usuario, contrasena, partidas_jugadas, partidas_ganadas, puntaje_total FROM usuarios WHERE LOWER(nombre_usuario) = LOWER(?)");
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String contrasenaAlmacenada = rs.getString("contrasena");
                if (contrasenaAlmacenada.equals(contrasena)) {
                    Usuario usuario = new Usuario(rs.getInt("id"), rs.getString("nombre_usuario"), rs.getInt("partidas_jugadas"), rs.getInt("partidas_ganadas"), rs.getInt("puntaje_total"));
                    rs.close();
                    stmt.close();
                    System.out.println("[BD] Login: " + nombreUsuario);
                    return usuario;
                }
            }
            rs.close();
            stmt.close();
            return null;
        } catch (SQLException e) {
            System.err.println("[BD] Error login: " + e.getMessage());
            return null;
        }
    }
}
