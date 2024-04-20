import java.util.Vector;
import java.io.*;

public class Tablero {

    public static final int NFILAS = 6;
    public static final int NCOLUMNAS = 7;
    public static final int NOBJETIVO = 4;

    private static final String MARCA_J1 = "X";
    private static final String MARCA_J2 = "O";
    private static final String MARCA_VACIO = " ";
    private static final String[] MARCAS = { MARCA_VACIO, MARCA_J1, MARCA_J2 };

    private static final int VACIO = 0;
    private static final int JUGADOR1 = 1;
    private static final int JUGADOR2 = 2;
    private static final int EMPATE = -1;

    private int[][] _casillas;
    private int[] _posicionLibre;
    private int _ganador = EMPATE;

    private static final double NORMALIZADORPOTENCIAL = (NCOLUMNAS >= NOBJETIVO
            ? (2 * NOBJETIVO - 1 <= NCOLUMNAS ? 2 * NOBJETIVO - 1 : NCOLUMNAS)
            : 0)
            + (NFILAS >= NOBJETIVO ? (2 * NOBJETIVO - 1 <= NCOLUMNAS ? 2 : (NCOLUMNAS >= NOBJETIVO ? 1 : 0)) + 1 : 0);

    static final MyFunctionalInterface[] HEURISTICAS = { Tablero::heuristicaSimetrica, Tablero::heuristicaPotentialWin,
            Tablero::heuristicaPotentialLoss, Tablero::heuristicaCentral, Tablero::heuristicaVerticalidad };

    /** Creates a new instance of Tablero */
    public Tablero() {
        this._casillas = new int[NCOLUMNAS][NFILAS];
        this._posicionLibre = new int[NCOLUMNAS];
        this.inicializar();
    }

    protected Object clone() {
        Tablero result = new Tablero();
        result.copiarCasillas(this._casillas);
        result.copiarPosicionLibre(this._posicionLibre);
        return (result);
    }

    protected void finalize() {
        this._casillas = null;
        this._posicionLibre = null;
    }

    public boolean equals(Object obj) {
        int col, fila;

        Tablero tablero = (Tablero) obj;
        return (tablero.casillasIguales(_casillas));
    }

    public String toString() {
        String result = new String();
        int col, fila;

        for (fila = NFILAS - 1; fila >= 0; fila--) {
            result += "|";
            for (col = 0; col < NCOLUMNAS; col++) {
                result += (MARCAS[_casillas[col][fila]] + "|");
            }
            result += "\n";
        }
        return (result);
    }

    public boolean[] columnasLibres() {
        boolean[] result = new boolean[NCOLUMNAS];
        int col;

        /*
         * Aquí obligamos a que si el tablero es simétrico
         * solo se pueda jugar en la mitad de la tabla
         * izquierda
         */
        if (simetricoVertical()) {
            for (col = 0; col < (NCOLUMNAS % 2 == 0 ? NCOLUMNAS / 2 : NCOLUMNAS / 2 + 1); col++) {
                result[col] = (_posicionLibre[col] < NFILAS);
            }
        } else {
            for (col = 0; col < NCOLUMNAS; col++) {
                result[col] = (_posicionLibre[col] < NFILAS);
            }
        }

        return (result);
    }

    boolean esFinal() {
        return (_ganador != 0);
    }

    /*
     * Función que chequea el estado del tablero para comprobar
     * si es una posicion final y tomar nota de ello en el atributo
     * privado _ganador
     * 
     * Debe ser llamada una vez creado el tablero, porque las demas
     * funciones consultarán la variable _ganador (en lugar de reevaluar
     * el tablero de nuevo cada vez )
     */

    public void obtenerGanador() {
        int col, fila, jugador;

        _ganador = 0; // no hay ganador
        for (col = 0; col < NCOLUMNAS; col++) {
            for (fila = 0; fila < NFILAS; fila++) {
                jugador = _casillas[col][fila];
                if (jugador != VACIO) {
                    if (hayLineaVertical(col, fila, jugador) ||
                            hayLineaHorizontal(col, fila, jugador) ||
                            hayLineaDiagonal(col, fila, jugador)) {
                        _ganador = jugador;
                        return;
                    }
                }
            }
        }
        // no gana ninguno de los dos
        // comprobar si hay empate (columnas agotadas)
        boolean empate = true;
        for (col = 0; col < NCOLUMNAS; col++) {
            empate = empate && (_posicionLibre[col] == NFILAS);
        }
        if (empate) {
            _ganador = EMPATE;
        }
    }

    void inicializar() {
        int col, fila;

        for (col = 0; col < NCOLUMNAS; col++) {
            for (fila = 0; fila < NFILAS; fila++) {
                _casillas[col][fila] = VACIO;
            }
            _posicionLibre[col] = 0;
        }
        _ganador = 0;
    }

