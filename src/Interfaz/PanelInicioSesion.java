package Interfaz;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class PanelInicioSesion extends JPanel {

    private JTextField campoUsuario, campoServidor, campoPuerto;
    private JPasswordField campoContrasena;
    private JButton botonEntrar, botonRegistrar;
    private EscuchaInicioSesion escucha;
    
    private static final Color AZUL_OSCURO = new Color(66, 133, 244);
    private static final Color AZUL_CLARO = new Color(135, 206, 250);
    private static final Color VERDE = new Color(74, 222, 128);
    
    public interface EscuchaInicioSesion {
        void alIniciarSesion(String usuario, String contrasena, String servidor, int puerto);
        void alRegistrar(String usuario, String contrasena, String servidor, int puerto);
    }
    
    public PanelInicioSesion(EscuchaInicioSesion escucha) {
        this.escucha = escucha;
        setLayout(new GridBagLayout());
        setOpaque(false);
        
        JPanel caja = crearCajaInicioSesion();
        add(caja);
    }
     private JPanel crearCajaInicioSesion() {
        JPanel caja = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(5, 5, getWidth() - 5, getHeight() - 5, 25, 25));
                
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 25, 25));
                
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(AZUL_OSCURO);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 7, getHeight() - 7, 24, 24));
            }
        };
        
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setOpaque(false);
        caja.setBorder(new EmptyBorder(40, 50, 40, 50));
        caja.setPreferredSize(new Dimension(420, 520));
        
        JLabel logo = new JLabel("FLIP 7");
        logo.setFont(new Font("Arial", Font.BOLD, 48));
        logo.setForeground(AZUL_OSCURO);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitulo = new JLabel("Inicia sesión o regístrate para jugar");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(100, 116, 139));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);
        panelCampos.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        campoUsuario = crearCampoTexto("Usuario");
        
        campoContrasena = new JPasswordField();
        campoContrasena.setFont(new Font("Arial", Font.PLAIN, 15));
        campoContrasena.setMaximumSize(new Dimension(300, 45));
        campoContrasena.setPreferredSize(new Dimension(300, 45));
        campoContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        campoServidor = crearCampoTexto("localhost");
        campoPuerto   = crearCampoTexto("5555");
        
        panelCampos.add(crearPanelCampo("Usuario:", campoUsuario));
        panelCampos.add(Box.createVerticalStrut(12));
        panelCampos.add(crearPanelCampo("Contraseña:", campoContrasena));
        panelCampos.add(Box.createVerticalStrut(12));
        panelCampos.add(crearPanelCampo("Servidor:", campoServidor));
        panelCampos.add(Box.createVerticalStrut(12));
        panelCampos.add(crearPanelCampo("Puerto:", campoPuerto));
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);
        panelBotones.setMaximumSize(new Dimension(320, 55));
        
        botonEntrar = crearBoton("ENTRAR", AZUL_OSCURO);
        botonRegistrar = crearBoton("REGISTRAR", VERDE);
        
        botonEntrar.addActionListener(e -> iniciarSesion());
        botonRegistrar.addActionListener(e -> registrar());
        
        campoContrasena.addActionListener(e -> iniciarSesion());
        
        panelBotones.add(botonEntrar);
        panelBotones.add(botonRegistrar);
        
        caja.add(logo);
        caja.add(Box.createVerticalStrut(5));
        caja.add(subtitulo);
        caja.add(Box.createVerticalStrut(30));
        caja.add(panelCampos);
        caja.add(Box.createVerticalStrut(25));
        caja.add(panelBotones);
        
        return caja;
    }
      private JTextField crearCampoTexto(String valor) {
        JTextField campo = new JTextField(valor);
        campo.setFont(new Font("Arial", Font.PLAIN, 15));
        campo.setMaximumSize(new Dimension(300, 45));
        campo.setPreferredSize(new Dimension(300, 45));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 2),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return campo;
    }
    
    private JPanel crearPanelCampo(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 70));
        
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 65, 85));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setPreferredSize(new Dimension(140, 45));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }
    