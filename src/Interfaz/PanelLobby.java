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