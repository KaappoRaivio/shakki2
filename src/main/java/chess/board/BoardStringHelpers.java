package chess.board;

import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.TermColor;

import static chess.piece.basepiece.PieceColor.BLACK;
import static chess.piece.basepiece.PieceColor.WHITE;

public class BoardStringHelpers {
    static String getSquare (Piece piece, PieceColor squareColor, boolean firstInRow, boolean lastInRow) {
        String res;
        String color;
        TermColor lightSquareColor = TermColor.ANSI_LIGHT_GRAY;
        TermColor darkSquareColor = TermColor.ANSI_DARK_GRAY;

        TermColor trailing = TermColor.ANSI_WHITE;

        String pieceColor = piece.getColor() == WHITE ? TermColor.ANSI_WHITE.getEscape() : TermColor.ANSI_BLACK.getEscape();
        String firstInsert = "";
        String lastInsert = "";
        if (firstInRow) {
            if (squareColor == WHITE) {
                res = "▌";
                color = lightSquareColor.getEscape(true) + pieceColor;
                firstInsert = trailing.getEscape();
                firstInsert += lightSquareColor.getEscape(true);
            } else {
                res = "▐";
                color = darkSquareColor.getEscape(true) + pieceColor;
                firstInsert = darkSquareColor.getEscape();
                firstInsert += trailing.getEscape(true);
            }
        } else if (lastInRow) {
            if (squareColor == WHITE) {
                res = "▌";
                color = lightSquareColor.getEscape(true) + pieceColor;
                lastInsert = lightSquareColor.getEscape(true) + trailing.getEscape();
                lastInsert += "▐";
            } else {
                res = "▐";
                color = darkSquareColor.getEscape(true) + pieceColor;
                lastInsert = darkSquareColor.getEscape(true) + trailing.getEscape();
                lastInsert += "▐";
            }
        } else {
            if (squareColor == WHITE) {
                res = "▌";
                color = lightSquareColor.getEscape(true) + pieceColor;
            } else {
                res = "▐";
                color = darkSquareColor.getEscape(true) + pieceColor;
            }
        }



        return lightSquareColor.getEscape(true) + darkSquareColor.getEscape() + firstInsert + res + color + piece + lastInsert + TermColor.ANSI_RESET.getEscape();
    }

    static String getString (Board board, PieceColor perspective) {
        String hPadding = " ";
        String vPadding = "";

        String letters = perspective == WHITE ? " a b c d e f g h " : " h g f e d c b a ";
        StringBuilder builder = new StringBuilder(vPadding).append(hPadding).append("\n ").append(letters).append(hPadding).append("\n").append(vPadding);

        PieceColor squareColor = BLACK;
        for (int y = 7; y >= 0; y--) {
            squareColor = squareColor.invert();
            if (y < 7) {
                builder.append("\n");
            }

            int currentRow = perspective == WHITE ? y + 1 : 8 - y;
            builder.append(currentRow);

            for (int x = 0; x < 8; x++) {
                builder.append(BoardStringHelpers.getSquare(board.getPieceInSquareRelativeTo(perspective, x, y), squareColor, x == 0, x == 7));
                squareColor = squareColor.invert();

            }

            builder.append(currentRow);
        }

        return builder.append("\n").append(vPadding).append(hPadding).append(letters).append(hPadding).toString();
    }

    static String getConventionalString(Board board) {
        String hPadding = " ";
        String vPadding = "";

        StringBuilder builder = new StringBuilder(vPadding).append(hPadding).append("\n  a b c d e f g h").append(hPadding).append("\n").append(vPadding);
        for (int y = 7; y >= 0; y--) {
            if (y < 7) {
                builder.append("\n");
            }

            builder.append(y + 1).append(hPadding);

            for (int x = 0; x < 8; x++) {
                builder.append(board.getPieceInSquare(x, y).conventionalToString());

                if (x < 7) {
                    builder.append(" ");
                }
            }

            builder.append(hPadding).append(y + 1);
        }

        return builder.append("\n").append(vPadding).append(hPadding).append(" A B C D E F G H").append(hPadding).toString();
    }
}
