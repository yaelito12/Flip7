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
