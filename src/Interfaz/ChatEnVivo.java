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
    private JLabel crearEtiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(140,150,170));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setBorder(new EmptyBorder(0,0,0,12));
        return l;
    }

    private JTextField crearCampo(String t) {
        JTextField f = new JTextField(t);
        f.setBackground(new Color(45,55,70));
        f.setForeground(Color.WHITE);
        f.setCaretColor(new Color(100,170,255));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60,75,95)),
                new EmptyBorder(8,10,8,10)
        ));
        f.setPreferredSize(new Dimension(180,36));
        return f;
    }

    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t) {
            boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }

            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hover ? aclarar(c, 1.15f) : c;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }

            private Color aclarar(Color col, float f) {
                return new Color(
                        Math.min(255, (int)(col.getRed()*f)),
                        Math.min(255, (int)(col.getGreen()*f)),
                        Math.min(255, (int)(col.getBlue()*f))
                );
            }
        };

        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(130,40));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void conectar() {
        String nom = campoNombre.getText().trim();
        if (nom.isEmpty()) {
            campoNombre.setBackground(new Color(80,50,50));
            return;
        }

        try {
            puerto = Integer.parseInt(campoPuerto.getText().trim());
        } catch (Exception e) {
            campoPuerto.setBackground(new Color(80,50,50));
            return;
        }

        host = campoHost.getText().trim();
        if (host.isEmpty()) host = "localhost";

        nombreJugador = nom;
        confirmado = true;
        dispose();
    }

    public boolean mostrarDialogo() {
        setVisible(true);
        return confirmado;
    }

    public String getHost() { return host; }
    public int getPort() { return puerto; }
    public String getPlayerName() { return nombreJugador; }
}
