package players.evaluators;

import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import players.BoardEvaluator;
import players.CurrentBestEvaluator;
import xyz.niflheim.stockfish.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Query;
import xyz.niflheim.stockfish.engine.enums.QueryType;
import xyz.niflheim.stockfish.engine.enums.Variant;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

import java.util.concurrent.atomic.AtomicReference;

public class StockfishEvaluator implements BoardEvaluator {
    private final Object lock = new Object();
    private StockfishClient client;

    public StockfishEvaluator() {
        try {
            client = new StockfishClient.Builder()
                    .setInstances(100)
                    .setVariant(Variant.BMI2)
                    .setPath("/home/kaappo/git/shakki2/assets/engines/")
                    .build();
        } catch (StockfishInitException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public double evaluateBoard(Board board, int currentDepth) {
        Query query = new Query.Builder(QueryType.Score)
                .setFen(board.toFEN())
                .build();

        AtomicReference<String> res = new AtomicReference<>("");
        AtomicReference<Boolean> wait = new AtomicReference<>(true);

        client.submit(query, result -> {
            synchronized (lock) {
                res.set(result);
                wait.set(false);

                lock.notifyAll();
            }
        });

        while (wait.get()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return Integer.parseInt(res.get()) / 100.0;
    }

    public static void main(String[] args) {
        CurrentBestEvaluator e2 = new CurrentBestEvaluator(0, PieceColor.WHITE);
        StockfishEvaluator e = new StockfishEvaluator();
        System.out.println(e.evaluateBoard(Board.getStartingPosition(), 0));
        System.out.println(e2.evaluateBoard(Board.getStartingPosition(), 0));
    }
}
