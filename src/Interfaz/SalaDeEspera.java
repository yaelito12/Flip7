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