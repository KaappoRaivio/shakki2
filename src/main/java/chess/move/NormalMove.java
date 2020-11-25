package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import misc.Position;
import misc.exceptions.ChessException;
import chess.piece.NoPiece;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NormalMove extends Move {
    final protected Position origin;
    final protected Position destination;
    final protected Piece pieceInOrigin;
    final protected Piece pieceInDestination;
    final protected PieceColor color;
//    final private Board board;

    public NormalMove (Position origin, Position destination, Board board) {
        Piece pieceInOrigin;
        Piece pieceInDestination;
        this.origin = origin;
        this.destination = destination;
        pieceInOrigin = board.getPieceInSquare(origin);
        pieceInDestination = board.getPieceInSquare(destination);

        PieceColor color = board.getPieceInSquare(origin).getColor();
        if (color == PieceColor.NO_COLOR) {
            System.out.println("Suspicious move generation with no piece at origin! " + board  + ", " + origin);
            color = board.getPieceInSquare(destination).getColor();
            pieceInOrigin = pieceInDestination;
            pieceInDestination = NoPiece.NO_PIECE;
        }
        this.pieceInOrigin = pieceInOrigin;
        this.pieceInDestination = pieceInDestination;
        this.color = color;
//        this.board = board;
    }

    @Override
    public void makeMove (Piece[][] buffer) {
        buffer[destination.getY()][destination.getX()] = buffer[origin.getY()][origin.getX()];
        buffer[origin.getY()][origin.getX()] = NoPiece.NO_PIECE;
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        buffer[origin.getY()][origin.getX()] = buffer[destination.getY()][destination.getX()];
        buffer[destination.getY()][destination.getX()] = pieceInDestination;

    }

    @Override
    public boolean resetsFiftyMoveRule () {
        return pieceInOrigin.getType() == PieceType.PAWN || isCapturingMove();
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
    public boolean isCapturingMove() {
        return pieceInDestination.getType() != PieceType.NO_PIECE;
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
    public Piece getPiece() {
        return pieceInOrigin;
    }

    @Override
    public int getIncrementalHash(int oldHash, BoardHasher hasher) {
        oldHash ^= hasher.getPartHash(origin, pieceInOrigin);
        oldHash ^= hasher.getPartHash(destination, pieceInDestination);
        oldHash ^= hasher.getPartHash(destination, pieceInOrigin);

        return oldHash;
    }

    @Override
    public String getShortAlgebraicNotation(Board board) {
        StringBuilder builder = new StringBuilder();
        board.makeMove(this);
        if (pieceInOrigin.getType() != PieceType.PAWN) {
            builder.append(MoveHashMap.moveHashMap.get(pieceInOrigin.getType()).toUpperCase());

            String clarifierY = "";
            String clarifierX = "";
            Set<Position> brotherPiecePositions = getBrotherPiecePositions(board);
            for (Position location : brotherPiecePositions) {
                if (location.getX() == origin.getX()) {
                    clarifierX = origin.toString().substring(1);
                } else if (location.getY() == origin.getY()) {
                    clarifierY = origin.toString().substring(0, 1);
                } else {
                    clarifierX = origin.toString().substring(0, 1);
                }
            }
            builder.append(clarifierX.toLowerCase()).append(clarifierY.toLowerCase());
        } else {
            if (isCapturingMove()) {
                builder.append(origin.toString().substring(0, 1).toLowerCase());
            }
//            builder.append(destination.toString().toLowerCase());
        }

        if (isCapturingMove()) {
            builder.append("x");
        }

        builder.append(destination.toString().toLowerCase());
        if (board.isCheckmate()) {
            builder.append("#");
        } else if (board.isCheck()) {
            builder.append("+");
        }

        board.unMakeMove(1);
        return builder.toString();
    }

    private Set<Position> getBrotherPiecePositions(Board board) {
        Set<Move> possibleMoves = board
                .getPieceInSquare(destination)
                .getPossibleMoves(board, destination, board.getLastMove(), true);
        return possibleMoves
                .stream()
                .filter(move -> board.getPieceInSquare(move.getDestination()).getColor() == color
                            && board.getPieceInSquare(move.getDestination()).getType() == pieceInOrigin.getType())
                .map(Move::getDestination)
                .collect(Collectors.toSet());

    }

    @Override
    public boolean isSelfCapture () {
        return pieceInOrigin.getColor() == pieceInDestination.getColor();
    }

    @Override
    public String toString () {
        if (pieceInOrigin.getType() == PieceType.PAWN) {
            if (pieceInDestination.getType() != PieceType.NO_PIECE) {
                return origin.toString().substring(0, 1).toLowerCase() + "x" + destination.toString().toLowerCase();
            } else {
                return destination.toString().toLowerCase();
            }
        }
        if (isCapturingMove()) {
            return MoveHashMap.moveHashMap.get(pieceInOrigin.getType()).toUpperCase() + "x" + destination.toString().toLowerCase();
        } else {
            return MoveHashMap.moveHashMap.get(pieceInOrigin.getType()).toUpperCase() + destination.toString().toLowerCase();
        }
//        return color.toString();

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
        int result = 1 << 25;
        result |= getOrigin().hashCode() << (22 - 6);
        result |= getDestination().hashCode() << (22 - 12);
        result |= pieceInOrigin.hashCode() << (22 - 16);
        result |= pieceInDestination.hashCode() << (22 - 20);

        return result;
    }

    @Override
    public Position getDestination () {
        return destination;
    }

    public static void main(String[] args) {

//        board.makeMove();

    }
}

class MoveHashMap {
    static final Map<PieceType, String> moveHashMap = Map.ofEntries(
            Map.entry(PieceType.KING, "k"),
            Map.entry(PieceType.ROOK, "r"),
            Map.entry(PieceType.KNIGHT, "n"),
            Map.entry(PieceType.QUEEN, "q"),
            Map.entry(PieceType.PAWN, "p"),
            Map.entry(PieceType.BISHOP, "b"),
            Map.entry(PieceType.NO_PIECE, "null")
    );
}
