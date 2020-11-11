package players.treeai;

import chess.board.Board;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TreeAIWorker extends Thread {
    public static final Object lock = new Object();


    private List<Move> moves;
    private Board board;
    private int id;
    private int depth;
    private BoardEvaluator evaluator;
    private ConcurrentHashMap<Integer, TranspositionTableEntry> transpositionTable;
    private Map<Move, Double> result;
    public Map<Move, List<Move>> moveHistorys = new HashMap<>();
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


    public TreeAIWorker(List<Move> moves, Board board, int id, int depth, BoardEvaluator evaluator, ConcurrentHashMap<Integer, TranspositionTableEntry> transpositionTable) {
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
            double value = deepEvaluateBoard(board, move);
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
    double deepEvaluateBoard(Board board, Move initialMove) {
        return deepEvaluateBoard(board, depth, 0, -1e23, 1e23, initialMove);
    }

    public void _stop () {
        run = false;
    }

    private double deepEvaluateBoard(Board board, int currentDepth, int absoluteLimit, double alpha, double beta, Move initialMove) {
//        double alphaOrig = alpha;
//        TranspositionTableEntry entry = transpositionTable.get(board.hashCode());
//        if (entry != null && entry.getDepth() >= currentDepth) {
//            if (entry.getNodeType() == TranspositionTableEntry.NODE_EXACT) {
//                return entry.getPositionValue();
//            } else if (entry.getNodeType() == TranspositionTableEntry.NODE_LOWER) {
//                alpha = max(alpha, entry.getPositionValue());
//            } else if (entry.getNodeType() == TranspositionTableEntry.NODE_UPPER) {
//                beta = min(beta, entry.getPositionValue());
//            }
//
//            if (alpha >= beta) {
//                return entry.getPositionValue();
//            }
//        }

        if (board.isCheckmate() || board.isDraw() || currentDepth <= 0) {
            moveHistorys.put(initialMove, List.copyOf(board.getMoveHistory()));
            return evaluator.evaluateBoard(board, currentDepth);
        } else {
            double totalPositionValue = -1e22;
            List<Move> allPossibleMoves = board.getAllPossibleMoves();
            sortMoves(allPossibleMoves, board, currentDepth, evaluator);
            for (Move move : allPossibleMoves) {
                board.executeMoveNoChecks(move);
                totalPositionValue = max(-deepEvaluateBoard(board, currentDepth - 1, absoluteLimit, -beta, -alpha, initialMove), totalPositionValue);
                board.unMakeMove(1);

                alpha = max(alpha, totalPositionValue);
                if (alpha >= beta) {
                    break;
                }
            }

//            int nodeType;
//            if (totalPositionValue <= alphaOrig) {
//                nodeType = TranspositionTableEntry.NODE_UPPER;
//            } else if (totalPositionValue >= beta) {
//                nodeType = TranspositionTableEntry.NODE_LOWER;
//            } else {
//                nodeType = TranspositionTableEntry.NODE_EXACT;
//            }
//
//            var newEntry = new TranspositionTableEntry(totalPositionValue, nodeType, currentDepth);
//            transpositionTable.put(board.hashCode(), newEntry);

            return totalPositionValue;
        }
    }

    private static void sortMoves(List<Move> allPossibleMoves, Board board, int currentDepth, BoardEvaluator evaluator) {
        allPossibleMoves.sort((a, b) -> {
            board.executeMoveNoChecks(a);
            double score1 = evaluator.evaluateBoard(board, currentDepth);
            board.unMakeMove(1);
            board.executeMoveNoChecks(b);
            double score2 = evaluator.evaluateBoard(board, currentDepth);
            board.unMakeMove(1);
            return (int) (score1 - score2);
        });
    }


    public boolean isReady() {
        return ready;
    }

    public Map<Move, Double> getResult() {
//        System.out.println(moveHistorys.keySet().stream().map(key -> key + ": " + moveHistorys.get(key)).collect(Collectors.joining("\n")));
        return result;
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("r3kb1r/1bpq1ppp/p3pn2/1p4B1/2pPP3/P1N5/1P3PPP/R2QKB1R w KQkq - 0 11");

        var a = board.getAllPossibleMoves();
        sortMoves(a, board, 4, new BoardEvaluator(3, PieceColor.WHITE));
        System.out.println(board);
        System.out.println(a);
    }
}
