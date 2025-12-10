package flip7.client.gui;

import flip7.common.Carta;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class PanelCarta extends JPanel {

    private java.util.List<Carta> cartas = new ArrayList<>();

    public PanelCarta() {
        setOpaque(false);
    }

    public void setCartas(java.util.List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
        repaint();
    }

    public void limpiar() {
        cartas.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int anchoCarta = 58, altoCarta = 85;
        int espacio = 6;
        int y = 5;

        if (cartas.isEmpty()) {
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fill(new RoundRectangle2D.Float(5, y, anchoCarta, altoCarta, 10, 10));
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 5}, 0));
            g2.setColor(new Color(255, 255, 255, 30));
            g2.draw(new RoundRectangle2D.Float(5, y, anchoCarta, altoCarta, 10, 10));
            return;
        }

        int anchoTotal = cartas.size() * anchoCarta + (cartas.size() - 1) * espacio;
        int anchoDisponible = getWidth() - 10;

        if (anchoTotal > anchoDisponible && cartas.size() > 1) {
            espacio = (anchoDisponible - anchoCarta) / (cartas.size()) - anchoCarta + espacio;
            if (espacio < -anchoCarta + 15) espacio = -anchoCarta + 15;
        }

        int x = 5;
        for (int i = 0; i < cartas.size(); i++) {
            dibujarCarta(g2, cartas.get(i), x, y, anchoCarta, altoCarta);
            x += anchoCarta + espacio;
        }
    }