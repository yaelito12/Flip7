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
    private void inicializarUI() {

        cardLayout = new CardLayout();

        contenedorPrincipal = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint bg = new GradientPaint(
                        0, 0, AZUL_CLARO,
                        0, getHeight(), new Color(248, 250, 252));
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.setColor(Color.WHITE);
                g2.fillOval(-50, -80, 300, 200);
                g2.fillOval(150, -50, 250, 180);
                g2.fillOval(getWidth() - 250, -60, 350, 220);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };

        panelLogin = new PanelInicioSesion(new PanelInicioSesion.EscuchaInicioSesion() {

            @Override
            public void alIniciarSesion(String usuario, String contrasena, String host, int puerto) {
                new Thread(() -> {
                    if (!cliente.estaConectado()) {
                        if (!cliente.conectar(host, puerto, usuario)) {
                            panelLogin.alFallarConexion();
                            return;
                        }
                    }
                    cliente.login(usuario, contrasena);
                }).start();
            }

            @Override
            public void alRegistrar(String usuario, String contrasena, String host, int puerto) {
                new Thread(() -> {
                    if (!cliente.estaConectado()) {
                        if (!cliente.conectar(host, puerto, usuario)) {
                            panelLogin.alFallarConexion();
                            return;
                        }
                    }
                    cliente.registrar(usuario, contrasena);
                }).start();
            }
        });
