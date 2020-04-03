package chess.board;

import chess.misc.exceptions.ChessException;
import chess.misc.Position;
import chess.piece.King;
import chess.piece.Knight;
import chess.piece.NoPiece;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

public class ThreatChecker {
    static boolean isUnderThreat (Position square, Board board) {
        return checkForPawns           (square, board)
            || checkForKings           (square, board)
            || checkForKnights         (square, board)
            || checkForBishopsAndQueens(square, board)
            || checkForRooksAndQueens  (square, board);
    }

    private static boolean checkForRooksAndQueens (Position square, Board board) {
        PieceColor opponentColor = board.getPieceInSquare(square).getColor().invert();

        Piece left = makeRayCast(board, square, 1, 0);
        Piece right =makeRayCast(board, square, -1, 0);
        Piece up = makeRayCast(board, square, 0, -1);
        Piece down = makeRayCast(board, square, 0, 1);

        return     (left.getType()   == PieceType.QUEEN || left.getType()   == PieceType.ROOK) && left.getColor()  == opponentColor
                || (right.getType()  == PieceType.QUEEN || right.getType()  == PieceType.ROOK) && right.getColor() == opponentColor
                || (up.getType()     == PieceType.QUEEN || up.getType()     == PieceType.ROOK) && up.getColor()    == opponentColor
                || (down.getType()   == PieceType.QUEEN || down.getType()   == PieceType.ROOK) && down.getColor()  == opponentColor;
    }

    private static boolean checkForBishopsAndQueens (Position square, Board board) {
        PieceColor opponentColor = board.getPieceInSquare(square).getColor().invert();

        Piece upLeft = makeRayCast(board, square, -1, -1);
        Piece upRight =makeRayCast(board, square, 1, -1);
        Piece downLeft = makeRayCast(board, square, -1, 1);
        Piece downRight = makeRayCast(board, square, 1, 1);

        return     (upLeft.getType()       == PieceType.QUEEN || upLeft.getType()       == PieceType.BISHOP) && upLeft.getColor()    == opponentColor
                || (upRight.getType()      == PieceType.QUEEN || upRight.getType()      == PieceType.BISHOP) && upRight.getColor()   == opponentColor
                || (downLeft.getType()     == PieceType.QUEEN || downLeft.getType()     == PieceType.BISHOP) && downLeft.getColor()  == opponentColor
                || (downRight.getType()    == PieceType.QUEEN || downRight.getType()    == PieceType.BISHOP) && downRight.getColor() == opponentColor;
    }

    private static boolean checkForKnights (Position square, Board board) {
        PieceColor color = board.getPieceInSquare(square).getColor();

        for (Position offset : Knight.offsets) {
            Position newPosition;

            newPosition = square.offset(offset, false);
            if (!newPosition.verify()) {
                continue;
            }

            Piece piece = board.getPieceInSquare(newPosition);
            if (piece.getType() == PieceType.KNIGHT && piece.getColor() == color.invert()) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkForKings (Position square, Board board) {
        PieceColor color = board.getPieceInSquare(square).getColor();

        for (Position offset : King.offsets) {
            Position newPosition;

            newPosition = square.offset(offset, false);
            if (!newPosition.verify()) {
                continue;
            }

            Piece piece = board.getPieceInSquare(newPosition);
            if (piece.getType() == PieceType.KING && piece.getColor() == color.invert()) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkForPawns (Position square, Board board) {
        Piece piece = board.getPieceInSquare(square);

        try {
            Position offset = square.offset(1, -piece.getForwardDirection(), false);
            Piece right;
            if (offset.verify()) {
                right = board.getPieceInSquare(offset);
            } else {
                right = null;
            }

            offset = square.offset(-1, -piece.getForwardDirection(), false);
            Piece left;
            if (offset.verify()) {
                left = board.getPieceInSquare(offset);
            } else {
                left = null;
            }

            return right != null && right.getType() == PieceType.PAWN && right.getColor() == piece.getColor().invert()
                ||  left != null && left.getType() == PieceType.PAWN &&  left.getColor() == piece.getColor().invert();

        } catch (ChessException e) {
            throw new ChessException("Not applicable " + board + square + board.getStateHistory().getCurrentState().getWhiteKingPosition() + board.getMoveHistoryPretty());
        }
    }

    private static Piece makeRayCast (Board board, Position position, int deltaX, int deltaY) {
        int x = position.getX();
        int y = position.getY();

        x += deltaX;
        y += deltaY;

        while (x >= 0 && x < 8 && y >= 0 && y < 8) {
            Piece current = board.getPieceInSquare(x, y);
            if (current.getColor() != PieceColor.NO_COLOR) {
                return current;
            }

            x += deltaX;
            y += deltaY;
        }

        return new NoPiece();
    }

    public static void main(String[] args) {
        Board board = Board.fromFile("/home/kaappo/git/chess/src/main/resources/boards/1564930703584.out");

        System.out.println(board.isCheck(PieceColor.BLACK));
    }
}
