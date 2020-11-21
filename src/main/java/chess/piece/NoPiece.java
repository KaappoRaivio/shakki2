package chess.piece;

import chess.board.Board;
import misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.Collections;
import java.util.Set;

public class NoPiece extends Piece {
    public static final NoPiece NO_PIECE = new NoPiece();

    private NoPiece () {
        super(PieceType.NO_PIECE, PieceColor.NO_COLOR, " ", 0);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        return Collections.emptySet();
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[8][8];
    }

    @Override
    public boolean equals (Object o) {
        try {
            return o.getClass().equals(getClass());
        } catch (NullPointerException e) {
            return false;
        }
    }

}
