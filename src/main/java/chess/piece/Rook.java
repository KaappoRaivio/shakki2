package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.HashSet;
import java.util.Set;

public class Rook extends Piece {
    public Rook (PieceColor color) {
        this(color, color == PieceColor.WHITE ? "♖" : "♜");
    }

    Rook (PieceColor color, String symbol) {
        super(PieceType.ROOK, color, symbol, 500);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        Set<Move> moves = new HashSet<>();
        
        moves.addAll(getStraightPathMoves(board, position, 0, 1, includeSelfCapture));
        moves.addAll(getStraightPathMoves(board, position, 0, -1, includeSelfCapture));
        moves.addAll(getStraightPathMoves(board, position, 1, 0, includeSelfCapture));
        moves.addAll(getStraightPathMoves(board, position, -1, 0, includeSelfCapture));

        return moves;
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {-0.33, -0.33, -0.33, 0.33, 0.33, -0.33, -0.33, -0.33},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {0.33, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.33},
                {-0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33},

        };
    }
}
