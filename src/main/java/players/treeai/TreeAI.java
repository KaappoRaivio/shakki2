package players.treeai;

import chess.board.Board;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import players.Player;
import runner.CapableOfPlaying;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;

public class TreeAI implements CapableOfPlaying {
    private PieceColor color;
    private Board board;
    private int depth;
    private BoardEvaluator evaluator;

    public TreeAI(PieceColor color, Board board, int depth) {
        this.color = color;
        this.board = board;
        this.depth = depth - 1;
        evaluator = new BoardEvaluator(depth);
    }

    @Override
    public Move getMove() {
        Map<Move, Double> values = new HashMap<>();

        for (Move move : board.getAllPossibleMoves(color)) {
            board.executeMoveNoChecks(move);
            values.put(move, -deepEvaluateBoard(board));
            board.unMakeMove(1);
        }

        System.out.println(values);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return values
                .entrySet()
                .stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow()
                .getKey();
    }

    private double deepEvaluateBoard(Board board) {
        return deepEvaluateBoard(board, depth, 0, -1e23, 1e23);
    }

    private double deepEvaluateBoard(Board board, int currentDepth, int absoluteLimit, double alpha, double beta) {
        if (board.isCheck() && currentDepth == 0 && absoluteLimit == 0) {
//            currentDepth += 2;
//            absoluteLimit += 2;
//            System.out.println("Adding" + ", " + absoluteLimit);
        } else {
//            System.out.println("not adding");
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

            return totalPositionValue;
        }
    }

    @Override
    public void updateValues(Board board, PieceColor turn, int moveCount) {
        this.board = board;
    }

    @Override
    public PieceColor getColor() {
        return color;
    }

    public static void main(String[] args) {
        //        Board board = Board.getStartingPosition();
//        BoardEvaluator evaluator = new BoardEvaluator(4);
//        Board board = Board.fromFEN("1k1R4/8/1K6/8/8/8/8/8 b - - 0 1");
        Board board = Board.fromFEN("k7/8/2K5/8/8/4Q3/8/8 w - - 0 1");
        System.out.println(board);
        TreeAI ai = new TreeAI(PieceColor.WHITE, board, 3);
        ai.updateValues(board, PieceColor.WHITE, 30);
        Move move = ai.getMove();
        System.out.println(move);
        board.makeMove(move);

        System.out.println(board);
//        System.out.println(evaluator.evaluateBoard(board));

    }
}
