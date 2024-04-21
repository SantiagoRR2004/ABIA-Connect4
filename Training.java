import java.util.Arrays;
import java.util.Random;

public class Training {

    int max = 10;
    int min = -10;
    float step = 1f;

    String filename = "pesos.txt";

    int totalTrains = 10000;

    int numberOfGames = 2;
    int capaMaxima = 4;

    Random random = new Random();
    private static MyFunctionalInterface[] _heuristicas;

    public static void main(String[] args) {
        Training t = new Training();
        t.train();
    }

    public void train() {

        float[] pesos = Archivo.obtenerPesos(filename);
        getHeuristicas(pesos.length);

        for (int i = 0; i < totalTrains; i++) {

            float[] newPesos = changePesos(pesos);
            int[] stats = playGames(pesos, newPesos);

            System.out.println("Wins: " + stats[0] + "\t\tDraws: " + stats[1] + "\tLoses: " +
                    stats[2]);

            int[] stats2 = playGames(newPesos, pesos);

            if (stats[0] != stats2[2] || stats[1] != stats2[1] || stats[2] != stats2[0]) {
                System.out.println("Error: The operation is not reversable.");
            }

            if (newerBetter(stats[0], stats[1], stats[2])) {
                Archivo.guardarPesos(filename, newPesos);
                pesos = Arrays.copyOf(newPesos, newPesos.length);
                System.out.println("New weights saved.");
            }
        }
    }

    public void getHeuristicas(int lenPesos) {
        Training._heuristicas = new MyFunctionalInterface[lenPesos];

        for (int i = 0; i < lenPesos; i++) {
            Training._heuristicas[i] = Tablero.HEURISTICAS[i];
        }
    }

    public float[] changePesos(float[] pesos) {

        float[] newPesos = Arrays.copyOf(pesos, pesos.length);
        int randomIndex = random.nextInt(pesos.length);
        int upOrDown = (random.nextInt(2) == 0) ? -1 : 1; // -1 or 1

        if (min <= pesos[randomIndex] + step * upOrDown && pesos[randomIndex] + step * upOrDown <= max) {
            newPesos[randomIndex] = pesos[randomIndex] + step * upOrDown;
        }
        return newPesos;
    }

    public int[] playGames(float[] pesos, float[] newPesos) {
        /*
         * Aquí las victorias son de los nuevos pesos.
         * 
         * Esto iterará numberOfGames veces, jugando ese número de partidas con los
         * pesos dados. Luego, devolverá un array con el número de victorias, derrotas y
         * empates.
         */
        Jugador jugador1 = new Jugador(1);
        Jugador jugador2 = new Jugador(2);

        jugador1.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos, _heuristicas), capaMaxima));
        jugador2.establecerEstrategia(
                new EstrategiaAlphaBeta(new EvaluadorPonderado(newPesos, _heuristicas), capaMaxima));

        int wins = 0;
        int loses = 0;
        int draws = 0;

        // Esta variable significa si los nuevos pesos tienen el siguiente movimiento.
        boolean firstNew = random.nextBoolean();

        for (int i = 0; i < numberOfGames; i++) {
            Tablero tablero = new Tablero();
            tablero.inicializar();
            if (firstNew) {
                Conecta4.jugar(jugador2, jugador1, tablero);
            } else {
                Conecta4.jugar(jugador1, jugador2, tablero);
            }

            if (tablero.hayEmpate()) {
                draws++;
            } else if (tablero.ganaJ1()) {
                loses++;
            } else if (tablero.ganaJ2()) {
                wins++;
            }

            // Cambiar el primer jugador
            firstNew = !firstNew;
        }

        return new int[] { wins, draws, loses };
    }

    public boolean newerBetter(int wins, int draws, int loses) {
        /*
         * Simplemente comprobamos que haya ganado más partidas que las que ha perdido.
         * Los empates no cuentan para esta métrica.
         * No es mayor o igual porque queremos que mejore.
         */
        return wins > loses;
    }

}
