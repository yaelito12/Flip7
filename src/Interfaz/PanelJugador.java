package Interfaz;

import gflip7.commun.Jugador;
import gflip7.commun.Carta;

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
