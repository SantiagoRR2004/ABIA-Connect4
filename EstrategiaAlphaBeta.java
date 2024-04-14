public class EstrategiaAlphaBeta extends EstrategiaMiniMax {
    /*
     * Estrategia que implementa una busqueda AlphaBeta
     * 
     * Los parametros de la busqueda (funcion de evaluacion + cota máxima)
     * se establecen al crear el objeto o con las funciones
     * "establecerEvaluador()" y "establecerCapaMaxima()"
     */

    /** Creates a new instance of EstrategiaAlphaBeta */
    public EstrategiaAlphaBeta() {
        super();
    }

    public EstrategiaAlphaBeta(Evaluador evaluador, int capaMaxima) {
        super(evaluador, capaMaxima);
    }

    @Override
    protected int Estrategia(Tablero tablero, int jugador, int capa) {
        return AlphaBeta(tablero, _evaluador.MINIMO, _evaluador.MAXIMO, jugador, capa);
    }

    public int AlphaBeta(Tablero tablero, int alpha, int beta, int jugador, int capa) {
        // Implementa la propagación de valores AlphaBeta propiamente dicha
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
        int col, aux;

        if (esCapaMIN(capa)) {
            int betaActual = beta;
            aux = 20000;

            for (col = 0; col < Tablero.NCOLUMNAS; col++) {
                if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                    // crear nuevo tablero y comprobar ganador
                    if (betaActual <= alpha) {
                        break; // poda alpha
                    }
                    nuevoTablero = (Tablero) tablero.clone();
                    nuevoTablero.anadirFicha(col, jugador);
                    nuevoTablero.obtenerGanador();

                    // evaluarlo (OJO: cambiar jugador e incrementar capa)
                    aux = minimo2(aux,
                            AlphaBeta(nuevoTablero, alpha, beta, Jugador.alternarJugador(jugador), (capa + 1)));
                    betaActual = minimo2(betaActual, aux);
                    nuevoTablero = null; // Ya no se necesita
                }
            }

        } else {
            int alphaActual = alpha;
            aux = -20000;
            for (col = 0; col < Tablero.NCOLUMNAS; col++) {
                if (movimientosPosibles[col]) { // se puede añadir ficha en columna
                    // crear nuevo tablero y comprobar ganador
                    if (alphaActual >= beta) {
                        break; // poda beta
                    }
                    nuevoTablero = (Tablero) tablero.clone();
                    nuevoTablero.anadirFicha(col, jugador);
                    nuevoTablero.obtenerGanador();

                    // evaluarlo (OJO: cambiar jugador e incrementar capa)
                    aux = maximo2(aux,
                            AlphaBeta(nuevoTablero, alpha, beta, Jugador.alternarJugador(jugador), (capa + 1)));
                    alphaActual = maximo2(alphaActual, aux);
                    nuevoTablero = null; // Ya no se necesita
                }
            }
        }
        return (aux);
    }

} // Fin clase EstrategiaAlphaBeta