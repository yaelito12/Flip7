package Interfaz;

import gflip7.comun.SalaJuego;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class SalaDeEspera extends JPanel {

    private JLabel roomNameLabel, playersCountLabel, statusLabel;
    private JPanel playerListPanel;
    private JButton readyBtn, leaveBtn, joinAsPlayerBtn;
    private WaitingRoomListener listener;

    private boolean isReady = false;
    private boolean isSpectator = false;

    private Set<String> readyPlayers = new HashSet<>();
    private int totalPlayers = 0;
    private int maxPlayers = 4;

    private static final Color BLUE_DARK = new Color(66, 133, 244);
    private static final Color GREEN = new Color(74, 222, 128);
    private static final Color RED = new Color(248, 113, 113);
    private static final Color ORANGE = new Color(251, 146, 60);
    private static final Color PURPLE = new Color(139, 92, 246);

    public interface WaitingRoomListener {
        void onReady();
        void onLeaveRoom();
        void onJoinAsPlayer();
    }

    public SalaDeEspera(WaitingRoomListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(30, 60, 30, 60));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
    }
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        roomNameLabel = new JLabel("SALA DE ESPERA");
        roomNameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        roomNameLabel.setForeground(BLUE_DARK);

        playersCountLabel = new JLabel("Jugadores: 0/0");
        playersCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playersCountLabel.setForeground(new Color(100, 116, 139));

        header.add(roomNameLabel, BorderLayout.WEST);
        header.add(playersCountLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth(), getHeight(), 20, 20));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(BLUE_DARK);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 6, getHeight() - 6, 19, 19));
            }
        };

        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Jugadores en la sala:");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(51, 65, 85));

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(playerListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        center.add(title, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        statusLabel = new JLabel("Presiona LISTO para comenzar (mínimo 2 jugadores)");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(100, 116, 139));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        center.add(statusLabel, BorderLayout.SOUTH);

        return center;
    }
     private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        leaveBtn = createButton("SALIR", RED);
        readyBtn = createButton("LISTO", GREEN);
        joinAsPlayerBtn = createButton("UNIRSE", PURPLE);
        joinAsPlayerBtn.setVisible(false);

        leaveBtn.addActionListener(e -> {
            if (listener != null) listener.onLeaveRoom();
        });

        readyBtn.addActionListener(e -> {
            if (!isSpectator && !isReady && listener != null) {
                isReady = true;
                readyBtn.setText("LISTO!");
                listener.onReady();
                updateStatus();
            }
        });

        joinAsPlayerBtn.addActionListener(e -> {
            if (isSpectator && listener != null) {
                listener.onJoinAsPlayer();
            }
        });

        panel.add(leaveBtn);
        panel.add(readyBtn);
        panel.add(joinAsPlayerBtn);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            boolean hover = false;
            Color btnColor = color;

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
                Color c = hover ? btnColor.brighter() : btnColor;

                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(3, 3, w - 3, h - 3, 14, 14));

                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 3, h - 3, 14, 14));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                              (h + fm.getAscent() - fm.getDescent()) / 2);
            }
        };

        btn.setPreferredSize(new Dimension(180, 50));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        return btn;
    }
    
public void updateRoom(SalaJuego room) {
        if (room == null) return;

        SwingUtilities.invokeLater(() -> {

            roomNameLabel.setText(room.getNombreSala());

            String textCount = "Jugadores: " + room.getJugadoresActuales() + "/" + room.getMaxJugadores();
            if (room.getCantidadEspectadores() > 0)
                textCount += " | " + room.getCantidadEspectadores() + " observadores";

            playersCountLabel.setText(textCount);

            totalPlayers = room.getJugadoresActuales();
            maxPlayers = room.getMaxJugadores();

            // Mostrar botón "UNIRSE" si es espectador y hay espacio
            joinAsPlayerBtn.setVisible(
                isSpectator && !room.isJuegoIniciado() &&
                room.getJugadoresActuales() < room.getMaxJugadores()
            );

            // --- LISTA DE JUGADORES ---
            playerListPanel.removeAll();

            java.util.List<String> jugadores = room.getJugadores();
            for (int i = 0; i < jugadores.size(); i++) {
                String nombre = jugadores.get(i);
                boolean isHost = (i == 0);
                boolean ready = readyPlayers.contains(nombre);
                playerListPanel.add(createPlayerEntry(nombre, isHost, ready, false));
                playerListPanel.add(Box.createVerticalStrut(8));
            }

            // --- LISTA DE ESPECTADORES ---
            java.util.List<String> espectadores = room.getEspectadores();
            if (!espectadores.isEmpty()) {
                playerListPanel.add(Box.createVerticalStrut(10));
                JLabel specTitle = new JLabel("--- Observadores ---");
                specTitle.setFont(new Font("Arial", Font.ITALIC, 12));
                specTitle.setForeground(new Color(148, 163, 184));
                playerListPanel.add(specTitle);
                playerListPanel.add(Box.createVerticalStrut(8));

                for (String s : espectadores) {
                    playerListPanel.add(createPlayerEntry(s, false, false, true));
                    playerListPanel.add(Box.createVerticalStrut(6));
                }
            }

            playerListPanel.revalidate();
            playerListPanel.repaint();
            updateStatus();
        });
    }

public void setPlayerReady(String name) {
        readyPlayers.add(name);
    }

    private void updateStatus() {
        int readyCount = readyPlayers.size() + (isReady ? 1 : 0);

        if (isSpectator) {
            statusLabel.setText("Modo espectador - Esperando inicio de partida");
            statusLabel.setForeground(PURPLE);
        } else if (totalPlayers < 2) {
            statusLabel.setText("Esperando más jugadores... (mínimo 2)");
            statusLabel.setForeground(ORANGE);
        } else if (readyCount < totalPlayers) {
            statusLabel.setText("Esperando que todos estén listos (" + readyCount + "/" + totalPlayers + ")");
            statusLabel.setForeground(ORANGE);
        } else {
            statusLabel.setText("¡Todos listos! Iniciando partida...");
            statusLabel.setForeground(GREEN);
        }
    }

    private JPanel createPlayerEntry(String name, boolean isHost, boolean ready, boolean isSpec) {
        JPanel entry = new JPanel(new BorderLayout());
        entry.setOpaque(false);
        entry.setBorder(new EmptyBorder(12, 15, 12, 15));
        entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        String prefix = isSpec ? "(obs) "
                : ready ? "[OK] "
                : isHost ? "[Host] "
                : "";

        JLabel nameLabel = new JLabel(prefix + name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(
            isSpec ? PURPLE
            : ready ? GREEN.darker()
            : isHost ? ORANGE
            : new Color(51, 65, 85)
        );

        String roleText = isSpec ? "Observando"
                : ready ? "Listo"
                : isHost ? "Host"
                : "Esperando";

        JLabel roleLabel = new JLabel(roleText);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(
                isSpec ? PURPLE
                : ready ? GREEN
                : isHost ? ORANGE
                : new Color(148, 163, 184));

        entry.add(nameLabel, BorderLayout.WEST);
        entry.add(roleLabel, BorderLayout.EAST);

        return entry;
    }