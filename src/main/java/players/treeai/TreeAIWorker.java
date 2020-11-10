package players.treeai;

import chess.board.Board;
import chess.move.Move;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TreeAIWorker extends Thread {
    private volatile static AtomicDouble globalAlpha = new AtomicDouble();
    private volatile static AtomicDouble globalBeta = new AtomicDouble();

    public static final Object lock = new Object();

    public static void resetAlphaAndBeta () {
        globalAlpha.set(-1e23);
        globalBeta.set(1e23);
    }


    private Set<Move> moves;
    private Board board;
    private int id;
    private int depth;
    private BoardEvaluator evaluator;
    private ConcurrentHashMap<Integer, TranspositionTableEntry> transpositionTable;
    private Map<Move, Double> result;
    private boolean run;

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


    public TreeAIWorker(Set<Move> moves, Board board, int id, int depth, BoardEvaluator evaluator, ConcurrentHashMap<Integer, TranspositionTableEntry> transpositionTable) {
        super();

        this.moves = moves;
        this.board = board;
        this.id = id;
        this.depth = depth;
        this.evaluator = evaluator;
        this.transpositionTable = transpositionTable;
        result = new HashMap<>();
        this.run = true;
    }

    @Override
    public void run() {
        for (Move move : moves) {
            board.executeMoveNoChecks(move);
            double value = -deepEvaluateBoard(board);
            board.unMakeMove(1);

            result.put(move, value);
            if (!run) break;
        }
        if (run)
            ready = true;
            synchronized (lock) {
                lock.notifyAll();
            }
    }
    double deepEvaluateBoard(Board board) {
        return deepEvaluateBoard(board, depth, 0, -1e23, 1e23);
    }

    public void _stop () {
        run = false;
    }

    private double deepEvaluateBoard(Board board, int currentDepth, int absoluteLimit, double alpha, double beta) {
        double alphaOrig = alpha;

        TranspositionTableEntry entry = transpositionTable.get(board.hashCode());
        if (entry != null && entry.getDepth() >= currentDepth) {
            if (entry.getNodeType() == TranspositionTableEntry.NODE_EXACT) {
                return entry.getPositionValue();
            } else if (entry.getNodeType() == TranspositionTableEntry.NODE_LOWER) {
                alpha = max(alpha, entry.getPositionValue());
            } else if (entry.getNodeType() == TranspositionTableEntry.NODE_UPPER) {
                beta = min(beta, entry.getPositionValue());
            }

            if (alpha >= beta) {
                return entry.getPositionValue();
            }
        }

        if (board.isCheckmate() || board.isDraw() || currentDepth <= 0) {
            return evaluator.evaluateBoard(board, currentDepth);
        } else {
            double totalPositionValue = -1e22;
            for (Move move : board.getAllPossibleMoves()) {
                board.executeMoveNoChecks(move);
                totalPositionValue = max(-deepEvaluateBoard(board, currentDepth - 1, absoluteLimit, -beta, -alpha), totalPositionValue);
                board.unMakeMove(1);

                alpha = max(alpha, totalPositionValue);
                if (alpha >= beta) {
                    break;
                }
            }

            int nodeType;
            if (totalPositionValue <= alphaOrig) {
                nodeType = TranspositionTableEntry.NODE_UPPER;
            } else if (totalPositionValue >= beta) {
                nodeType = TranspositionTableEntry.NODE_LOWER;
            } else {
                nodeType = TranspositionTableEntry.NODE_EXACT;
            }

            var newEntry = new TranspositionTableEntry(totalPositionValue, nodeType, currentDepth);
            transpositionTable.put(board.hashCode(), newEntry);

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
