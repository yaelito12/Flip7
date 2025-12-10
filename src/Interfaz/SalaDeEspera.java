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