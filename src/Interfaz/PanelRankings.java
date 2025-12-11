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
    
    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("TABLA DE RANKINGS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(AZUL_OSCURO);
                g2.drawString(getText(), 0, getHeight() - 5);
            }
        };
        titulo.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel subtitulo = new JLabel("Los mejores jugadores de Flip 7");
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitulo.setForeground(new Color(100, 116, 139));

        JPanel panelTitulo = new JPanel(new BorderLayout(0, 5));
        panelTitulo.setOpaque(false);
        panelTitulo.add(titulo, BorderLayout.NORTH);
        panelTitulo.add(subtitulo, BorderLayout.CENTER);

        header.add(panelTitulo, BorderLayout.WEST);

        return header;
    }

