package players.treeai;

import chess.board.Board;
import chess.move.Move;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.max;

public class TreeAIWorker extends Thread {
    private volatile static AtomicDouble globalAlpha = new AtomicDouble();
    private volatile static AtomicDouble globalBeta = new AtomicDouble();

    public static void resetAlphaAndBeta () {
        globalAlpha.set(-1e23);
        globalBeta.set(1e23);
    }


    private Set<Move> moves;
    private Board board;
    private int id;
    private int depth;
    private BoardEvaluator evaluator;
    private Map<Move, Double> result;

    @Override
    public String toString() {
        return "TreeAIWorker{" +
                "moves=" + moves +
                ", id=" + id +
                ", ready=" + ready +
                ", result=" + result +
                '}';
    }

    private boolean ready = false;


    public TreeAIWorker(Set<Move> moves, Board board, int id, int depth, BoardEvaluator evaluator) {
        super();

        this.moves = moves;
        this.board = board;
        this.id = id;
        this.depth = depth;
        this.evaluator = evaluator;
        result = new HashMap<>();
    }

    @Override
    public void run() {
        for (Move move : moves) {
            board.executeMoveNoChecks(move);
            double value = -deepEvaluateBoard(board);
            board.unMakeMove(1);

            result.put(move, value);
        }

        ready = true;
    }
    double deepEvaluateBoard(Board board) {
        return deepEvaluateBoard(board, depth, 0, -1e23, 1e23);
    }

    private double deepEvaluateBoard(Board board, int currentDepth, int absoluteLimit, double α, double β) {
        if (board.isCheckmate() || board.isDraw() || currentDepth <= 0) {
            return evaluator.evaluateBoard(board, currentDepth);
//            return 0;
        } else {
            double totalPositionValue = -1e22;
            for (Move move : board.getAllPossibleMoves()) {
                board.executeMoveNoChecks(move);
                totalPositionValue = max(-deepEvaluateBoard(board, currentDepth - 1, absoluteLimit, -β, -α), totalPositionValue);
                board.unMakeMove(1);

                α = max(α, totalPositionValue);
                if (α >= β) {
                    break;
                }

//                globalAlpha.set
            }

            return totalPositionValue;
        }
    }


    public boolean isReady() {
        return ready;
    }

    public Map<Move, Double> getResult() {
        return result;
    }
}
