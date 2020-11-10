package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

import java.util.Set;

public class Queen extends Piece {
    private Rook rook;
    private Bishop bishop;

    public Queen (PieceColor color) {
        super(PieceType.QUEEN, color, "♛", 900);
         rook = new Rook(color);
         bishop = new Bishop(color);
    }

    @Override
    public Pair<Set<Move>, Set<Move>> getPossibleMoves(Board board, Position position, Move lastMove) {
        Pair<Set<Move>, Set<Move>> moves = rook.getPossibleMoves(board, position, lastMove);
        mergePairs(moves, bishop.getPossibleMoves(board, position, lastMove));

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