    public void mostrar() {
        int col;

        System.out.println();
        System.out.print(this.toString());

        // Solo para ponerlo bonito
        System.out.print("_");
        for (col = 0; col < NCOLUMNAS; col++) {
            System.out.print("__");
        }
        System.out.println();
        System.out.print("|");
        for (col = 0; col < NCOLUMNAS; col++) {
            System.out.print(col + "|");
        }
        System.out.println();
    }

    public boolean ganaJ1() {
        return (_ganador == JUGADOR1);
    }

    public boolean ganaJ2() {
        return (_ganador == JUGADOR2);
    }

    public boolean hayEmpate() {
        return (_ganador == EMPATE);
    }

    public void anadirFicha(int columna, int jugador) {
        if (_posicionLibre[columna] < NCOLUMNAS - 1) {
            _casillas[columna][_posicionLibre[columna]] = jugador;
            _posicionLibre[columna]++;
        }
    }

    private boolean hayLineaVertical(int col, int fila, int jugador) {
        int j;
        int numCasillas = 0;

        for (j = fila; j < NFILAS; j++) {
            if (_casillas[col][j] == jugador) {
                numCasillas++;
            } else {
                break;
            }
        }
        return (numCasillas >= NOBJETIVO);
    }

    private boolean hayLineaHorizontal(int col, int fila, int jugador) {
        int i;
        int numCasillas = 0;

        for (i = col; i < NCOLUMNAS; i++) {
            if (_casillas[i][fila] == jugador) {
                numCasillas++;
            } else {
                break;
            }
        }
        return (numCasillas >= NOBJETIVO);
    }

