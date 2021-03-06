package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import misc.Position;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

public class CastlingMove extends Move {
    final private CastlingType castlingType;
    final private PieceColor color;
    final private Position kingPosition;
    final private Board board;

    private CastlingRookMove castlingRookMove;
    private CastlingKingMove castlingKingMove;


    public CastlingMove (CastlingType castlingType, PieceColor kingColor, Board board) {
        this.castlingType = castlingType;
        this.color = kingColor;
        this.kingPosition = board.getStateHistory().getCurrentState().getKingPosition(kingColor);
        this.board = board;

        prepareMoves();


    }

    private void prepareMoves () {
        final int kingDestinationOffsetX;
        final int rookDestinationOffsetX;
        final int rookPositionOffsetX;


        switch (castlingType) {
            case KING_SIDE:
                kingDestinationOffsetX = 2;
                rookDestinationOffsetX = 1;
                rookPositionOffsetX = 3;
                break;
            case QUEEN_SIDE:
                kingDestinationOffsetX = -2;
                rookDestinationOffsetX = -1;
                rookPositionOffsetX = -4;
                break;
            default:
                throw new RuntimeException("Shouldn't happen");
        }

        Position kingDestination = kingPosition.offsetX(kingDestinationOffsetX);

        Position rookOrigin = kingPosition.offsetX(rookPositionOffsetX);
        Position rookDestination = kingPosition.offsetX(rookDestinationOffsetX);

        castlingKingMove = new CastlingKingMove(kingPosition, kingDestination, board);
        castlingRookMove = new CastlingRookMove(rookOrigin, rookDestination, board);
    }

    @Override
    public void makeMove (Piece[][] buffer) {
        castlingKingMove.makeMove(buffer);
        castlingRookMove.makeMove(buffer);
    }

    @Override
    public void unmakeMove (Piece[][] buffer) {
        castlingKingMove.unmakeMove(buffer);
        castlingRookMove.unmakeMove(buffer);
    }

    @Override
    public boolean resetsFiftyMoveRule () {
        return false;
    }

    @Override
    public boolean affectsKingPosition () {
        return true;
    }

    @Override
    public boolean capturesKing() {
        return castlingKingMove.capturesKing() || castlingRookMove.capturesKing();
    }

    @Override
    public boolean isCapturingMove() {
        return false;
    }

    @Override
    public boolean isSelfCapture() {
        return false;
    }

    @Override
    public Pair<PieceColor, Position> getNewKingPosition () {

        return new Pair<>(color, castlingKingMove.getDestination());
    }

    @Override
    public Position getOrigin () {
        return kingPosition;
    }

    @Override
    public Position getDestination() {
        return castlingRookMove.getDestination();
    }

    @Override
    public PieceColor getColor () {
        return color;
    }

    @Override
    public Piece getPiece() {
        return castlingRookMove.getPiece();
    }

    @Override
    public int getIncrementalHash(int oldHash, BoardHasher hasher) {
        oldHash = castlingKingMove.getIncrementalHash(oldHash, hasher);
        oldHash = castlingRookMove.getIncrementalHash(oldHash, hasher);
//        oldHash ^= hasher.getPartHash(kingPosition, castlingKingMove.pieceInOrigin);
//        oldHash ^= hasher.getPartHash(castlingKingMove.destination, castlingKingMove.pieceInOrigin);

        return oldHash;
    }

    @Override
    public String getShortAlgebraicNotation(Board board) {
        String shortAlgebraicNotation;
        switch (castlingType) {
            case QUEEN_SIDE:
                shortAlgebraicNotation = "O-O-O";
                break;
            case KING_SIDE:
                shortAlgebraicNotation = "O-O";
                break;
            default:
                throw new RuntimeException("");
        }

        board.makeMove(this);
        if (board.isCheckmate()) {
            shortAlgebraicNotation += "#";
        } else if (board.isCheck()) {
            shortAlgebraicNotation += "+";
        }
        board.unMakeMove(1);

        return shortAlgebraicNotation;
    }

    @Override
    public String toString () {
        return getShortAlgebraicNotation(null) + " " + color;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof CastlingMove)) return false;
        CastlingMove that = (CastlingMove) o;
        return castlingType == that.castlingType &&
                getColor() == that.getColor();
//                kingPosition.equals(that.kingPosition) &&
//                board.equals(that.board) &&
//                castlingRookMove.equals(that.castlingRookMove) &&
//                castlingKingMove.equals(that.castlingKingMove);
    }

    @Override
    public int hashCode () {
//        return Objects.hash(castlingType, getColor(), kingPosition, board, castlingRookMove, castlingKingMove);
        int result = 1 << 26;

        if (color == PieceColor.WHITE) {
            return result | (castlingType == CastlingType.KING_SIDE ? 1 : 2);
        } else {
            return result | (castlingType == CastlingType.KING_SIDE ? 3 : 4);
        }
    }
}
