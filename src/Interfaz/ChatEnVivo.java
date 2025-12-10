package Interfaz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class    ChatEnVivo extends JDialog {
    private JTextField campoHost, campoPuerto, campoNombre;
    private String host;
    private int puerto;
    private String nombreJugador;
    private boolean confirmado;

    public ChatEnVivo(JFrame parent) {
        super(parent, "Conectar", true);
        setUndecorated(true);
        setSize(360, 300);
        setLocationRelativeTo(parent);

        JPanel principal = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(32,40,55), 0, getHeight(), new Color(22,28,40));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-4, getHeight()-4, 18, 18));
                g2.setStroke(new BasicStroke(2));
                g2.setColor(new Color(55,70,95));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-6, getHeight()-6, 17, 17));
            }
        };

        principal.setOpaque(false);
        principal.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setOpaque(false);

        JLabel logo = new JLabel("FLIP 7");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logo.setForeground(new Color(255,200,60));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Juego de Cartas");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(130,140,160));
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        panelTitulo.add(logo, BorderLayout.CENTER);
        panelTitulo.add(sub, BorderLayout.SOUTH);