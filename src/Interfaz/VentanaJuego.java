package Interfaz;

import cliente.JuegoCliente;
import gflip7.comun.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class VentanaJuego extends JFrame implements JuegoCliente.EscuchaClienteJuego {

    private JuegoCliente cliente = new JuegoCliente();
    private int miIdJugador = -1;
    private boolean esMiTurno, juegoIniciado, esEspectador;

    private Map<Integer, PanelJugador> panelesJugadores = new HashMap<>();

    private CardLayout cardLayout;
    private JPanel contenedorPrincipal;
    private PanelInicioSesion panelLogin;
    private PanelLobby panelLobby;
    private SalaDeEspera salaDeEspera;
    private PanelRankings panelRankings;
    private boolean esHost = false;
    private JPanel panelJuego;
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

        // CAMBIO IMPORTANTE: No cerrar automáticamente
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Agregar listener para manejar el cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manejarCierreVentana();
            }
        });

        setSize(1250, 850);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
    }

    private void manejarCierreVentana() {
        if (juegoIniciado && cliente.estaConectado()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "⚠️ HAY UNA PARTIDA EN CURSO\n\n"
                    + "Si sales ahora:\n"
                    + "• Perderás la partida automáticamente\n"
                    + "• NO recibirás puntos por esta ronda\n"
                    + "• Se registrará como derrota en tus estadísticas\n\n"
                    + "¿Estás seguro de que quieres salir?",
                    "Advertencia - Partida en Curso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                // Salir de la sala y desconectar
                if (cliente.getIdSalaActual() != null) {
                    cliente.salirSala();
                }
                if (cliente.estaConectado()) {
                    cliente.desconectar();
                }
                dispose();
                System.exit(0);
            }
            // Si dice NO, no hace nada y permanece en el juego

        } // CASO 2: Está en una sala de espera
        // CASO 2: Esta en una sala de espera
        else if (cliente.getIdSalaActual() != null && cliente.estaConectado()) {
            String mensaje;

            if (esHost) {
                // Verificar si hay mas jugadores
                // (Asumimos que si es host y hay sala, puede haber mas jugadores)
                mensaje = "Eres el HOST de la sala.\n"
                        + "Si sales, el rol de host se transferira al siguiente jugador.\n\n"
                        + "Deseas salir?";
            } else {
                mensaje = "Estas en una sala de espera.\nDeseas salir de la sala y cerrar el juego?";
            }

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    mensaje,
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                cliente.salirSala();
                if (cliente.estaConectado()) {
                    cliente.desconectar();
                }
                dispose();
                System.exit(0);
            }
        } // CASO 3: Está conectado pero no en sala ni jugando
        else if (cliente.estaConectado()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas cerrar el juego?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                cliente.desconectar();
                dispose();
                System.exit(0);
            }
        } // CASO 4: No está conectado, puede cerrar directamente
        else {
            dispose();
            System.exit(0);
        }
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
                if (cliente.estaConectado()) {
                    cliente.desconectar();
                }
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
                if (comoEspectador) {
                    cliente.unirseSalaComoEspectador(idSala);
                } else {
                    cliente.unirseSala(idSala);
                }
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
                            "Eres el HOST. Si sales, el rol de host se transferira\n"
                            + "al siguiente jugador en la sala.\n\n"
                            + "Deseas salir?",
                            "Confirmar Salida",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
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

    private JPanel crearControles() {
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15)) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(100, 180, 246));
                g2.fillRect(0, 0, getWidth(), 4);
            }
        };

        controles.setBorder(new EmptyBorder(12, 0, 18, 0));

        botonListo = crearBoton("LISTO", VERDE);
        botonPedir = crearBoton("PEDIR CARTA", AZUL_OSCURO);
        botonPlantarse = crearBoton("PLANTARSE", NARANJA);
        JButton botonSalir = crearBoton("SALIR", ROJO);

        botonListo.addActionListener(e -> {
            cliente.listo();
            botonListo.setEnabled(false);
            botonListo.setText("ESPERANDO...");
        });

        botonPedir.addActionListener(e -> {
            if (esMiTurno) {
                cliente.pedir();
            }
        });

        botonPlantarse.addActionListener(e -> {
            if (esMiTurno) {
                cliente.plantarse();
            }
        });

        botonSalir.addActionListener(e -> {

            if (juegoIniciado) {
                int c = JOptionPane.showConfirmDialog(
                        this,
                        "⚠️ HAY UNA PARTIDA EN CURSO\n\n"
                        + "Si sales ahora:\n"
                        + "• Perderás la partida automáticamente\n"
                        + "• NO recibirás puntos por esta ronda\n"
                        + "• Se registrará como derrota en tus estadísticas\n\n"
                        + "¿Estás seguro de que quieres salir?",
                        "Advertencia - Partida en Curso",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (c == JOptionPane.YES_OPTION) {
                    cliente.salirSala();
                    juegoIniciado = false;
                    esHost = false;
                    limpiarEstadoJuego();
                    mostrarPanel("lobby");
                }
            } else if (esHost) {
                int c = JOptionPane.showConfirmDialog(
                        this,
                        "Eres el HOST. Si sales, la sala se cerrará.\n¿Salir?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (c == JOptionPane.YES_OPTION) {
                    cliente.salirSala();
                    esHost = false;
                    limpiarEstadoJuego();
                    mostrarPanel("lobby");
                }
            } else {
                cliente.salirSala();
                limpiarEstadoJuego();
                mostrarPanel("lobby");
            }
        });

        botonListo.setEnabled(false);
        botonPedir.setEnabled(false);
        botonPlantarse.setEnabled(false);

        controles.add(botonSalir);
        controles.add(botonListo);
        controles.add(botonPedir);
        controles.add(botonPlantarse);

        return controles;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                Color c = isEnabled()
                        ? (hover ? aclarar(color, 1.1f) : color)
                        : new Color(203, 213, 225);

                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(3, 3, w - 3, h - 3, 14, 14));

                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 14, 14));

                g2.setPaint(new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, h / 2, new Color(255, 255, 255, 0)));
                g2.fill(new RoundRectangle2D.Float(2, 2, w - 7, h / 2 - 2, 12, 12));

                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();

                g2.setColor(isEnabled() ? Color.WHITE : new Color(148, 163, 184));
                g2.drawString(getText(),
                        (w - fm.stringWidth(getText())) / 2,
                        (h + fm.getAscent() - fm.getDescent()) / 2);
            }

            private Color aclarar(Color col, float factor) {
                return new Color(
                        Math.min(255, (int) (col.getRed() * factor)),
                        Math.min(255, (int) (col.getGreen() * factor)),
                        Math.min(255, (int) (col.getBlue() * factor))
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

    private void actualizarControles() {
        SwingUtilities.invokeLater(() -> {
            botonPedir.setEnabled(juegoIniciado && esMiTurno && !esEspectador);
            botonPlantarse.setEnabled(juegoIniciado && esMiTurno && !esEspectador);
        });
    }

    private void actualizarPanelesJugadores(EstadoJuego estado) {

        if (estado == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {

            List<Jugador> jugadores = estado.getJugadores();

            List<Jugador> conectados = new java.util.ArrayList<>();
            for (Jugador j : jugadores) {
                if (j.estaConectado()) {
                    conectados.add(j);
                }
            }

            if (panelesJugadores.size() != conectados.size()) {
                panelJugadores.removeAll();
                panelesJugadores.clear();

                for (Jugador j : conectados) {
                    PanelJugador pj = new PanelJugador(j.getId() == miIdJugador);
                    pj.setJugador(j);
                    panelesJugadores.put(j.getId(), pj);
                    panelJugadores.add(pj);
                }

                for (int i = conectados.size(); i < 6; i++) {
                    JPanel vacio = new JPanel();
                    vacio.setOpaque(false);
                    panelJugadores.add(vacio);
                }

                panelJugadores.revalidate();
            }

            Jugador turno = estado.getJugadorActual();

            for (Jugador j : conectados) {
                PanelJugador pj = panelesJugadores.get(j.getId());
                if (pj != null) {
                    pj.setJugador(j);
                    pj.setTurnoActual(turno != null && turno.getId() == j.getId());
                }
            }

            panelInfo.actualizarEstado(estado);
            panelJugadores.repaint();
        });
    }

    @Override
    public void alConectar(int idJugador, String nombreJugador) {
    }

    @Override
    public void alDesconectar() {
        SwingUtilities.invokeLater(() -> {
            juegoIniciado = false;
            esMiTurno = false;
            esHost = false;
            mostrarPanel("login");
            JOptionPane.showMessageDialog(
                    this,
                    "Desconectado del servidor",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    @Override
    public void alLoginExitoso(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            miIdJugador = usuario.getId();
            cliente.setNombreJugador(usuario.getNombreUsuario());
            panelLobby.setPlayerName(usuario.getNombreUsuario());
            mostrarPanel("lobby");
            panelInfo.registrar("Conexión establecida");
            panelInfo.registrar("  Partidas: " + usuario.getPartidasJugadas()
                    + " | Ganadas: " + usuario.getPartidasGanadas());
        });
    }

    @Override
    public void alLoginFallido(String razon) {
        panelLogin.alFallarInicioSesion(razon);
    }

    @Override
    public void alRegistroExitoso(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            panelLogin.restablecerBotones();
            JOptionPane.showMessageDialog(
                    this,
                    "Usuario registrado correctamente",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            miIdJugador = usuario.getId();
            cliente.setNombreJugador(usuario.getNombreUsuario());
            panelLobby.setPlayerName(usuario.getNombreUsuario());
            mostrarPanel("lobby");
            panelInfo.registrar("Registro exitoso");
        });
    }

    @Override
    public void alRegistroFallido(String razon) {
        panelLogin.alFallarRegistro(razon);
    }

    @Override
    public void alUnirseJugador(int idJugador, String nombreJugador) {
        panelInfo.registrar("+ " + nombreJugador + " se unió");
    }

    @Override
    public void alSalirJugador(int idJugador, String nombreJugador) {
        SwingUtilities.invokeLater(() -> {
            panelInfo.registrar("- " + nombreJugador + " salió");

            PanelJugador panel = panelesJugadores.remove(idJugador);
            if (panel != null) {
                panelJugadores.remove(panel);
                JPanel vacio = new JPanel();
                vacio.setOpaque(false);
                panelJugadores.add(vacio);
                panelJugadores.revalidate();
                panelJugadores.repaint();
            }
        });
    }

    @Override
    public void alIniciarJuego(List<Jugador> jugadores) {
        SwingUtilities.invokeLater(() -> {
            juegoIniciado = true;
            botonListo.setEnabled(false);
            botonListo.setText("EN JUEGO");
            mostrarPanel("game");
            panelInfo.registrar("\nJUEGO INICIADO\n");
            actualizarControles();
        });
    }

    @Override
    public void alIniciarRonda(int numeroRonda) {
        panelInfo.registrar("\nRONDA " + numeroRonda);
    }

    @Override
    public void alTuTurno(int idJugador) {
        SwingUtilities.invokeLater(() -> {
            esMiTurno = (idJugador == miIdJugador);
            actualizarControles();

            if (esMiTurno && !esEspectador) {
                indicadorTurno.setText("TU TURNO");
                Toolkit.getDefaultToolkit().beep();
            } else {
                EstadoJuego s = cliente.getEstadoJuegoActual();
                if (s != null) {
                    Jugador j = s.getJugadorPorId(idJugador);
                    if (j != null) {
                        indicadorTurno.setText("Turno: " + j.getNombre());
                    }
                }
            }
        });
    }

    @Override
    public void alRepartirCarta(int idJugador, Carta carta) {
        EstadoJuego s = cliente.getEstadoJuegoActual();
        if (s != null) {
            Jugador j = s.getJugadorPorId(idJugador);
            if (j != null) {
                panelInfo.registrar(j.getNombre() + " ← " + carta);
            }
        }
    }

    @Override
    public void alJugadorEliminado(int idJugador, Carta carta) {
        EstadoJuego s = cliente.getEstadoJuegoActual();
        if (s != null) {
            Jugador j = s.getJugadorPorId(idJugador);
            if (j != null) {
                panelInfo.registrar("X " + j.getNombre() + " eliminado con " + carta);
            }
        }
        if (idJugador == miIdJugador) {
            esMiTurno = false;
            actualizarControles();
        }
    }

    @Override
    public void alJugadorPlantado(int idJugador) {
        EstadoJuego s = cliente.getEstadoJuegoActual();
        if (s != null) {
            Jugador j = s.getJugadorPorId(idJugador);
            if (j != null) {
                panelInfo.registrar(j.getNombre() + " se plantó");
            }
        }
        if (idJugador == miIdJugador) {
            esMiTurno = false;
            actualizarControles();
        }
    }

    @Override
    public void alJugadorCongelado(int idJugador) {
        EstadoJuego s = cliente.getEstadoJuegoActual();
        if (s != null) {
            Jugador j = s.getJugadorPorId(idJugador);
            if (j != null) {
                panelInfo.registrar(j.getNombre() + " congelado");
            }
        }
    }

    @Override
    public void alRobarCartaAccion(int idJugador, Carta carta) {
        EstadoJuego s = cliente.getEstadoJuegoActual();
        if (s != null) {
            Jugador j = s.getJugadorPorId(idJugador);
            if (j != null) {
                panelInfo.registrar("* " + j.getNombre() + " -> " + carta);
            }
        }
    }

    @Override
    public void alElegirObjetivoAccion(Carta carta, List<Jugador> jugadoresActivos) {
        if (esEspectador) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            botonPedir.setEnabled(false);
            botonPlantarse.setEnabled(false);

            String[] opciones = new String[jugadoresActivos.size()];
            for (int i = 0; i < jugadoresActivos.size(); i++) {
                opciones[i] = jugadoresActivos.get(i).getNombre();
            }

            String seleccion = null;

            while (seleccion == null) {
                seleccion = (String) JOptionPane.showInputDialog(
                        this,
                        "¿A quién asignas " + carta + "?",
                        "Carta de Acción",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );
            }

            for (Jugador j : jugadoresActivos) {
                if (j.getNombre().equals(seleccion)) {
                    cliente.asignarCartaAccion(j.getId(), carta);
                    break;
                }
            }
        });
    }

    @Override
    public void alFinRonda(List<Jugador> jugadores, int numeroRonda) {
        SwingUtilities.invokeLater(() -> {
            esMiTurno = false;
            indicadorTurno.setText("");
            actualizarControles();

            panelInfo.registrar("\nFIN RONDA " + numeroRonda);
            for (Jugador j : jugadores) {
                panelInfo.registrar("  " + j.getNombre() + ": +" + j.getPuntajeRonda()
                        + " → " + j.getPuntajeTotal());
            }
            panelInfo.mostrarPuntuaciones(jugadores);
        });
    }

    @Override
    public void alProximaRonda(int numeroRonda, int segundosEspera) {
        JOptionPane.showMessageDialog(
                this,
                "Siguiente ronda en " + segundosEspera + " segundos",
                "Ronda " + numeroRonda,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void alFinJuego(List<Jugador> jugadores, int idGanador) {
        SwingUtilities.invokeLater(() -> {
            juegoIniciado = false;
            esMiTurno = false;
            actualizarControles();

            Jugador ganador = null;
            for (Jugador j : jugadores) {
                if (j.getId() == idGanador) {
                    ganador = j;
                }
            }

            String msg = ganador != null
                    ? ganador.getNombre() + " gana con " + ganador.getPuntajeTotal() + " pts"
                    : "Fin del juego";

            panelInfo.registrar("\n" + msg + "\n");

            int opcion = JOptionPane.showOptionDialog(
                    this,
                    msg + "\n\n¿Jugar otra partida?",
                    "Fin del Juego",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Revancha", "Salir"},
                    "Revancha"
            );

            if (opcion == 0) {
                salaDeEspera.reset();
                limpiarEstadoJuego();
                mostrarPanel("waiting");
            } else {
                cliente.salirSala();
                limpiarEstadoJuego();
                mostrarPanel("lobby");
            }
        });
    }

    @Override
    public void alActualizarEstado(EstadoJuego estado) {
        actualizarPanelesJugadores(estado);
    }

    @Override
    public void alMensajeChat(int idJugador, String nombreJugador, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaChat.append(nombreJugador + ": " + mensaje + "\n");
            areaChat.setCaretPosition(areaChat.getDocument().getLength());
        });
    }

    @Override
    public void alError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            panelInfo.registrar("! " + mensaje);
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void alListaSalas(List<SalaJuego> salas) {
        panelLobby.updateRoomList(salas);
    }

    @Override
    public void alCrearSala(SalaJuego sala, int idJugador) {
        SwingUtilities.invokeLater(() -> {
            miIdJugador = idJugador;
            esHost = true;
            esEspectador = false;
            salaDeEspera.setSpectator(false);
            salaDeEspera.updateRoom(sala);
            mostrarPanel("waiting");
        });
    }

    @Override
    public void alUnirseSala(SalaJuego sala, int idJugador) {
        SwingUtilities.invokeLater(() -> {
            miIdJugador = idJugador;
            esEspectador = (idJugador < 0);
            esHost = false;
            salaDeEspera.setSpectator(esEspectador);
            salaDeEspera.updateRoom(sala);
            mostrarPanel("waiting");
        });
    }

    @Override
    public void alActualizarSala(SalaJuego sala) {
        salaDeEspera.updateRoom(sala);
    }

    @Override
    public void alErrorSala(String error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void alRecibirRankings(List<Usuario> rankings) {
        panelRankings.actualizarRankings(rankings);
    }

    @Override
    public void alCambioHost(String nuevoHost, String anteriorHost) {
        SwingUtilities.invokeLater(() -> {
            panelInfo.registrar("! Cambio de host: " + nuevoHost);
            panelInfo.registrar("  (Anterior: " + anteriorHost + ")");

            salaDeEspera.notificarCambioHost(nuevoHost);

            String miNombre = cliente.getNombreJugador();
            if (miNombre != null && miNombre.equals(nuevoHost)) {
                esHost = true;

                JOptionPane.showMessageDialog(
                        this,
                        "Ahora eres el HOST de la sala.\n\n"
                        + "Como host, puedes:\n"
                        + "- Iniciar la partida cuando todos esten listos\n"
                        + "- La sala se cerrara si tu sales",
                        "Eres el nuevo Host",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                esHost = false;
            }
        });
    }

    private void limpiarEstadoJuego() {
        SwingUtilities.invokeLater(() -> {
            panelJugadores.removeAll();
            panelesJugadores.clear();

            for (int i = 0; i < 6; i++) {
                JPanel vacio = new JPanel();
                vacio.setOpaque(false);
                panelJugadores.add(vacio);
            }

            panelJugadores.revalidate();
            panelJugadores.repaint();
            areaChat.setText("");
            indicadorTurno.setText("");
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            VentanaJuego v = new VentanaJuego();
            v.setVisible(true);
        });
    }
}
