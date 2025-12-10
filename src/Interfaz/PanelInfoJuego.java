package Interfaz;

import gflip7.comun.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PanelInfoJuego extends JPanel {

    private JLabel etiquetaRonda, etiquetaFase, etiquetaMazo;
    private JTextArea areaRegistro;

    private static final Color AZUL_OSCURO = new Color(66, 133, 244);

    public PanelInfoJuego() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        JPanel cajaInfo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(100, 150, 200, 30));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth(), getHeight(), 14, 14));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 14, 14));

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(100, 180, 246));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 5, getHeight() - 5, 13, 13));
            }
        };

        cajaInfo.setLayout(new GridLayout(3, 1, 0, 8));
        cajaInfo.setBorder(new EmptyBorder(15, 18, 15, 18));
        cajaInfo.setOpaque(false);

        etiquetaRonda = crearEtiqueta("RONDA", "1");
        etiquetaFase = crearEtiqueta("FASE", "Esperando");
        etiquetaMazo = crearEtiqueta("MAZO", "-- cartas");

        cajaInfo.add(etiquetaRonda);
        cajaInfo.add(etiquetaFase);
        cajaInfo.add(etiquetaMazo);

        JPanel cajaRegistro = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(100, 150, 200, 30));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth(), getHeight(), 14, 14));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 14, 14));

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(100, 180, 246));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 5, getHeight() - 5, 13, 13));
            }
        };
          cajaRegistro.setOpaque(false);
        cajaRegistro.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel tituloReg = new JLabel("REGISTRO");
        tituloReg.setFont(new Font("Arial", Font.BOLD, 11));
        tituloReg.setForeground(AZUL_OSCURO);
        tituloReg.setBorder(new EmptyBorder(0, 5, 8, 0));

        areaRegistro = new JTextArea();
        areaRegistro.setEditable(false);
        areaRegistro.setOpaque(false);
        areaRegistro.setForeground(new Color(51, 65, 85));
        areaRegistro.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaRegistro.setLineWrap(true);
        areaRegistro.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(areaRegistro);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(220, 140));

        cajaRegistro.add(tituloReg, BorderLayout.NORTH);
        cajaRegistro.add(scroll, BorderLayout.CENTER);

        add(cajaInfo, BorderLayout.NORTH);
        add(cajaRegistro, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String titulo, String valor) {
        return new JLabel(
            "<html><span style='color:#64748b;font-size:9px;font-weight:bold;'>"
            + titulo + "</span><br><span style='color:#1e293b;font-size:15px;font-weight:bold;'>"
            + valor + "</span></html>"
        );
    }