public class EstrategiaAlphaBeta extends Estrategia {
    /*
     * Estrategia que implementa una busqueda AlphaBeta
     * 
     * Los parametros de la busqueda (funcion de evaluacion + cota máxima)
     * se establecen al crear el objeto o con las funciones
     * "establecerEvaluador()" y "establecerCapaMaxima()"
     */

    private Evaluador _evaluador;
    private int _capaMaxima;

    private int _jugadorMAX; // - guarda el identificador del jugador
                             // que hace el papel de MAX
    // - necesario al hacer las evaluaciones
    // de posiciones finales (ganador, perdedor, empate)
    // en el caso base de la recursividad del MINIMAX

    /** Creates a new instance of EstrategiaAlphaBeta */
    public EstrategiaAlphaBeta() {
    }

    public EstrategiaAlphaBeta(Evaluador evaluador, int capaMaxima) {
        this.establecerEvaluador(evaluador);
        this.establecerCapaMaxima(capaMaxima);
    }

    public int buscarMovimiento(Tablero tablero, int jugador) {
        // Implementa primera capa del MINIMAX + seleccion jugada mas prometedora
        //
        //
        // capa O -> capa MAX -> maximiza
        // devuelve la columna con mayor evaluacion

        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valorSucesor;
        int mejorPosicion = -1; // Movimiento nulo
        int mejorValor = _evaluador.MINIMO; // Minimo valor posible

        _jugadorMAX = jugador; // - anota el identificador del jugador que
                               // tiene el papel de MAX
                               // - necesario para evaluar posiciones finales
        for (col = 0; col < Tablero.NCOLUMNAS; col++) {
            if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                // crear nuevo tablero y comprobar ganador
                nuevoTablero = (Tablero) tablero.clone();
                nuevoTablero.anadirFicha(col, jugador);
                nuevoTablero.obtenerGanador();

                // evaluarlo (OJO: cambiar jugador, establecer capa a 1)
                valorSucesor = MINIMAX(nuevoTablero, _evaluador.MINIMO, _evaluador.MAXIMO;, Jugador.alternarJugador(jugador), 1);
                nuevoTablero = null; // Ya no se necesita

                // tomar mejor valor
                if (valorSucesor >= mejorValor) {
                    mejorValor = valorSucesor;
                    mejorPosicion = col;
                }
            }
        }
        return (mejorPosicion);
    }

    public int MINIMAX(Tablero tablero, int alpha, int beta, int jugador, int capa) {
        // Implementa la propagación de valores MINIMAX propiamente dicha
        // a partir del segundo nivel (capa 1)

        // Casos base
        if (tablero.hayEmpate()) {
            return (0);
        }
        // la evaluacion de posiciones finales (caso base recursididad)
        // se hace SIEMPRE desde la prespectiva de MAX
        // -> se usa el identificador del jugador MAX (1 o 2) guardado
        // en la llamada a buscarMovimiento()
        if (tablero.esGanador(_jugadorMAX)) { // gana MAX
            return (_evaluador.MAXIMO);
        }
        if (tablero.esGanador(Jugador.alternarJugador(_jugadorMAX))) { // gana el otro
            return (_evaluador.MINIMO);
        }
        if (capa == (_capaMaxima)) { // alcanza nivel maximo
            return (_evaluador.valoracion(tablero, _jugadorMAX));
        }

        // Recursividad sobre los sucesores
        boolean movimientosPosibles[] = tablero.columnasLibres();
        Tablero nuevoTablero;
        int col, valor, valorSucesor;

        if (esCapaMIN(capa)) {
            betaActual = beta;
            aux = 20000;
            
            for (col = 0; col < Tablero.NCOLUMNAS; col++) {
                if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                    // crear nuevo tablero y comprobar ganador
                    if(betaActual <= alfa){
                        break;
                    }
                    nuevoTablero = (Tablero) tablero.clone();
                    nuevoTablero.anadirFicha(col, jugador);
                    nuevoTablero.obtenerGanador();

                    // evaluarlo (OJO: cambiar jugador e incrementar capa)
                    valorSucesor = minimo2(aux, MINIMAX(nuevoTablero, alpha, beta, Jugador.alternarJugador(jugador), (capa + 1)));
                    betaActual = minimo2(betaActual, aux);
                    nuevoTablero = null; // Ya no se necesita
                }
        }
        } else {
            alphaActual = alpha;
            aux = -20000;
            for (col = 0; col < Tablero.NCOLUMNAS; col++) {
                if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                    // crear nuevo tablero y comprobar ganador
                    if(betaActual <= alfa){
                        break;
                    }
                    nuevoTablero = (Tablero) tablero.clone();
                    nuevoTablero.anadirFicha(col, jugador);
                    nuevoTablero.obtenerGanador();

                    // evaluarlo (OJO: cambiar jugador e incrementar capa)
                    valorSucesor = minimo2(aux, MINIMAX(nuevoTablero, alpha, beta, Jugador.alternarJugador(jugador), (capa + 1)));
                    betaActual = minimo2(betaActual, aux);
                    nuevoTablero = null; // Ya no se necesita
                }
            }
        return (valor);
    }

    public void establecerCapaMaxima(int capaMaxima) {
        _capaMaxima = capaMaxima;
    }

    public void establecerEvaluador(Evaluador evaluador) {
        _evaluador = evaluador;
    }

    private static final boolean esCapaMIN(int capa) {
        return ((capa % 2) == 1); // es impar
    }

    private static final boolean esCapaMAX(int capa) {
        return ((capa % 2) == 0); // es par
    }

    private static final int maximo2(int v1, int v2) {
        if (v1 > v2)
            return (v1);
        else
            return (v2);
    }

    private static final int minimo2(int v1, int v2) {
        if (v1 < v2)
            return (v1);
        else
            return (v2);
    }

} // Fin clase EstrategiaAlphaBeta