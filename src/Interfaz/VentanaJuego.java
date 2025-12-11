package Interfaz;

import gflip7.cliente.ClienteJuego;
import gflip7.comun.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class VentanaJuego extends JFrame implements ClienteJuego.EscuchaClienteJuego {

    private ClienteJuego cliente = new ClienteJuego();
    private int miIdJugador = -1;
    private boolean esMiTurno, juegoIniciado, esEspectador;

    private Map<Integer, PanelJugador> panelesJugadores = new HashMap<>();

    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;
    private PanelInicioSesion panelLogin;
    private PanelLobby panelLobby;
    private SalaDeEspera salaDeEspera;
    private PanelRankings panelRankings;
    private JPanel panelJuego;
    private boolean esHost = false;
    private JPanel panelJugadores;
    private PanelInfoJuego panelInfo;
    private JButton botonPedir, botonPlantarse, botonListo;
    private JLabel indicadorTurno;
    private JTextArea areaChat;
    private JTextField entradaChat;

    private static final Color AZUL_CLARO = new Color(135, 206, 250);
    private static final Color AZUL_OSCURO = new Color(66, 133, 244);
    private static final Color VERDE = new Color(74, 222, 128);
    private static final Color NARANJA = new Color(251, 146, 60);
    private static final Color ROJO = new Color(248, 113, 113);

    public VentanaJuego() {
        super("Flip 7 - Juego de Cartas");
        cliente.agregarEscucha(this);
        inicializarUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 850);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (cliente.estaConectado()) cliente.desconectar();
            }
        });
    }