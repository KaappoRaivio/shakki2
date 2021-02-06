package chess.piece.basepiece;

import chess.board.Board;
import misc.Position;
import misc.exceptions.ChessException;
import chess.move.Move;
import chess.move.NormalMove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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


    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove) {
        return getPossibleMoves(board, position, lastMove, false);
    }

//    abstract public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture, boolean barrier);

    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        return getPossibleMoves(board, position, lastMove, includeSelfCapture, null);
    }

    abstract public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture, Position barrier);



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
//        return Objects.hash(type, color);
        return type.getOrdinal() + color.getOrdinal();
    }

    public final double getValue (Position position) {
        return getValue(position.getX(), position.getY());
    }

    public final double getValue (int x, int y) {
//        System.out.println(value);
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

    public static int getForwardDirection (PieceColor color) {
        switch (color) {
            case WHITE:
                return 1;
            case BLACK:
                return -1;
            default:
                throw new ChessException("Not applicable to NoColor");
        }
    }

    private Set<Position> getStraightPath (Board board, Position position, int deltaX, int deltaY, boolean includeSelfCapture, Position barrier) {
        return getStraightPath(board, position.getX(), position.getY(), deltaX, deltaY, includeSelfCapture, barrier);
    }
    private Set<Position> getStraightPath (Board board, int x, int y, int deltaX, int deltaY, boolean includeSelfCapture, Position barrier) {
        Set<Position> moves = new HashSet<>();

        PieceColor ownColor = board.getPieceInSquare(x, y).getColor();

        x += deltaX;
        y += deltaY;

        while (x >= 0 && x < 8 && y >= 0 && y < 8) {

            PieceColor currentPieceColor = board.getPieceInSquare(x, y).getColor();
            if (includeSelfCapture) {
                if (currentPieceColor != PieceColor.NO_COLOR || (barrier != null && x == barrier.getX() && y == barrier.getY())) {
                    moves.add(new Position(x, y));
                    break;
                } else {
                    moves.add(new Position(x, y));
                }
            } else {
                if (currentPieceColor == ownColor || (barrier != null && x == barrier.getX() && y == barrier.getY())) {
                    break;
                } else if (currentPieceColor == ownColor.invert()) {
                    moves.add(new Position(x, y));
                    break;
                } else {
                    moves.add(new Position(x, y));
                }
            }

            x += deltaX;
            y += deltaY;
        }

        return moves;
    }
    protected List<Move> getStraightPathMoves(Board board, Position position, int deltaX, int deltaY, boolean includeSelfCapture, Position barrier) {

        List<Move> moves = new ArrayList<>();

        for (Position destination : getStraightPath(board, position, deltaX, deltaY, includeSelfCapture, barrier)) {
//            moves.add(Move.parseMove(position.toString() + destination.toString(), board.getPieceInSquare(position).getColor(), board));


            NormalMove move = new NormalMove(position, destination, board);
//            moves.getSecond().add(move);
//            if (!move.isSelfCapture()) {
                moves.add(move);
//            }
        }

        return  moves;
    }
    protected Set<Move> getMovesFromOffsets(List<Position> offsets, Board board, Position position, boolean includeSelfCapture) {
        Set<Move> moves = new HashSet<>();

        for (Position offset : offsets) {
            Position destination = position.offset(offset, false);
            if (destination.verify() && (board.getPieceInSquare(destination).getColor() != this.color || (includeSelfCapture && board.getPieceInSquare(destination).getColor() != PieceColor.NO_COLOR))) {
                NormalMove move = new NormalMove(position, destination, board);
                moves.add(move);
            }
        }
        return moves;
    }

    public String conventionalToString () {
        if (color == PieceColor.BLACK) return symbol;
        else {
            switch (type) {
                case KING:
                    return "♔";
                case ROOK:
                    return "♖";
                case BISHOP:
                    return "♗";
                case PAWN:
                    return "♙";
                case QUEEN:
                    return "♕";
                case KNIGHT:
                    return "♘";
                case NO_PIECE:
                    return ".";
                default:
                    throw new RuntimeException("Unknown type!");
            }
        }
    }
}
