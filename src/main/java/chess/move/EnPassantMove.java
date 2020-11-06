package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import chess.misc.Position;
import chess.piece.NoPiece;
import chess.piece.basepiece.Piece;

public class EnPassantMove extends NormalMove {
    private final Position opponentPawnPosition;
    private final Piece opponentPawn;

    public EnPassantMove (Position origin, Position destination, Board board) {
        super(origin, destination, board);

        int offsetX = destination.getX() - origin.getX();
        opponentPawnPosition = origin.offsetX(offsetX);
        opponentPawn = board.getPieceInSquare(opponentPawnPosition);
    }

    @Override
    public void makeMove (Piece[][] buffer) {
        super.makeMove(buffer);



        buffer[opponentPawnPosition.getY()][opponentPawnPosition.getX()] = new NoPiece();
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        super.unmakeMove(buffer);

        int offsetX = getDestination().getX() - getOrigin().getX();
        Position opponentPawnPosition = getOrigin().offsetX(offsetX);

        buffer[opponentPawnPosition.getY()][opponentPawnPosition.getX()] = opponentPawn;
    }

    @Override
    public int getNewHash (int oldHash, BoardHasher hasher) {
        int newHash = super.getNewHash(oldHash, hasher);
        newHash ^= hasher.getPartHash(opponentPawnPosition, opponentPawn);

        return newHash;
    }

    @Override
    public int getOldHash (int newHash, BoardHasher hasher) {
        int oldHash = super.getOldHash(newHash, hasher);
        newHash ^= hasher.getPartHash(opponentPawnPosition, opponentPawn);

        return newHash;
    }
}
