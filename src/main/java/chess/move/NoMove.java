package chess.move;

import chess.board.BoardHasher;
import chess.misc.Position;
import chess.misc.exceptions.ChessException;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

public class NoMove implements Move {
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
    public Pair<PieceColor, Position> getNewKingPosition () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public Position getOrigin () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public PieceColor getColor () {
        throw new ChessException("Not applicable for NoMove");
    }

    @Override
    public int getNewHash(int oldHash, BoardHasher hasher) {
        return oldHash;
    }

    @Override
    public int getOldHash(int newHash, BoardHasher hasher) {
        return newHash;
    }

    @Override
    public String toString () {
        return "NoMove";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NoMove;
    }
}
