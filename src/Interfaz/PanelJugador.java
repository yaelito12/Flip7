package Interfaz;

import gflip7.comun.Jugador;
import gflip7.comun.Carta;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class PanelJugador extends JPanel {

    private Jugador jugador;
    private boolean esMiTurno, esLocal;
    private PanelCarta panelCarta;

    private static final Color AZUL_OSCURO = new Color(66, 133, 244);
    private static final Color VERDE = new Color(74, 222, 128);
    private static final Color ROJO = new Color(248, 113, 113);
    private static final Color CYAN = new Color(103, 232, 249);

    public PanelJugador(boolean esLocal) {
        this.esLocal = esLocal;
        setLayout(new BorderLayout());
        setOpaque(false);

        panelCarta = new PanelCarta();
        add(panelCarta, BorderLayout.CENTER);
    }

    public void setJugador(Jugador j) {
        jugador = j;
        if (jugador != null) {
            panelCarta.setCartas(jugador.getTodasLasCartas());
        } else {
            panelCarta.limpiar();
        }
        repaint();
    }
  public void setTurnoActual(boolean turno) {
        esMiTurno = turno;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        g2.setColor(new Color(100, 150, 200, 40));
        g2.fill(new RoundRectangle2D.Float(4, 4, w - 4, h - 4, 16, 16));

        Color fondo = esLocal ? new Color(240, 253, 244) : Color.WHITE;
        g2.setColor(fondo);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 4, h - 4, 16, 16));

        Color borde = esLocal ? VERDE : new Color(203, 213, 225);

        if (esMiTurno && jugador != null && jugador.estaActivo()) {
            borde = AZUL_OSCURO;
            g2.setColor(new Color(66, 133, 244, 40));
            g2.setStroke(new BasicStroke(8f));
            g2.draw(new RoundRectangle2D.Float(0, 0, w - 4, h - 4, 16, 16));
        }

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(borde);
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 6, h - 6, 15, 15));

        if (jugador != null) {
            dibujarHeader(g2, w);
            dibujarEstado(g2, w, h);
        }

        super.paintComponent(g);
    }
     private void dibujarHeader(Graphics2D g2, int w) {

        g2.setColor(new Color(241, 245, 249));
        g2.fill(new RoundRectangle2D.Float(3, 3, w - 10, 38, 13, 13));
        g2.fillRect(3, 20, w - 10, 20);

        Color ind;
        if (jugador.estaEliminado()) ind = ROJO;
        else if (jugador.estaCongelado()) ind = CYAN;
        else if (jugador.estaPlantado()) ind = VERDE;
        else if (esMiTurno) ind = AZUL_OSCURO;
        else ind = new Color(148, 163, 184);

        g2.setColor(ind);
        g2.fillOval(12, 12, 14, 14);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(new Color(30, 41, 59));

        String nombre = jugador.getNombre() + (esLocal ? " (Tú)" : "");
        g2.drawString(nombre, 32, 24);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String score = String.valueOf(jugador.getPuntajeTotal());
        FontMetrics fm = g2.getFontMetrics();

        g2.setColor(AZUL_OSCURO);
        g2.drawString(score, w - fm.stringWidth(score) - 15, 26);
    }
     private void dibujarEstado(Graphics2D g2, int w, int h) {
        String texto = null;
        Color color = null;
        Color fondo = null;

        if (jugador.estaEliminado()) {
            texto = "ELIMINADO";
            color = ROJO;
            fondo = new Color(254, 226, 226);
        } else if (jugador.estaCongelado()) {
            texto = "CONGELADO";
            color = CYAN;
            fondo = new Color(207, 250, 254);
        } else if (jugador.estaPlantado()) {
            texto = "PLANTADO";
            color = VERDE;
            fondo = new Color(220, 252, 231);
        } else if (esMiTurno && jugador.estaActivo()) {
            texto = esLocal ? "► ¡TU TURNO!" : "► JUGANDO";
            color = AZUL_OSCURO;
            fondo = new Color(219, 234, 254);
        }

        if (texto != null) {
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            int wTxt = fm.stringWidth(texto) + 20;
            int x = (w - wTxt) / 2;

            g2.setColor(fondo);
            g2.fill(new RoundRectangle2D.Float(x, h - 28, wTxt, 20, 10, 10));

            g2.setColor(color);
            g2.drawString(texto, x + 10, h - 14);
        }
    }

    @Override
    public Insets getInsets() {
        return new Insets(45, 10, 35, 10);
    }
}



