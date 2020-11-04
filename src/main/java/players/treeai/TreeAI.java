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

public class TreeAI implements CapableOfPlaying {
    private PieceColor color;
    private Board board;
    private int depth;

    public TreeAI(PieceColor color, Board board, int depth) {
        this.color = color;
        this.board = board;
        this.depth = depth;
    }

    @Override
    public Move getMove() {
        Map<Board, Double> values = new HashMap<>();

        for (Move move : board.getAllPossibleMoves(color)) {
            board.executeMoveNoChecks(move);
            values.put(board.deepCopy(), deepEvaluateBoard(board, board.getTurn() == color, depth));
            board.unMakeMove(1);
        }

        System.out.println(values);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return values.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey().getLastMove();
    }

    private double deepEvaluateBoard(Board board, boolean maximizingPlayer, int currentDepth) {
        if (board.isCheckmate() || board.isDraw() || currentDepth == 0) {
            return evaluateBoard(board);
        } else {
            if (maximizingPlayer) {
                double totalPositionValue = -1e9;

                for (Move move : board.getAllPossibleMoves()) {
                    board.executeMoveNoChecks(move);
                    totalPositionValue = Math.max(deepEvaluateBoard(board, false, currentDepth - 1), totalPositionValue);
                    board.unMakeMove(1);
                }

                return totalPositionValue;
            } else {
                double totalPositionValue = 1e9;

                for (Move move : board.getAllPossibleMoves()) {
                    board.executeMoveNoChecks(move);
                    totalPositionValue = Math.min(deepEvaluateBoard(board, true, currentDepth - 1), totalPositionValue);
                    board.unMakeMove(1);
                }

                return totalPositionValue;
            }
        }
    }

    private double evaluateBoard(Board board) {
        double totalValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPieceInSquare(x, y);
                totalValue += piece.getColor() == color ? piece.getValue(null) : -piece.getValue(null);
            }
        }
        return totalValue;
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
        Board board = Board.fromFEN("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPP1/RNBQKB1N w - - 0 1");
        System.out.println(new TreeAI(PieceColor.WHITE, board, 3).evaluateBoard(board));
    }
}
