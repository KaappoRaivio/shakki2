package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.Set;

public class Queen extends Piece {
    public Queen (PieceColor color) {
        super(PieceType.QUEEN, color, color == PieceColor.WHITE ? "♕" : "♛", 900);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        Set<Move> moves = new Rook(color).getPossibleMoves(board, position, lastMove, includeSelfCapture);
        moves.addAll(new Bishop(color).getPossibleMoves(board, position, lastMove, includeSelfCapture));

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
