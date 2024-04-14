public class EvaluadorPonderado extends Evaluador {
    /*
     */

    private int[] _pesos;
    private MyFunctionalInterface[] _funciones;
    private boolean _setUp = false;

    /** Creates a new instance of EvaluadorPonderado */
    public EvaluadorPonderado() {
    }

    public EvaluadorPonderado(int[] pesos, MyFunctionalInterface[] funciones) {
        inicializar(pesos, funciones);
    }

    public void inicializar(int[] pesos, MyFunctionalInterface[] funciones) {
        if (pesos.length == funciones.length) {
            this._pesos = pesos;
            this._funciones = funciones;
            this._setUp = true;
        } else {
            ERROR("Error: numero de pesos y funciones no coincide\n");
        }
    }

    public int valoracion(Tablero tablero, int jugador) {
        int valoracion = 0;
        if (!_setUp) {
            ERROR("Error: EvaluadorPonderado no inicializado\n");
        } else {
            for (int i = 0; i < _funciones.length; i++) {
                valoracion += _pesos[i] * _funciones[i].execute(tablero, jugador);
            }
        }
        return valoracion;
    }

    public static final void ERROR(java.lang.String mensaje) {
        System.out.println("ERROR\n\t" + mensaje);
    }

}
