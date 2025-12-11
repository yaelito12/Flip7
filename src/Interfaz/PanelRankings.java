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

    private JPanel crearPanelTablaRankings() {
        JPanel panel = new JPanel(new BorderLayout()) {
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
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnas = {"Pos", "Jugador", "Partidas", "Ganadas", "% Victoria", "Puntos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaRankings = new JTable(modeloTabla);
        tablaRankings.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaRankings.setRowHeight(45);
        tablaRankings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRankings.setShowGrid(false);
        tablaRankings.setIntercellSpacing(new Dimension(0, 5));

        JTableHeader headerTabla = tablaRankings.getTableHeader();
        headerTabla.setFont(new Font("Arial", Font.BOLD, 14));
        headerTabla.setBackground(AZUL_CLARO);
        headerTabla.setForeground(new Color(30, 41, 59));
        headerTabla.setPreferredSize(new Dimension(0, 45));

        DefaultTableCellRenderer rendererCentro = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (sel) {
                    c.setBackground(new Color(219, 234, 254));
                    c.setForeground(AZUL_OSCURO);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(new Color(51, 65, 85));
                }

                setHorizontalAlignment(col == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));

                return c;
            }
        };

        for (int i = 0; i < tablaRankings.getColumnCount(); i++) {
            tablaRankings.getColumnModel().getColumn(i).setCellRenderer(rendererCentro);
        }

        JScrollPane scroll = new JScrollPane(tablaRankings);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
