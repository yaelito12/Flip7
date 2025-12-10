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