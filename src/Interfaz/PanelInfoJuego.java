package Interfaz;

import flip7.common.*;
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