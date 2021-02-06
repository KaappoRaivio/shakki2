package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import chess.board.BoardNotation;
import misc.Position;
import chess.piece.CastlingKing;
import chess.piece.CastlingRook;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

import java.io.Serializable;

public abstract class Move implements Serializable {
    public static Move parseMove(String text, PieceColor color, Board board, boolean shortAlgebraicNotation) {
        if (shortAlgebraicNotation) {
            return MoveParser.parseMove(text, board);
        } else {
            return parseMove(text, color, board);
        }
    }
    public static Move parseMove(String text, PieceColor color, Board board) {
        if (text.equals("") || text.equals("NoMove")) {
            return NoMove.NO_MOVE;
        } else if (text.toUpperCase().equals("O-O")) {
            return new CastlingMove(CastlingType.KING_SIDE, color, board);
        } else if (text.toUpperCase().equals("O-O-O")) {
            return new CastlingMove(CastlingType.QUEEN_SIDE, color, board);
        } else {
            Position origin = Position.fromString(text.substring(0, 2));
            Position destination = Position.fromString(text.substring(2, 4));

            if (board.getPieceInSquare(origin) instanceof CastlingRook) {
                return new CastlingRookMove(origin, destination, board);
            } else if (board.getPieceInSquare(origin) instanceof CastlingKing) {
                return new CastlingKingMove(origin, destination, board);
            } else {
                if (text.contains("=")) {
                    return new PromotionMove(origin, destination, board, BoardNotation.DEFAULT_NOTATION.getPiece(text.split("=")[1]).getType());
                }
                return new NormalMove(origin, destination, board);
            }
        }
    }

    public abstract void   makeMove(Piece[][] buffer);
    public abstract void unmakeMove(Piece[][] buffer);

    public abstract boolean resetsFiftyMoveRule();
    public abstract boolean affectsKingPosition();
    public abstract boolean capturesKing();
    public abstract boolean isCapturingMove();
    public abstract boolean isSelfCapture ();

    public abstract Pair<PieceColor, Position> getNewKingPosition();

    public abstract Position getOrigin();
    public abstract Position getDestination();
    public abstract PieceColor getColor();
    public abstract Piece getPiece();

    public abstract int getIncrementalHash(int oldHash, BoardHasher hasher);

    public abstract String getShortAlgebraicNotation(Board board);

    public static void main(String[] args) {
        Board board = Board.getStartingPosition();
        System.out.println(Move.parseMove("e2e4", PieceColor.WHITE, board));
    }
}
