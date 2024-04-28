import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class GameSimulationThread extends Thread {
    private static final Random random = new Random();

    private final AtomicInteger draws;
    private final AtomicInteger wins;
    private final AtomicInteger loses;

    private final AtomicInteger time;
    private final AtomicInteger nodes;
    private final AtomicInteger nMovs;

    private final Jugador player1;
    private final Jugador player2;
    private final int numberOfGames;

    public GameSimulationThread(Jugador player1, Jugador player2, int numberOfGames, AtomicInteger draws,
            AtomicInteger wins, AtomicInteger loses, AtomicInteger time, AtomicInteger nodes, AtomicInteger nMovs) {
        this.player1 = player1;
        this.player2 = player2;
        this.numberOfGames = numberOfGames;
        this.draws = draws;
        this.wins = wins;
        this.loses = loses;
        this.time = time;
        this.nodes = nodes;
        this.nMovs = nMovs;
    }

    @Override
    public void run() {
        for (int i = 0; i < numberOfGames; i++) {
            simulateGame();
        }
    }

    private void simulateGame() {
        Tablero tablero = new Tablero();
        tablero.inicializar();

        if (random.nextBoolean()) {
            Conecta4.jugar(player1, player2, tablero);
        } else {
            Conecta4.jugar(player2, player1, tablero);
        }

        if (tablero.hayEmpate()) {
            draws.incrementAndGet();
        } else if (tablero.ganaJ1()) {
            wins.incrementAndGet();
        } else if (tablero.ganaJ2()) {
            loses.incrementAndGet();
        }

        long[] metricas = player1.getMetricas();
        time.addAndGet((int) metricas[2]);
        nMovs.addAndGet((int) metricas[1]);
        nodes.addAndGet((int) metricas[0]);
    }
}