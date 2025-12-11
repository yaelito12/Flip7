package Interfaz;

import gflip7.comun.Usuario;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class PanelRankings extends JPanel {
    private JTable tablaRankings;
    private DefaultTableModel modeloTabla;
    private JButton botonVolver, botonActualizar;
    private EscuchaRankings escucha;

    private static final Color AZUL_OSCURO = new Color(66, 133, 244);
    private static final Color AZUL_CLARO = new Color(135, 206, 250);
    private static final Color VERDE = new Color(74, 222, 128);
    private static final Color ORO = new Color(255, 215, 0);
    private static final Color PLATA = new Color(192, 192, 192);
    private static final Color BRONCE = new Color(205, 127, 50);

    public interface EscuchaRankings {
        void alVolver();
        void alActualizar();
    }

    public PanelRankings(EscuchaRankings escucha) {
        this.escucha = escucha;
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(30, 50, 30, 50));

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelTablaRankings(), BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }
