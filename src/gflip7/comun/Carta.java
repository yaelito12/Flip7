package flip7.comun;

import java.io.Serializable;

public class Carta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TipoCarta { 
        NUMERO, 
        MODIFICADOR, 
        CONGELAR, 
        VOLTEAR_TRES, 
        SEGUNDA_OPORTUNIDAD 
    }
    
    private TipoCarta tipo;
    private int valor;
    private boolean esX2;
    
    public Carta(TipoCarta tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.esX2 = (tipo == TipoCarta.MODIFICADOR && valor == -1);
    }
    
    public TipoCarta getTipo() { 
        return tipo; 
    }
    
    public int getValor() { 
        return valor; 
    }
    
    public boolean esX2() { 
        return esX2; 
    }

 

    public boolean esCartaAccion() { 
        return tipo == TipoCarta.CONGELAR || 
               tipo == TipoCarta.VOLTEAR_TRES || 
               tipo == TipoCarta.SEGUNDA_OPORTUNIDAD; 
    }

    public boolean esCartaNumero() { 
        return tipo == TipoCarta.NUMERO; 
    }

    public boolean esCartaModificador() { 
        return tipo == TipoCarta.MODIFICADOR; 
    }
}