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
        
        panelLobby = new PanelLobby(new PanelLobby.LobbyListener() {

            @Override
            public void onSalir() {
                if (cliente.estaConectado()) cliente.desconectar();
                esHost = false;
                panelLogin.limpiarCampos();
                mostrarPanel("login");
            }

            @Override
            public void onCrearSala(String nombreSala, String nombreJugador, int maxJugadores) {
                cliente.crearSala(nombreSala, maxJugadores);
            }

            @Override
            public void onUnirseSala(String idSala, String nombreJugador, boolean comoEspectador) {
                esEspectador = comoEspectador;
                if (comoEspectador) cliente.unirseSalaComoEspectador(idSala);
                else cliente.unirseSala(idSala);
            }

            @Override
            public void onActualizar() {
                cliente.solicitarSalas();
            }

            @Override
            public void onVerRankings() {
                cliente.solicitarRankings();
                mostrarPanel("rankings");
            }
        });

        panelRankings = new PanelRankings(new PanelRankings.EscuchaRankings() {
            @Override
            public void alVolver() {
                mostrarPanel("lobby");
            }

            @Override
            public void alActualizar() {
                cliente.solicitarRankings();
            }
        });

        salaDeEspera = new SalaDeEspera(new SalaDeEspera.WaitingRoomListener() {

            @Override
            public void onReady() {
                cliente.listo();
            }

            @Override
            public void onLeaveRoom() {
                if (esHost) {
                    int confirm = JOptionPane.showConfirmDialog(
                            VentanaJuego.this,
                            "Eres el HOST. Si sales, la sala se cerrará.",
                            "Advertencia",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        cliente.salirSala();
                        esHost = false;
                        mostrarPanel("lobby");
                    }
                } else {
                    cliente.salirSala();
                    mostrarPanel("lobby");
                }
            }

            @Override
            public void onJoinAsPlayer() {
                String id = cliente.getIdSalaActual();
                if (id != null) {
                    cliente.salirSala();
                    esEspectador = false;
                    esHost = false;
                    cliente.unirseSala(id);
                }
            }
        });

        panelJuego = crearPanelJuego();

        contenedorPrincipal.add(panelLogin, "login");
        contenedorPrincipal.add(panelLobby, "lobby");
        contenedorPrincipal.add(panelRankings, "rankings");
        contenedorPrincipal.add(salaDeEspera, "waiting");
        contenedorPrincipal.add(panelJuego, "game");

        setContentPane(contenedorPrincipal);
        mostrarPanel("login");
    }

    private void mostrarPanel(String nombre) {
        cardLayout.show(contenedorPrincipal, nombre);
    }
     private JPanel crearPanelJuego() {
        JPanel principal = new JPanel(new BorderLayout(0, 0));
        principal.setOpaque(false);

        JPanel cabecera = crearCabeceraJuego();

        JPanel areaCentral = new JPanel(new BorderLayout(15, 0));
        areaCentral.setOpaque(false);
        areaCentral.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel panelMesa = crearPanelMesa();
        JPanel panelDerecho = crearPanelDerecho();

        areaCentral.add(panelMesa, BorderLayout.CENTER);
        areaCentral.add(panelDerecho, BorderLayout.EAST);

        JPanel controles = crearControles();

        principal.add(cabecera, BorderLayout.NORTH);
        principal.add(areaCentral, BorderLayout.CENTER);
        principal.add(controles, BorderLayout.SOUTH);

        return principal;
    }

    private JPanel crearPanelMesa() {
        JPanel mesa = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                g2.setColor(new Color(100, 150, 200, 30));
                g2.fill(new RoundRectangle2D.Float(4, 4, w - 4, h - 4, 28, 28));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 5, h - 5, 28, 28));

                g2.setStroke(new BasicStroke(4f));
                g2.setColor(new Color(100, 180, 246));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 9, h - 9, 26, 26));

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(new Color(100, 180, 246));
                g2.fillOval(w / 2 - 80, h / 2 - 80, 160, 160);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2.setFont(new Font("Arial", Font.BOLD, 42));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(AZUL_OSCURO);
                g2.drawString("FLIP", w / 2 - fm.stringWidth("FLIP") / 2, h / 2 - 8);

                g2.setFont(new Font("Arial", Font.BOLD, 56));
                fm = g2.getFontMetrics();
                g2.drawString("7", w / 2 - fm.stringWidth("7") / 2, h / 2 + 45);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };

        mesa.setOpaque(false);
        mesa.setBorder(new EmptyBorder(25, 25, 25, 25));

        panelJugadores = new JPanel(new GridLayout(2, 3, 15, 15));
        panelJugadores.setOpaque(false);

        mesa.add(panelJugadores, BorderLayout.CENTER);
        return mesa;
    }
    private JPanel crearCabeceraJuego() {
        JPanel cabecera = new JPanel(new BorderLayout()) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(100, 180, 246));
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
            }
        };

        cabecera.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel logo = new JLabel("FLIP 7");
        logo.setFont(new Font("Arial", Font.BOLD, 32));
        logo.setForeground(AZUL_OSCURO);

        indicadorTurno = new JLabel("");
        indicadorTurno.setFont(new Font("Arial", Font.BOLD, 16));
        indicadorTurno.setForeground(AZUL_OSCURO);

        cabecera.add(logo, BorderLayout.WEST);
        cabecera.add(indicadorTurno, BorderLayout.EAST);

        return cabecera;
    }

    private JPanel crearPanelDerecho() {
        JPanel derecho = new JPanel(new BorderLayout(0, 12));
        derecho.setOpaque(false);
        derecho.setPreferredSize(new Dimension(250, 0));

        panelInfo = new PanelInfoJuego();

        JPanel panelChat = new JPanel(new BorderLayout(0, 8)) {

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

        panelChat.setOpaque(false);
        panelChat.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel tituloChat = new JLabel("CHAT");
        tituloChat.setFont(new Font("Arial", Font.BOLD, 12));
        tituloChat.setForeground(AZUL_OSCURO);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setOpaque(false);
        areaChat.setForeground(new Color(51, 65, 85));
        areaChat.setFont(new Font("Arial", Font.PLAIN, 12));
        areaChat.setLineWrap(true);

        JScrollPane scrollChat = new JScrollPane(areaChat);
        scrollChat.setOpaque(false);
        scrollChat.getViewport().setOpaque(false);
        scrollChat.setBorder(null);

        entradaChat = new JTextField();
        entradaChat.setBackground(new Color(248, 250, 252));
        entradaChat.setForeground(new Color(30, 41, 59));
        entradaChat.setCaretColor(AZUL_OSCURO);
        entradaChat.setFont(new Font("Arial", Font.PLAIN, 12));
        entradaChat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));

        entradaChat.addActionListener(e -> {
            String t = entradaChat.getText().trim();
            if (!t.isEmpty() && cliente.estaConectado()) {
                cliente.enviarChat(t);
                entradaChat.setText("");
            }
        });

        panelChat.add(tituloChat, BorderLayout.NORTH);
        panelChat.add(scrollChat, BorderLayout.CENTER);
        panelChat.add(entradaChat, BorderLayout.SOUTH);

        derecho.add(panelInfo, BorderLayout.NORTH);
        derecho.add(panelChat, BorderLayout.CENTER);

        return derecho;
    }
