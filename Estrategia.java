/*
 * Estrategia.java
 *
 * Created on 10 de enero de 2004, 16:57
 */

/**
 *
 * @author ribadas
 */
public abstract class Estrategia {
    /*
     * Superclase del patron estrategia, el ofrece interfaz comun de todas las
     * estrategias (funcion buscarMovimiento).
     */

    /** Creates a new instance of Estrategia */
    /* Definimos las variables para las metricas */
    public int Nnodos = 0;
    public int Nmovs = 0; 
    public long tiempoSum = 0;
    public Estrategia() {
    }

    public abstract int buscarMovimiento(Tablero tablero, int jugador);

    public void resetMetricas() {
        Nnodos = 0;
        Nmovs = 0;
        tiempoSum = 0;
    }

}
