package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import misc.Position;
import misc.exceptions.ChessException;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

public class NoMove extends Move {
    public static final NoMove NO_MOVE = new NoMove();

    private NoMove () {}

    @Override
    public void makeMove (Piece[][] buffer) {
        throw new ChessException("Not applicable for NoMove!");
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        throw new ChessException("Not applicable for NoMove!");
    }

    @Override
    public boolean resetsFiftyMoveRule () {
        return false;
    }

    @Override
    public boolean affectsKingPosition () {
        return false;
    }

    @Override
    public boolean capturesKing() {
        return false;
    }

    @Override
    public boolean isCapturingMove() {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public boolean isSelfCapture() {
        return false;
    }

    @Override
    public Pair<PieceColor, Position> getNewKingPosition () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public Position getOrigin () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public Position getDestination() {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public PieceColor getColor () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public Piece getPiece() {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public int getIncrementalHash(int oldHash, BoardHasher hasher) {
        return oldHash;
    }

    @Override
    public String getShortAlgebraicNotation(Board board) {
        return toString();
    }

    @Override
    public String toString () {
        return "...";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoMove;
    }
}