    private boolean hayLineaDiagonal(int col, int fila, int jugador) {
        int i, j, k;
        int numCasillas = 0;

        // diagonales "crecientes"
        for (k = 0; k < NOBJETIVO; k++) {
            i = col + k;
            j = fila + k;
            if ((i < NCOLUMNAS) && (j < NFILAS)) {
                if (_casillas[i][j] == jugador) {
                    numCasillas++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (numCasillas >= NOBJETIVO) {
            return (true);
        }

        // diagonales "decrecientes"
        numCasillas = 0;
        for (k = 0; k < NOBJETIVO; k++) {
            i = col + k;
            j = fila - k;
            if ((i < NCOLUMNAS) && (j >= 0)) {
                if (_casillas[i][j] == jugador) {
                    numCasillas++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return (numCasillas >= NOBJETIVO);
    }

    private void copiarCasillas(int[][] casillas) {
        int col, fila;

        for (col = 0; col < NCOLUMNAS; col++) {
            for (fila = 0; fila < NFILAS; fila++) {
                this._casillas[col][fila] = casillas[col][fila];
            }
        }
    }

    private void copiarPosicionLibre(int[] posicionLibre) {
        int col, fila;

        for (col = 0; col < NCOLUMNAS; col++) {
            this._posicionLibre[col] = posicionLibre[col];
        }
    }

    public boolean casillasIguales(int[][] casillas) {
        int col, fila;
        for (col = 0; col < NCOLUMNAS; col++) {
            for (fila = 0; fila < NFILAS; fila++) {
                if (this._casillas[col][fila] != casillas[col][fila]) {
                    return (false); // Son distintos -> salir devolviendo falso
                }
            }
        }
        return (true); // Si llega todas las casillas son iguales
    }

    public boolean finalJuego() {
        return (_ganador != 0);
    }

    public int ganador() {
        return (_ganador);
    }

    public boolean esGanador(int jugador) {
        return (jugador == _ganador);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Aquí añadimos lo nuevo

    public int otherPlayer(int jugador) {
        return Jugador.alternarJugador(jugador);
        // return (jugador == JUGADOR1 ? JUGADOR2 : JUGADOR1);
    }

    public boolean simetricoVertical() {
        int col, fila;
        for (col = 0; col < NCOLUMNAS / 2; col++) {
            for (fila = 0; fila < NFILAS; fila++) {
                if (this._casillas[col][fila] != this._casillas[NCOLUMNAS - 1 - col][fila]) {
                    return (false); // Son distintos -> salir devolviendo falso
                }
            }
        }
        return (true); // Si llega todas las casillas son iguales
    }

    public int heuristicaSimetrica(int jugador) {
        /*
         * Esta función es una heurística bastante mala
         * Devuelve 1 si el tablero es simétrico
         * o 0 en caso contrario
         */
        if (simetricoVertical()) {
            return 1;
        } else {
            return 0;
        }
    }

    public double heuristicaPotentialWin(int jugador) {
        /*
         * Es una heurística muy buena
         * Por cada línea de NOBJETIVO fichas del jugador
         * no bloqueada por el otro jugador, suma 1
         * por cada ficha del jugador en la línea
         */
        int total = 0;

        // First the horizontal lines
        for (int fila = 0; fila < (NFILAS - NOBJETIVO); fila++) {
            for (int col = 0; col < NCOLUMNAS; col++) {
                int pieces = 0;
                for (int pos = 0; pos < NOBJETIVO; pos++) {
                    if (_casillas[col][fila + pos] == jugador) {
                        pieces++;
                    } else if (_casillas[col][fila + pos] == otherPlayer(jugador)) {
                        pieces = 0;
                        break;
                    }
                }
                total += pieces;
            }
        }

        // Now the vertical lines
        for (int col = 0; col < (NCOLUMNAS - NOBJETIVO); col++) {
            for (int fila = 0; fila < NFILAS; fila++) {
                int pieces = 0;
                for (int pos = 0; pos < NOBJETIVO; pos++) {
                    if (_casillas[col + pos][fila] == jugador) {
                        pieces++;
                    } else if (_casillas[col + pos][fila] == otherPlayer(jugador)) {
                        pieces = 0;
                        break;
                    }
                }
                total += pieces;
            }
        }

        // Now the diagonal lines from bottom-left to top-right
        for (int col = 0; col < (NCOLUMNAS - NOBJETIVO); col++) {
            for (int fila = 0; fila < (NFILAS - NOBJETIVO); fila++) {
                int pieces = 0;
                for (int pos = 0; pos < NOBJETIVO; pos++) {
                    if (_casillas[col + pos][fila + pos] == jugador) {
                        pieces++;
                    } else if (_casillas[col + pos][fila + pos] == otherPlayer(jugador)) {
                        pieces = 0;
                        break;
                    }
                }
                total += pieces;
            }
        }

        // Now the diagonal lines from top-left to bottom-right
        for (int col = 0; col < (NCOLUMNAS - NOBJETIVO); col++) {
            for (int fila = NOBJETIVO - 1; fila < NFILAS; fila++) {
                int pieces = 0;
                for (int pos = 0; pos < NOBJETIVO; pos++) {
                    if (_casillas[col + pos][fila - pos] == jugador) {
                        pieces++;
                    } else if (_casillas[col + pos][fila - pos] == otherPlayer(jugador)) {
                        pieces = 0;
                        break;
                    }
                }
                total += pieces;
            }
        }

        // Ahora normalizamos el valor como podemos
        // Lo hacemos diviendo por el número de líneas posibles
        // colocando la primera ficha de todas

        // Esto es todas las horizontales a bajo nivel
        // Una vertical
        // Dos diagonales si es posible

        return total / NORMALIZADORPOTENCIAL;
    }

    public double heuristicaPotentialLoss(int jugador) {
        /*
         * Lo mismo que heuristicaPotentialWin pero para el oponente
         * por eso le pasamos el oponente a heuristicaPotentialWin
         */
        return heuristicaPotentialWin(otherPlayer(jugador));
    }

    public double heuristicaCentral(int jugador) {
        /*
         * Esta heurística cuenta que las columnas centrales sean las más altas
         * Si una columna extrema es menor o igual que la central se suma 1
         * lo contrario se resta 1
         */
        double total = 0;

        // Primera mitad
        for (int col = 0; col < Math.ceil(NCOLUMNAS / 2.0) - 1; col++) {
            if (_posicionLibre[col] <= _posicionLibre[col + 1]) {
                total += 1;
            } else {
                total -= 1;
            }
        }

        for (int col = (int) Math.ceil(NCOLUMNAS / 2.0); col < NCOLUMNAS - 1; col++) {
            if (_posicionLibre[col] >= _posicionLibre[col + 1]) {
                total += 1;
            } else {
                total -= 1;
            }
        }
        // Normalizamos entre -1 y 1
        // entre el número de huecos
        return total / (NCOLUMNAS - 1);
    }

    public double heuristicaVerticalidad(int jugador) {
        /*
         * Queremos que los tableros sean lo menos verticales posibles
         * Para eso empezamos en el total y vamos restando por la altura de la columna
         * Es una suma geométrica. La primera ficha vale 1, la segunda 2, la tercera
         * 3...
         * Y tenemos que ir sumando todas y después restamos.
         */
        // https://es.wikipedia.org/wiki/Progresi%C3%B3n_aritm%C3%A9tica
        double total = NCOLUMNAS * NFILAS * (NFILAS + 1) / 2;

        for (int col = 0; col < NCOLUMNAS; col++) {
            total -= _posicionLibre[col] * (_posicionLibre[col] + 1) / 2;
        }

        // Normalizamos entre 0 y 1
        return total / (NCOLUMNAS * NFILAS * (NFILAS + 1) / 2);

    }

} // Fin clase Tablero

interface MyFunctionalInterface {
    double execute(Tablero heuristica, int jugador);
}