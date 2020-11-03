package chess.board;

import chess.misc.Position;
import chess.misc.ReadWriter;
import chess.move.CastlingType;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BoardParser {
    public static final String LAST_MOVE = "last_move";
    public static final String FIFTY_MOVE_RESET = "fifty_move_reset";
    public static final String MOVECOUNT = "movecount";

    public static Board fromFile (String path, BoardNotation boardNotation) {
        return fromHumanReadable(ReadWriter.readFile(path), boardNotation);
    }

    public static Board fromFEN (String FEN) {
        return fromHumanReadable(convertFEN(FEN), BoardNotation.DEFAULT_NOTATION);
    }

    private static Board fromHumanReadable (String representation, BoardNotation boardNotation) {
        String[] lines = representation.split("\n");
        Piece[][] buffer = new Piece[8][8];

        for (int y = 0; y < 8; y++) {
            String[] line = lines[7 - y].split("( )+");

            for (int x = 0; x < 8; x++) {
                buffer[y][x] = boardNotation.getPiece(line[x]);
            }
        }

        String lastMoveString = "";
        int movesSince50Reset = 0;
        int moveCount = 0;


        PieceColor turn = null;
        loop: for (String line : lines) {
            switch (line.toUpperCase()) {
                case "WHITE":
                    turn = PieceColor.WHITE;
                    break loop;
                case "BLACK":
                    turn = PieceColor.BLACK;
                    break loop;
            }
        }

        Objects.requireNonNull(turn);

        for (String line : lines) {
            switch (line.split("( )+")[0]) {
                case LAST_MOVE:
                    lastMoveString = line.split("( )+")[1];
                    break;
                case FIFTY_MOVE_RESET:
                    movesSince50Reset = Integer.parseInt(line.split("( )+")[1]) * 2;
                    break;
                case MOVECOUNT:
                    moveCount = (Integer.parseInt(line.split("( )+")[1]) - 1) * 2;
            }
        }


        return new Board(buffer, movesSince50Reset, lastMoveString, turn, moveCount);
    }


    public static Board fromFENFile (String path) {
        return fromHumanReadable(convertFEN(ReadWriter.readFile(path)), BoardNotation.DEFAULT_NOTATION);
    }

    static String getHumanReadableDump (Piece[][] board, int dimX, int dimY, BoardStateHistory stateHistory, Move lastMove) {
        StringBuilder builder = new StringBuilder();

        for (int y = dimY - 1; y >= 0; y--) {
            if (y < dimY - 1) {
                builder.append("\n");
            }

            for (int x = 0; x < board[y].length; x++) {
                builder.append(board[y][x]);

                if (x + 1 < board[y].length) {
                    builder.append(" ");
                }
            }
        }

        builder.append("\n");

        builder.append(stateHistory.getCurrentState().getTurn()).append("\n");
        builder.append(BoardParser.FIFTY_MOVE_RESET).append(" ").append(stateHistory.getCurrentState().getMovesSinceFiftyMoveReset()).append("\n");
        builder.append(BoardParser.LAST_MOVE).append(" ").append(lastMove).append("\n");

        return builder.toString();
    }

    public static String convertFEN(String FEN) {
        var split = FEN.strip().split(" ");
        String boardFEN = split[0];
        String turn = split[1];
        String castling = split[2];
        String enPassantTarget = split[3];
        String halfMoveClock = split[4];
        String fullMoveClock = split[5];

        String parsedBoard = convertBoard(boardFEN, castling);

        StringBuilder builder = new StringBuilder();
        builder.append(parsedBoard).append("\n");
        builder.append(turn.equals("w") ? "white" : "black").append("\n");

        builder.append(BoardParser.MOVECOUNT).append(" ").append(fullMoveClock).append("\n");
        builder.append(BoardParser.FIFTY_MOVE_RESET).append(" ").append(halfMoveClock).append("\n");
        builder.append(BoardParser.LAST_MOVE).append(" ").append(convertLastMove(enPassantTarget)).append("\n");

        return builder.toString();
    }


    private static String convertLastMove (String enPassantTarget) {
        if (enPassantTarget.equals("-")) {
            return "NoMove";
        }
        Position target = Position.fromString(enPassantTarget);
        if (target.getY() == 2) {
            return new Position(target.getX(), 1).toString() + new Position(target.getX(), 3 ).toString();
        } else {
            return new Position(target.getX(), 6).toString() + new Position(target.getX(), 4 ).toString();
        }
    }

    private static boolean canCastle (PieceColor color, CastlingType side, String castling) {
        switch (color) {
            case BLACK:
                switch (side) {
                    case KING_SIDE:
                        return castling.contains("k");
                    case QUEEN_SIDE:
                        return castling.contains("q");
                }
            case WHITE:
                switch (side) {
                    case KING_SIDE:
                        return castling.contains("K");
                    case QUEEN_SIDE:
                        return castling.contains("Q");
                }

            default:
                throw new RuntimeException("Impossible");
        }
    }

    private static Pattern number = Pattern.compile("\\d");

    private static String convertBoard(String FEN, String castling) {
        List<List<String>> board = new ArrayList<>();
        String[] rows = FEN.split("/");

        for (int y = 0; y < 8; y++) {
            List<String> row = new ArrayList<>();
            board.add(row);

            String current = rows[y];
            for (int x = 0; x < current.length(); x++) {
                String square = String.valueOf(current.charAt(x));
                if (number.matcher(square).matches()) {
                    for (int a = 0; a < Integer.parseInt(square); a++) {
                        row.add(".");
                    }
                    continue;
                }

                row.add(reverseCase(square));
            }
        }

        if (canCastle(PieceColor.WHITE, CastlingType.KING_SIDE, castling)) {
            board.get(7).set(7, "r̅");
            board.get(7).set(4, "k̅");
        }
        if (canCastle(PieceColor.WHITE, CastlingType.QUEEN_SIDE, castling)) {
            board.get(7).set(0, "r̅");
            board.get(7).set(4, "k̅");
        }
        if (canCastle(PieceColor.BLACK, CastlingType.KING_SIDE, castling)) {
            board.get(0).set(7, "R̅");
            board.get(0).set(4, "K̅");
        }
        if (canCastle(PieceColor.BLACK, CastlingType.QUEEN_SIDE, castling)) {
            board.get(0).set(0, "R̅");
            board.get(0).set(4, "K̅");
        }

        return board.stream().map(row -> String.join(" ", row)).collect(Collectors.joining("\n"));
    }

    // https://stackoverflow.com/questions/1729778/
    private static String reverseCase(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c))
            {
                chars[i] = Character.toLowerCase(c);
            }
            else if (Character.isLowerCase(c))
            {
                chars[i] = Character.toUpperCase(c);
            }
        }
        return new String(chars);
    }

    public static void main(String[] args) {
        System.out.println(fromFENFile("/home/kaappo/git/shakki2/src/main/resources/boards/test.fen"));
    }
}
