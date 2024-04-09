public class EvaluadorPonderado extends Evaluador {
    /*
     */

    /** Creates a new instance of EvaluadorPonderado */
    public EvaluadorPonderado() {
    }

    public int valoracion(Tablero tablero, int jugador) {
        // return aleatorio
        return (generador.nextInt(MAXIMO + Math.abs(MINIMO)) - Math.abs(MINIMO));
    }
}