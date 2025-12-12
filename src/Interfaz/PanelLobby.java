package Interfaz;

import gflip7.comun.SalaJuego;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class PanelLobby extends JPanel {
    private JTable tablaSalas;
    private DefaultTableModel modeloTabla;
    private JButton btnCrear, btnUnirse, btnActualizar;
    private JTextField campoNombre;
    private LobbyListener listener;

    private static final Color AZUL_OSCURO = new Color(66, 133, 244);
    private static final Color AZUL_CLARO = new Color(135, 206, 250);
    private static final Color VERDE = new Color(74, 222, 128);

    public interface LobbyListener {
        void onCrearSala(String nombreSala, String nombreJugador, int maxJugadores);
        void onUnirseSala(String idSala, String nombreJugador, boolean comoEspectador);
        void onActualizar();
        void onSalir();
        void onVerRankings();
    }

    public PanelLobby(LobbyListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(30, 50, 30, 50));

        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelListaSalas(), BorderLayout.CENTER);
        add(crearPanelAcciones(), BorderLayout.SOUTH);
    }
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("SALAS DISPONIBLES") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(AZUL_OSCURO);
                g2.drawString(getText(), 0, getHeight() - 5);
            }
        };
        titulo.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelNombre.setOpaque(false);

        JLabel lblNombre = new JLabel("Tu nombre:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNombre.setForeground(new Color(51, 65, 85));

        campoNombre = new JTextField(15);
        campoNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        campoNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 2),
                new EmptyBorder(8, 12, 8, 12)
        ));
        campoNombre.setText("Jugador");
        campoNombre.setEditable(false);
        campoNombre.setFocusable(false);
        campoNombre.setBackground(Color.WHITE);

        panelNombre.add(lblNombre);
        panelNombre.add(campoNombre);

        header.add(titulo, BorderLayout.WEST);
        header.add(panelNombre, BorderLayout.EAST);

        return header;
    }
    
    private JPanel crearPanelListaSalas() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(100, 150, 200, 30));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth(), getHeight(), 16, 16));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(100, 180, 246));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 5, getHeight() - 5, 15, 15));
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Nombre de Sala", "Host", "Jugadores", "Estado", "ID"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaSalas = new JTable(modeloTabla);
        tablaSalas.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaSalas.setRowHeight(40);
        tablaSalas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSalas.setShowGrid(false);
        tablaSalas.setIntercellSpacing(new Dimension(0, 5));

        tablaSalas.getColumnModel().getColumn(4).setMinWidth(0);
        tablaSalas.getColumnModel().getColumn(4).setMaxWidth(0);

        JTableHeader encabezado = tablaSalas.getTableHeader();
        encabezado.setFont(new Font("Arial", Font.BOLD, 14));
        encabezado.setBackground(AZUL_CLARO);
        encabezado.setForeground(new Color(30, 41, 59));
        encabezado.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(tabla, valor, seleccionado, foco, fila, columna);

                if (seleccionado) {
                    c.setBackground(new Color(219, 234, 254));
                    c.setForeground(AZUL_OSCURO);
                } else {
                    c.setBackground(fila % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(new Color(51, 65, 85));
                }

                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        render.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tablaSalas.getColumnCount(); i++) {
            tablaSalas.getColumnModel().getColumn(i).setCellRenderer(render);
        }

        tablaSalas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    unirseSalaSeleccionada(false);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaSalas);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);

        btnActualizar = crearBoton("<<", new Color(100, 116, 139));
        btnCrear = crearBoton("CREAR SALA", VERDE);
        btnUnirse = crearBoton("UNIRSE", AZUL_OSCURO);
        JButton btnEspectar = crearBoton("OBSERVAR", new Color(139, 92, 246));
        JButton btnRankings = crearBoton("VER RANKINGS", new Color(139, 92, 246));

        btnActualizar.addActionListener(e -> {
            if (listener != null) listener.onSalir();
        });

        btnCrear.addActionListener(e -> mostrarDialogoCrearSala());
        btnUnirse.addActionListener(e -> unirseSalaSeleccionada(false));
        btnEspectar.addActionListener(e -> unirseSalaSeleccionada(true));
        btnRankings.addActionListener(e -> {
            if (listener != null) listener.onVerRankings();
        });

        panel.add(btnActualizar);
        panel.add(btnCrear);
        panel.add(btnUnirse);
        panel.add(btnEspectar);
        panel.add(btnRankings);

        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                Color c = hover ? aclarar(color, 1.1f) : color;

                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(3, 3, w - 3, h - 3, 14, 14));

                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 14, 14));

                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 80), 0, h / 2, new Color(255, 255, 255, 0)));
                g2.fill(new RoundRectangle2D.Float(2, 2, w - 7, h / 2 - 2, 12, 12));

                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
            }

            private Color aclarar(Color c, float f) {
                return new Color(
                        Math.min(255, (int) (c.getRed() * f)),
                        Math.min(255, (int) (c.getGreen() * f)),
                        Math.min(255, (int) (c.getBlue() * f))
                );
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(160, 50));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
    
    private void mostrarDialogoCrearSala() {
        String nombreJugador = campoNombre.getText().trim();
        if (nombreJugador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa tu nombre primero", "Error", JOptionPane.WARNING_MESSAGE);
            campoNombre.requestFocus();
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField campoNombreSala = new JTextField("Sala de " + nombreJugador);
        JSpinner spinnerMax = new JSpinner(new SpinnerNumberModel(4, 2, 6, 1));

        panel.add(new JLabel("Nombre de la sala:"));
        panel.add(campoNombreSala);
        panel.add(new JLabel("Máximo de jugadores:"));
        panel.add(spinnerMax);

        int r = JOptionPane.showConfirmDialog(this, panel, "Crear Nueva Sala", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (r == JOptionPane.OK_OPTION) {
            String nombreSala = campoNombreSala.getText().trim();
            int maxJugadores = (Integer) spinnerMax.getValue();

            if (nombreSala.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (listener != null) {
                listener.onCrearSala(nombreSala, nombreJugador, maxJugadores);
            }
        }
    }

    private void unirseSalaSeleccionada(boolean comoEspectador) {
        int fila = tablaSalas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sala", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nombreJugador = campoNombre.getText().trim();
        if (nombreJugador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa tu nombre primero", "Error", JOptionPane.WARNING_MESSAGE);
            campoNombre.requestFocus();
            return;
        }

        String idSala = (String) modeloTabla.getValueAt(fila, 4);

        if (listener != null) {
            listener.onUnirseSala(idSala, nombreJugador, comoEspectador);
        }
    }
      public void updateRoomList(List<SalaJuego> salas) {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);

            if (salas == null || salas.isEmpty())
                return;

            for (SalaJuego sala : salas) {

                String estado;
                if (sala.isJuegoIniciado()) estado = "En juego";
                else if (sala.estaLlena()) estado = "Llena";
                else estado = "Esperando";

                String jugadores = sala.getJugadoresActuales() + "/" + sala.getMaxJugadores();

                if (sala.getCantidadEspectadores() > 0)
                    jugadores += " +" + sala.getCantidadEspectadores() + " obs";

                modeloTabla.addRow(new Object[]{
                        sala.getNombreSala(),
                        sala.getNombreHost(),
                        jugadores,
                        estado,
                        sala.getIdSala()
                });
            }
        });
    }

    public String getPlayerName() {
        return campoNombre.getText().trim();
    }

    public void setPlayerName(String nombre) {
        campoNombre.setText(nombre);
    }
}
