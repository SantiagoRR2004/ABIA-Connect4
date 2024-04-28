import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

public class Testing {
    static Random random = new Random();

    static String filename1 = "pesos.txt";
    static String filename2 = "pesosTentativos.txt";

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

    static int maxThreads = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        testDepth();
        testMiniMaxVSAlphaBeta();
    }

    public static void testDepth() {
        Jugador jugadoPesos1 = new Jugador(1);
        Jugador jugadorPesos2 = new Jugador(1);
        Jugador jugadorRandom = new Jugador(1);
        Jugador oponente = new Jugador(2);

        int[] threadsByType = divideNumber(Testing.maxThreads, 3);

        for (int depth : listOfDepth) {
            AtomicInteger wins1 = new AtomicInteger(0), draws1 = new AtomicInteger(0), loses1 = new AtomicInteger(0),
                    wins2 = new AtomicInteger(0), draws2 = new AtomicInteger(0), loses2 = new AtomicInteger(0),
                    wins3 = new AtomicInteger(0), draws3 = new AtomicInteger(0), loses3 = new AtomicInteger(0);
            AtomicInteger time1 = new AtomicInteger(0), time2 = new AtomicInteger(0), time3 = new AtomicInteger(0);
            AtomicInteger nodes1 = new AtomicInteger(0), nodes2 = new AtomicInteger(0), nodes3 = new AtomicInteger(0);
            AtomicInteger nMovs1 = new AtomicInteger(0), nMovs2 = new AtomicInteger(0), nMovs3 = new AtomicInteger(0);

            List<Thread> threadList = new ArrayList<Thread>(Testing.maxThreads);

            jugadoPesos1.establecerEstrategia(
                    new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos1, _heuristicas1), depth));
            jugadorPesos2.establecerEstrategia(
                    new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos2, _heuristicas2), depth));
            jugadorRandom.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorAleatorio(), depth));
            oponente.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorAleatorio(), depth));

            for (int j : divideNumber(numberOfGames, threadsByType[0])) {
                threadList.add(new GameSimulationThread(jugadoPesos1, oponente, j, draws1, wins1, loses1,
                        time1, nodes1, nMovs1));
            }
            for (int j : divideNumber(numberOfGames, threadsByType[1])) {
                threadList.add(new GameSimulationThread(jugadorPesos2, oponente, j, draws2, wins2, loses2,
                        time2, nodes2, nMovs2));
            }
            for (int j : divideNumber(numberOfGames, threadsByType[2])) {
                threadList.add(new GameSimulationThread(jugadorRandom, oponente, j, draws3, wins3, loses3,
                        time3, nodes3, nMovs3));
            }

            for (Thread t : threadList) {
                t.start();
            }

            for (Thread t : threadList) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            showResults(depth, filename1, wins1, draws1, loses1, time1, nodes1, nMovs1);
            showResults(depth, filename2, wins2, draws2, loses2, time2, nodes2, nMovs2);
            showResults(depth, "Random", wins3, draws3, loses3, time3, nodes3, nMovs3);

        }
    }

    public static void testMiniMaxVSAlphaBeta() {

        Jugador jugadorMM = new Jugador(1);
        Jugador jugadorAB = new Jugador(1);
        Jugador oponente = new Jugador(2);

        int[] threadsByType = divideNumber(Testing.maxThreads, 2);

        for (int depth : listOfDepth) {

            AtomicInteger wins1 = new AtomicInteger(0), draws1 = new AtomicInteger(0), loses1 = new AtomicInteger(0),
                    wins2 = new AtomicInteger(0), draws2 = new AtomicInteger(0), loses2 = new AtomicInteger(0);
            AtomicInteger time1 = new AtomicInteger(0), time2 = new AtomicInteger(0);
            AtomicInteger nodes1 = new AtomicInteger(0), nodes2 = new AtomicInteger(0);
            AtomicInteger nMovs1 = new AtomicInteger(0), nMovs2 = new AtomicInteger(0);

            List<Thread> threadList = new ArrayList<Thread>(Testing.maxThreads);

            jugadorMM.establecerEstrategia(
                    new EstrategiaMiniMax(new EvaluadorPonderado(pesos1, _heuristicas1), depth));
            jugadorAB.establecerEstrategia(
                    new EstrategiaAlphaBeta(new EvaluadorPonderado(pesos1, _heuristicas1), depth));
            oponente.establecerEstrategia(new EstrategiaAlphaBeta(new EvaluadorAleatorio(), depth));

            for (int j : divideNumber(numberOfGames, threadsByType[0])) {
                threadList.add(new GameSimulationThread(jugadorMM, oponente, j, draws1, wins1, loses1,
                        time1, nodes1, nMovs1));
            }
            for (int j : divideNumber(numberOfGames, threadsByType[1])) {
                threadList.add(new GameSimulationThread(jugadorAB, oponente, j, draws2, wins2, loses2,
                        time2, nodes2, nMovs2));
            }

            for (Thread t : threadList) {
                t.start();
            }

            for (Thread t : threadList) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            showResults(depth,"MiniMax", wins1, draws1, loses1, time1, nodes1, nMovs1);
            showResults(depth,"AlphaBeta", wins2, draws2, loses2, time2, nodes2, nMovs2);
        }
    }

    public static int[] divideNumber(int number, int divisor) {
        int[] result = new int[divisor];
        int quotient = number / divisor;
        int remainder = number % divisor;

        // Assigning quotient to each integer
        for (int i = 0; i < divisor; i++) {
            result[i] = quotient;
        }

        // Distributing remainder among the integers
        for (int i = 0; i < remainder; i++) {
            result[i]++;
        }

        return result;
    }

    public static void showResults(int depth, String name, AtomicInteger wins, AtomicInteger draws, AtomicInteger loses,
            AtomicInteger time, AtomicInteger nodes, AtomicInteger nMovs) {
        System.out.printf("Depth: %-2d %-20s Wins: %-3d Draws: %-3d Loses: %-3d Time: %-10d Moves: %-10d Nodes: %d\n", depth,
                name,
                wins.get(), draws.get(), loses.get(), time.get(), nMovs.get(), nodes.get());
    }

}
