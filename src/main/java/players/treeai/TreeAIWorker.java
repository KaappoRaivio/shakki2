package players.treeai;

import chess.board.Board;
import misc.TermColor;
import misc.exceptions.ChessException;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import misc.Pair;
import org.apache.cayenne.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.*;
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
    private ConcurrentLinkedHashMap<Integer, TranspositionTableEntry> transpositionTable;
    private Map<Move, Double> result;
    public Map<Move, String> moveHistorys = new HashMap<>();
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


    public TreeAIWorker(List<Move> moves, Board board, int id, int depth, BoardEvaluator evaluator, ConcurrentLinkedHashMap<Integer, TranspositionTableEntry> transpositionTable) {
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
            double doubleListPair = deepEvaluateBoard(board, move);
            double value = -doubleListPair;
            board.unMakeMove(1);

            result.put(move, value);
            moveHistorys.put(move, getPrincipalVariation(board.deepCopy(), move));
            if (!run) break;
        }
        if (run)
            ready = true;
//            evaluated = new Pair<>(evaluator.white, evaluator.black);
            synchronized (lock) {
                lock.notifyAll();
            }
    }

    private String getPrincipalVariation(Board board, Move move) {
        List<String> moveHistory = new ArrayList<>();
        String boardFEN = "";
        String evaluation = "not available";
        try {
            while (true) {
    //            System.out.println(move);
                board.makeMove(move);
                moveHistory.add(String.format("%5s", move.toString()));
                TranspositionTableEntry entry = transpositionTable.get(board.hashCode());
                if (entry == null) {
                    boardFEN = String.format("%-60s", board.toFEN());
                    evaluation = String.format("%.2f", evaluator.evaluateBoard(board, 0));
                    break;
                }

                move = entry.getBestMove();
            }
        } catch (ChessException e) {
            System.out.println("Hash collision!");
            System.out.println(board);
            System.out.println(moveHistory);
            System.out.println(move);
            moveHistory.add("Could not go further due to hash collision");
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            System.out.println(transpositionTable.size());

        }

        return moveHistory.subList(0, Math.min(moveHistory.size(), depth + 1)).stream().collect(Collectors.joining(",", "[ ", " ]")) + ", " + TermColor.ANSI_ITALIC.getEscape() +  boardFEN + TermColor.ANSI_RESET.getEscape() + ", " + evaluation;
    }

    double deepEvaluateBoard(Board board, Move initialMove) {
        return deepEvaluateBoard(board, depth, -1e23, 1e23, initialMove);
    }

    public Pair<Integer, Integer> evaluated;

    public void _stop () {
        run = false;
    }

    private double deepEvaluateBoard(Board board, int currentDepth, double alpha, double beta, Move initialMove) {
        double alphaOrig = alpha;
        TranspositionTableEntry entry = transpositionTable.get(board.hashCode());
        if (entry != null && entry.getDepth() >= currentDepth && entry.getColor() == board.getTurn()) {
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
            return quiesce(board, alpha, beta, currentDepth);
        } else {
            double totalPositionValue = -1e22;
            Move bestMove = null;

            List<Move> allPossibleMoves = board.getAllPossibleMoves();
//            sortMoves(allPossibleMoves, board, currentDepth, evaluator);
            for (Move move : allPossibleMoves) {
                board.executeMoveNoChecks(move);
                int prevHash = board.hashCode();
                double value = -deepEvaluateBoard(board, currentDepth - 1, -beta, -alpha, initialMove);

                if (value > totalPositionValue) {
                    bestMove = move;
                    totalPositionValue = value;
                }

                if (board.hashCode() != prevHash) throw new RuntimeException("Hashes don't equal! " + board);
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

            var newEntry = new TranspositionTableEntry(totalPositionValue, nodeType, currentDepth, bestMove, board.getTurn());
            transpositionTable.put(board.hashCode(), newEntry);
            return totalPositionValue;
        }
    }

    private static void sortMoves(List<Move> allPossibleMoves, Board board, int currentDepth, BoardEvaluator evaluator) {
        allPossibleMoves.sort((a, b) -> {
            board.executeMoveNoChecks(a);
            double score1 = evaluator.evaluateBoard(board, currentDepth);
            if (board.isCheckmate() || board.isCheck()) score1 += 10000;
            board.unMakeMove(1);
            board.executeMoveNoChecks(b);
            double score2 = evaluator.evaluateBoard(board, currentDepth);
            if (board.isCheckmate() || board.isCheck()) score2 += 10000;
            board.unMakeMove(1);
            return Double.compare(score1, score2);
        });
    }

    private double quiesce(Board board, double alpha, double beta, int currentDepth) {
        return quiesce(board, alpha, beta, currentDepth, 10);
    }

    private double quiesce (Board board, double alpha, double beta, int currentDepth, int limit) {
        double standingPat = evaluator.evaluateBoard(board, currentDepth);
        return standingPat;
        
        
//        if (standingPat >= beta) {
//            return beta;
//        }
//        if (alpha < standingPat) {
//            alpha = standingPat;
//        }
//
//        if (limit <= 0) {
//            System.out.println("limit exceeded");
//            return standingPat;
//        }
//
//        double score;
//
//        for (Move move : board.getAllPossibleMoves()) {
//            if (!move.isCapturingMove()) {
//                continue;
//            } else {
//                board.executeMoveNoChecks(move);
//                boolean check = board.isCheck();
//                board.unMakeMove(1);
//                if (!check) continue;
//            }
//            board.makeMove(move);
//            score = -quiesce(board, -beta, -alpha, currentDepth, limit - 1);
//            board.unMakeMove(1);
//
//            if (score >= beta) {
//                return beta;
//            }
//            if (score > alpha) {
//                alpha = score;
//            }
//        }
//
//        return alpha;
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
        sortMoves(a, board, 4, new CandinateEvaluator(3, PieceColor.WHITE));
        System.out.println(board);
        System.out.println(a);
    }
}
