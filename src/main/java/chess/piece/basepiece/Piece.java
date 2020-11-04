package chess.piece.basepiece;

import chess.board.Board;
import chess.misc.Position;
import chess.misc.exceptions.ChessException;
import chess.move.Move;
import chess.move.NormalMove;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

abstract public class Piece implements Serializable {
    final private PieceType type;
    final protected PieceColor color;
    final private String symbol;
    final private int value;

    protected Piece (PieceType type, PieceColor color, String symbol, int value) {
        if (color == PieceColor.NO_COLOR ^ type == PieceType.NO_PIECE) {
            throw new ChessException("The combination " + type + " with color " + color + " is not valid. NO_PIECE and NO_COLOR must be used together!");
        }

        this.type = type;
        this.color = color;
        this.symbol = symbol;
        this.value = value;
    }


    abstract public Set<Move> getPossibleMoves (Board board, Position position, Move lastMove);
    protected abstract double[][] getPieceSquareTable ();

    @Override
    public String toString () {
        return symbol;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return type == piece.type &&
                color == piece.color;
    }


    @Override
    public int hashCode () {
        return Objects.hash(type, color);
    }

//    public int getIndex (Board board, Position position, Move lastMove) {
//        return getColor() == PieceColor.WHITE ? getType().ordinal(): getType().ordinal() + 1;
//    }

    public double getValue (Position position) {
//        switch (color) {
//            case NO_COLOR:
//            case WHITE:
//                return value + value * (getPieceSquareTable()[position.getY()][position.getX()]);
//            case BLACK:
//                return value + value * (getPieceSquareTable()[7 - position.getY()][position.getX()]);
//            default:
//                throw new ChessException("");

//        }
        return value;
    }

    public PieceType getType () {
        return type;
    }

    public PieceColor getColor () {
        return color;
    }

    public int getForwardDirection () {
        switch (color) {
            case WHITE:
                return 1;
            case BLACK:
                return -1;
            default:
                throw new ChessException("Not applicable! " + toString());
        }
    }

    private Set<Position> getStraightPath (Board board, Position position, int deltaX, int deltaY) {
        return getStraightPath(board, position.getX(), position.getY(), deltaX, deltaY);
    }

    private Set<Position> getStraightPath (Board board, int x, int y, int deltaX, int deltaY) {
        Set<Position> moves = new HashSet<>();

        PieceColor ownColor = board.getPieceInSquare(x, y).getColor();

        x += deltaX;
        y += deltaY;

        while (x >= 0 && x < 8 && y >= 0 && y < 8) {

            PieceColor currentPieceColor = board.getPieceInSquare(x, y).getColor();
            if (currentPieceColor == ownColor) {
                break;
            } else if (currentPieceColor == ownColor.invert()) {
                moves.add(new Position(x, y));
                break;
            } else {
                moves.add(new Position(x, y));
            }

            x += deltaX;
            y += deltaY;
        }

        return moves;
    }

    protected Set<Move> getStraightPathMoves (Board board, Position position, int deltaX, int deltaY) {
        Set<Move> moves = new HashSet<>();

        for (Position destination : getStraightPath(board, position, deltaX, deltaY)) {
//            moves.add(Move.parseMove(position.toString() + destination.toString(), board.getPieceInSquare(position).getColor(), board));
            moves.add(new NormalMove(position, destination, board));
        }

        return  moves;
    }
}
