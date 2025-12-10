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
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(15, 0, 15, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        campoHost = crearCampo("localhost");
        campoPuerto = crearCampo("5555");
        campoNombre = crearCampo("Jugador");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        campos.add(crearEtiqueta("Servidor"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        campos.add(campoHost, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        campos.add(crearEtiqueta("Puerto"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        campos.add(campoPuerto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        campos.add(crearEtiqueta("Tu nombre"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        campos.add(campoNombre, gbc);

        JPanel botones = new JPanel(new GridLayout(1, 2, 12, 0));
        botones.setOpaque(false);

        JButton botonCancelar = crearBoton("CANCELAR", new Color(180,70,70));
        JButton botonConectar = crearBoton("CONECTAR", new Color(70,160,90));

        botonCancelar.addActionListener(e -> { confirmado = false; dispose(); });
        botonConectar.addActionListener(e -> conectar());
        campoNombre.addActionListener(e -> conectar());

        botones.add(botonCancelar);
        botones.add(botonConectar);

        principal.add(panelTitulo, BorderLayout.NORTH);
        principal.add(campos, BorderLayout.CENTER);
        principal.add(botones, BorderLayout.SOUTH);

        setContentPane(principal);

        final Point[] arrastre = {null};
        principal.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                arrastre[0] = e.getPoint();
            }
        });

        principal.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - arrastre[0].x, p.y + e.getY() - arrastre[0].y);
            }
        });
    }