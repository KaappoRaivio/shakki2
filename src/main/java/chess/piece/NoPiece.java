package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class NoPiece extends Piece {
    public NoPiece () {
        super(PieceType.NO_PIECE, PieceColor.NO_COLOR, " ", 0);
    }

    @Override
    public Pair<Set<Move>, Set<Move>> getPossibleMoves(Board board, Position position, Move lastMove) {
        return new Pair<>(Collections.emptySet(), Collections.emptySet());
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
