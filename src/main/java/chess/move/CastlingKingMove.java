package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import chess.misc.Position;
import chess.piece.CastlingKing;
import chess.piece.King;
import chess.piece.basepiece.Piece;

public class CastlingKingMove extends NormalMove {
    public CastlingKingMove (Position origin, Position destination, Board board) {
        super(origin, destination, board);
    }

    @Override
    public int getNewHash (int oldHash, BoardHasher hasher) {
        oldHash ^= hasher.getPartHash(origin, pieceInOrigin);
        oldHash ^= hasher.getPartHash(destination, pieceInDestination);
        oldHash ^= hasher.getPartHash(destination, new King(color));

        return oldHash;
    }

    @Override
    public int getOldHash (int newHash, BoardHasher hasher) {
        newHash ^= hasher.getPartHash(destination, new King(color));
        newHash ^= hasher.getPartHash(destination, pieceInDestination);
        newHash ^= hasher.getPartHash(origin, pieceInOrigin);

        return newHash;
    }

    @Override
    public void makeMove (Piece[][] buffer) {
        super.makeMove(buffer);
        buffer[getDestination().getY()][getDestination().getX()] = new King(color);
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        super.unmakeMove(buffer);
        buffer[getOrigin().getY()][getOrigin().getX()] = new CastlingKing(color);
    }
}
