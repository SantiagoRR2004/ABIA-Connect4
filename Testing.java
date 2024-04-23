import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Testing {
    static Random random = new Random();

    static String filename1 = "pesos.txt";
    static String filename2 = "pesos.txt";

    static float[] pesos1 = Archivo.obtenerPesos(filename1);
    static float[] pesos2 = Archivo.obtenerPesos(filename2);

    private static MyFunctionalInterface[] _heuristicas1 = new MyFunctionalInterface[pesos1.length];
    private static MyFunctionalInterface[] _heuristicas2 = new MyFunctionalInterface[pesos2.length];

    static {
        for (int i = 0; i < pesos1.length; i++) {
            _heuristicas1[i] = Tablero.HEURISTICAS[i];
        }
        for (int i = 0; i < pesos2.length; i++) {
            _heuristicas2[i] = Tablero.HEURISTICAS[i];
        }
    }

    static int numberOfGames = 10;

    static int[] listOfDepth = { 1, 2, 3, 4, 5 };

    public static void main(String[] args) {
        testDepth();

    }

    public static void testDepth() {
        Jugador jugadoPesos1 = new Jugador(1);
        Jugador jugadorPesos2 = new Jugador(1);
        Jugador jugadorRandom = new Jugador(1);
        Jugador oponente = new Jugador(2);

        for (int depth : listOfDepth) {
            AtomicInteger wins1 = new AtomicInteger(0), draws1 = new AtomicInteger(0), loses1 = new AtomicInteger(0),
                    wins2 = new AtomicInteger(0), draws2 = new AtomicInteger(0), loses2 = new AtomicInteger(0),
                    wins3 = new AtomicInteger(0), draws3 = new AtomicInteger(0), loses3 = new AtomicInteger(0);
            AtomicInteger time1 = new AtomicInteger(0), time2 = new AtomicInteger(0), time3 = new AtomicInteger(0);
            AtomicInteger nodes1 = new AtomicInteger(0), nodes2 = new AtomicInteger(0), nodes3 = new AtomicInteger(0);

            jugadoPesos1.establecerEstrategia(
                    new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos1, _heuristicas1), depth));
            jugadorPesos2.establecerEstrategia(
                    new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos2, _heuristicas2), depth));
            jugadorRandom.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorAleatorio(), depth));
            oponente.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorAleatorio(), depth));

            GameSimulationThread thread1 = new GameSimulationThread(jugadoPesos1, oponente, numberOfGames, draws1,
                    wins1, loses1, time1, nodes1);
            GameSimulationThread thread2 = new GameSimulationThread(jugadorPesos2, oponente, numberOfGames, draws2,
                    wins2, loses2, time2, nodes2);
            GameSimulationThread thread3 = new GameSimulationThread(jugadorRandom, oponente, numberOfGames, draws3,
                    wins3, loses3, time3, nodes3);

            thread1.start();
            thread2.start();
            thread3.start();

            try {
                thread1.join();
                thread2.join();
                thread3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Depth: %-2d %-10s Wins: %-3d Draws: %-3d Loses: %-3d Time: %-10d Nodes: %d\n", depth, "Poderado1",
                    wins1.get(), draws1.get(), loses1.get(), time1.get()/numberOfGames, nodes1.get()/numberOfGames);
            System.out.printf("Depth: %-2d %-10s Wins: %-3d Draws: %-3d Loses: %-3d Time: %-10d Nodes: %d\n", depth, "Poderado2",
                    wins2.get(), draws2.get(), loses2.get(), time2.get()/numberOfGames, nodes2.get()/numberOfGames);
            System.out.printf("Depth: %-2d %-10s Wins: %-3d Draws: %-3d Loses: %-3d Time: %-10d Nodes: %d\n", depth, "Random",
                    wins3.get(), draws3.get(), loses3.get(), time3.get()/numberOfGames, nodes3.get()/numberOfGames);
        }
    }

    public void testMiniMaxVSAlphaBeta() {

    }
}
