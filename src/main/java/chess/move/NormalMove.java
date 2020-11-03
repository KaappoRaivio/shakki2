package chess.move;

import chess.board.Board;
import chess.misc.Position;
import chess.misc.exceptions.ChessException;
import chess.piece.NoPiece;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

;import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class NormalMove implements Move {
    final private Position origin;
    final private Position destination;
    final private Piece pieceInOrigin;
    final private Piece pieceInDestination;
    final protected PieceColor color;

    public NormalMove (Position origin, Position destination, Board board) {
        this.origin = origin;
        this.destination = destination;
        pieceInOrigin = board.getPieceInSquare(origin);
        pieceInDestination = board.getPieceInSquare(destination);

        this.color = board.getPieceInSquare(origin).getColor();
    }

    @Override
    public void makeMove (Piece[][] buffer) {
        buffer[destination.getY()][destination.getX()] = buffer[origin.getY()][origin.getX()];
        buffer[origin.getY()][origin.getX()] = new NoPiece();
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        buffer[origin.getY()][origin.getX()] = buffer[destination.getY()][destination.getX()];
        buffer[destination.getY()][destination.getX()] = pieceInDestination;

    }

    @Override
    public boolean resetsFiftyMoveRule () {
        return pieceInOrigin.getType() == PieceType.PAWN || pieceInDestination.getType() != PieceType.NO_PIECE;
    }

    @Override
    public boolean affectsKingPosition () {
        return pieceInOrigin.getType() == PieceType.KING;
    }

    @Override
    public boolean capturesKing() {
        return pieceInDestination.getType() == PieceType.KING;
    }

    @Override
    public Pair<PieceColor, Position> getNewKingPosition () {
        if (affectsKingPosition()) {
            return new Pair<>(color, destination);
        } else {
            throw new ChessException("Doesn't affect king position!");
        }
    }

    @Override
    public Position getOrigin () {
        return origin;
    }

    @Override
    public PieceColor getColor () {
        return color;
    }

    @Override
    public String toString () {
        switch (pieceInOrigin.getType()) {
            case PAWN:
                if (pieceInDestination.getType() != PieceType.NO_PIECE) {
                    return origin.toString().substring(0, 1).toLowerCase() + "x" + destination.toString().toLowerCase();
                } else {
                    return destination.toString().toLowerCase();
                }
            default:
                if (pieceInDestination.getType() != PieceType.NO_PIECE) {
                    return MoveHashMap.moveHashMap.get(pieceInOrigin.getType()).toUpperCase() + "x" + destination.toString().toLowerCase();
                } else {
                    return MoveHashMap.moveHashMap.get(pieceInOrigin.getType()).toUpperCase() + destination.toString().toLowerCase();
                }
        }

    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof NormalMove)) return false;
        NormalMove that = (NormalMove) o;
        return getOrigin().equals(that.getOrigin()) &&
                getDestination().equals(that.getDestination()) &&
                pieceInOrigin.equals(that.pieceInOrigin) &&
                pieceInDestination.equals(that.pieceInDestination) &&
                getColor() == that.getColor();
    }

    @Override
    public int hashCode () {
        return Objects.hash(getOrigin(), getDestination(), pieceInOrigin, pieceInDestination, getColor());
    }

    public Position getDestination () {
        return destination;
    }
}

class MoveHashMap {
    static final Map<PieceType, String> moveHashMap = Map.ofEntries(
            Map.entry(PieceType.KING, "k"),
            Map.entry(PieceType.ROOK, "r"),
            Map.entry(PieceType.KNIGHT, "n"),
            Map.entry(PieceType.QUEEN, "q"),
            Map.entry(PieceType.PAWN, "p"),
            Map.entry(PieceType.BISHOP, "b")
    );
}
