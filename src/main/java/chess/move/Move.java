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

public interface Move extends Serializable {
    static Move parseMove (String text, PieceColor color, Board board) {
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

    void   makeMove (Piece[][] buffer);
    void unmakeMove (Piece[][] buffer);

    boolean resetsFiftyMoveRule ();
    boolean affectsKingPosition ();
    boolean capturesKing ();
    boolean isCapturingMove ();

    Pair<PieceColor, Position> getNewKingPosition ();

    Position getOrigin ();
    Position getDestination ();
    PieceColor getColor ();
    Piece getPiece();

    int getIncrementalHash(int oldHash, BoardHasher hasher);

    String getShortAlgebraic(Board board);

    public static void main(String[] args) {
        Board board = Board.getStartingPosition();
        System.out.println(Move.parseMove("e2e4", PieceColor.WHITE, board));
    }
}
