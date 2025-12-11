package Interfaz;

import gflip7.comun.Carta;
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
    private void dibujarCarta(Graphics2D g2, Carta carta, int x, int y, int w, int h) {

        Color colorFondo, colorBorde, colorTexto, colorAcento;

        switch (carta.getTipo()) {
            case NUMERO:
                colorFondo = new Color(255, 252, 240);
                colorBorde = new Color(180, 165, 100);
                colorAcento = new Color(200, 185, 120);
                colorTexto = obtenerColorNumero(carta.getValor());
                break;
            case MODIFICADOR:
                colorFondo = new Color(255, 220, 100);
                colorBorde = new Color(200, 160, 50);
                colorAcento = new Color(220, 180, 70);
                colorTexto = new Color(150, 100, 30);
                break;
            case CONGELAR:
                colorFondo = new Color(180, 220, 245);
                colorBorde = new Color(100, 160, 200);
                colorAcento = new Color(140, 190, 225);
                colorTexto = new Color(40, 100, 150);
                break;
            case VOLTEAR_TRES:
                colorFondo = new Color(255, 240, 120);
                colorBorde = new Color(200, 180, 60);
                colorAcento = new Color(230, 210, 90);
                colorTexto = new Color(50, 120, 100);
                break;
            case SEGUNDA_OPORTUNIDAD:
                colorFondo = new Color(230, 100, 120);
                colorBorde = new Color(180, 60, 80);
                colorAcento = new Color(210, 80, 100);
                colorTexto = new Color(255, 250, 250);
                break;
            default:
                colorFondo = Color.WHITE;
                colorBorde = Color.GRAY;
                colorAcento = Color.LIGHT_GRAY;
                colorTexto = Color.BLACK;
        }

        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(new RoundRectangle2D.Float(x + 3, y + 3, w, h, 10, 10));

        GradientPaint grad = new GradientPaint(x, y, colorFondo, x, y + h, oscurecer(colorFondo, 0.95f));
        g2.setPaint(grad);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, 10, 10));

        g2.setStroke(new BasicStroke(2.5f));
        g2.setColor(colorBorde);
        g2.draw(new RoundRectangle2D.Float(x + 4, y + 4, w - 8, h - 8, 6, 6));

        g2.setStroke(new BasicStroke(0.8f));
        g2.setColor(new Color(colorBorde.getRed(), colorBorde.getGreen(), colorBorde.getBlue(), 80));
        g2.draw(new RoundRectangle2D.Float(x + 7, y + 7, w - 14, h - 14, 4, 4));

        dibujarAlas(g2, x, y, w, h, colorAcento);

        String texto = obtenerTextoCarta(carta);
        g2.setColor(colorTexto);

        Font fuente;
        if (carta.getTipo() == Carta.TipoCarta.NUMERO) fuente = new Font("Georgia", Font.BOLD, 28);
        else if (carta.getTipo() == Carta.TipoCarta.MODIFICADOR) fuente = new Font("Georgia", Font.BOLD, 22);
        else fuente = new Font("Arial", Font.BOLD, 11);

        g2.setFont(fuente);

        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(texto)) / 2;
        int ty = y + h / 2 + fm.getAscent() / 3 - 2;

        g2.setColor(new Color(0, 0, 0, 25));
        g2.drawString(texto, tx + 1, ty + 1);
        g2.setColor(colorTexto);
        g2.drawString(texto, tx, ty);

        String sub = obtenerSubtextoCarta(carta);
        if (sub != null) {
            g2.setFont(new Font("Arial", Font.BOLD, 7));
            fm = g2.getFontMetrics();
            tx = x + (w - fm.stringWidth(sub)) / 2;
            g2.setColor(new Color(colorTexto.getRed(), colorTexto.getGreen(), colorTexto.getBlue(), 140));
            g2.drawString(sub, tx, y + h - 10);
        }

        if (carta.getTipo() == Carta.TipoCarta.NUMERO) {
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(new Color(colorTexto.getRed(), colorTexto.getGreen(), colorTexto.getBlue(), 180));
            g2.drawString(String.valueOf(carta.getValor()), x + 10, y + 16);
        }
    }
      private void dibujarAlas(Graphics2D g2, int x, int y, int w, int h, Color c) {
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        g2.setStroke(new BasicStroke(1.2f));
        int yAla = y + 18;
        g2.drawArc(x + 12, yAla, 12, 8, 0, 180);
        g2.drawArc(x + w - 24, yAla, 12, 8, 0, 180);
    }

    private Color obtenerColorNumero(int v) {
        switch (v) {
            case 0: return new Color(80, 80, 80);
            case 1: case 2: return new Color(110, 90, 60);
            case 3: case 6: return new Color(20, 130, 110);
            case 4: case 5: return new Color(170, 60, 90);
            case 7: case 8: return new Color(220, 140, 70);
            case 9: case 10: return new Color(120, 70, 140);
            case 11: case 12: return new Color(70, 110, 160);
            default: return Color.BLACK;
        }
    }

    private Color oscurecer(Color c, float f) {
        return new Color((int)(c.getRed() * f), (int)(c.getGreen() * f), (int)(c.getBlue() * f));
    }

    private String obtenerTextoCarta(Carta carta) {
        switch (carta.getTipo()) {
            case NUMERO: return String.valueOf(carta.getValor());
            case MODIFICADOR: return carta.esX2() ? "×2" : "+" + carta.getValor();
            case CONGELAR: return "CONGELAR";
            case VOLTEAR_TRES: return "VOLTEAR 3";
            case SEGUNDA_OPORTUNIDAD: return "2ND";
            default: return "?";
        }
    }

    private String obtenerSubtextoCarta(Carta carta) {
        if (carta.getTipo() == Carta.TipoCarta.NUMERO) {
            String[] n = {"CERO", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE", "DIEZ", "ONCE", "DOCE"};
            return carta.getValor() < n.length ? n[carta.getValor()] : null;
        }
        if (carta.getTipo() == Carta.TipoCarta.SEGUNDA_OPORTUNIDAD) return "OPORTUNIDAD";
        if (carta.getTipo() == Carta.TipoCarta.CONGELAR) return "CONGELAR";
        if (carta.getTipo() == Carta.TipoCarta.VOLTEAR_TRES) return "TRES";
        return null;
    }
}
