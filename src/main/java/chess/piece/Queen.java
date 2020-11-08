package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.Set;
import java.util.function.BiFunction;

public class Queen extends Piece {
    private Rook rook;
    private Bishop bishop;

    public Queen (PieceColor color) {
        super(PieceType.QUEEN, color, color == PieceColor.WHITE ? "♕" : "♛", 900);
         rook = new Rook(color);
         bishop = new Bishop(color);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        Set<Move> moves = rook.getPossibleMoves(board, position, lastMove, includeSelfCapture);
        moves.addAll(bishop.getPossibleMoves(board, position, lastMove, includeSelfCapture));

        return moves;
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {-1.0, -0.2, -0.2, 0.2, 0.2, -0.2, -0.2, -1.0},
                {-0.2, 0.6, 1.0, 0.6, 0.6, 0.6, 0.6, -0.2},
                {-0.2, 1.0, 1.0, 1.0, 1.0, 1.0, 0.6, -0.2},
                {0.6, 0.6, 1.0, 1.0, 1.0, 1.0, 0.6, 0.2},
                {0.2, 0.6, 1.0, 1.0, 1.0, 1.0, 0.6, 0.2},
                {-0.2, 0.6, 1.0, 1.0, 1.0, 1.0, 0.6, -0.2},
                {-0.2, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, -0.2},
                {-1.0, -0.2, -0.2, 0.2, 0.2, -0.2, -0.2, -1.0},
        };
    }
}
