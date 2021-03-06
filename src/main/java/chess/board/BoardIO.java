package chess.board;

import chess.move.NormalMove;
import misc.Position;
import misc.ReadWriter;
import chess.move.CastlingType;
import chess.move.Move;
import chess.piece.*;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BoardIO {
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
//                    moveCount = (Integer.parseInt(line.split("( )+")[1]) - 1) * 2;
                    moveCount = Integer.parseInt(line.split("( )+")[1]);
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
        builder.append(BoardIO.FIFTY_MOVE_RESET).append(" ").append(stateHistory.getCurrentState().getMovesSinceFiftyMoveReset()).append("\n");
        builder.append(BoardIO.LAST_MOVE).append(" ").append(lastMove).append("\n");

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

        builder.append(BoardIO.MOVECOUNT).append(" ").append(fullMoveClock).append("\n");
        builder.append(BoardIO.FIFTY_MOVE_RESET).append(" ").append(halfMoveClock).append("\n");
        builder.append(BoardIO.LAST_MOVE).append(" ").append(convertLastMove(enPassantTarget)).append("\n");

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
            if (Character.isUpperCase(c)) {
                chars[i] = Character.toLowerCase(c);
            } else if (Character.isLowerCase(c)) {
                chars[i] = Character.toUpperCase(c);
            }
        }
        return new String(chars);
    }

    public static String toFEN (Board board) {
        StringBuilder builder = new StringBuilder();
        String fenBoard = getFENBoard(board);
        builder.append(fenBoard).append(" ");
        builder.append(board.getTurn() == PieceColor.WHITE ? "w" : "b").append(" ");
        builder.append(getCastlingString(board)).append(" ");
        builder.append(getEnPassantString(board)).append(" ");
        builder.append(board.getStateHistory().getCurrentState().getMovesSinceFiftyMoveReset()).append(" ");
        builder.append(board.getStateHistory().getCurrentState().getMoveCount()).append(" ");

        return builder.toString();
    }

    private static String getEnPassantString(Board board) {
        Move lastMove = board.getLastMove();
        if (lastMove instanceof NormalMove && Math.abs(lastMove.getOrigin().getY() - lastMove.getDestination().getY()) == 2 && lastMove.getPiece().getType() == PieceType.PAWN) {
            return new Position(lastMove.getDestination().getX(), (lastMove.getOrigin().getY() + lastMove.getDestination().getY()) / 2).toString().toLowerCase();
        }
        return "-";
    }

    private static String getCastlingString(Board board) {
        String whiteCastling = "";

        if (board.getPieceInSquare(4, 0) instanceof CastlingKing) {
            if (board.getPieceInSquare(7, 0) instanceof CastlingRook) whiteCastling += "K";
            if (board.getPieceInSquare(0, 0) instanceof CastlingRook) whiteCastling += "Q";
        }

        String blackCastling = "";

        if (board.getPieceInSquareRelativeTo(PieceColor.BLACK, 4, 0, true) instanceof CastlingKing) {
            if (board.getPieceInSquareRelativeTo(PieceColor.BLACK, 7, 0, true) instanceof CastlingRook) blackCastling += "k";
            if (board.getPieceInSquareRelativeTo(PieceColor.BLACK, 0, 0, true) instanceof CastlingRook) blackCastling += "q";
        }

        String result = whiteCastling + blackCastling;
        return result.length() > 0 ? result : "-";
    }

    private static Map<Piece, String> magicMap = Map.ofEntries(
            Map.entry(new King(PieceColor.WHITE), "K"),
            Map.entry(new King(PieceColor.BLACK), "k"),

            Map.entry(new CastlingKing(PieceColor.WHITE), "K"),
            Map.entry(new CastlingKing(PieceColor.BLACK), "k"),

            Map.entry(new Queen(PieceColor.WHITE), "Q"),
            Map.entry(new Queen(PieceColor.BLACK), "q"),

            Map.entry(new Rook(PieceColor.WHITE), "R"),
            Map.entry(new Rook(PieceColor.BLACK), "r"),

            Map.entry(new CastlingRook(PieceColor.WHITE), "R"),
            Map.entry(new CastlingRook(PieceColor.BLACK), "r"),

            Map.entry(new Bishop(PieceColor.WHITE), "B"),
            Map.entry(new Bishop(PieceColor.BLACK), "b"),

            Map.entry(new Knight(PieceColor.WHITE), "N"),
            Map.entry(new Knight(PieceColor.BLACK), "n"),

            Map.entry(new Pawn(PieceColor.WHITE), "P"),
            Map.entry(new Pawn(PieceColor.BLACK), "p")
    );

    private static String getFENBoard (Board board) {
//        StringBuilder builder = new StringBuilder();

        List<String> rows = new ArrayList<>();

        for (Piece[] pieces : board.getBoard()) {
            LinkedList<String> row = new LinkedList<>();
            for (int x = 0; x < 8; x++) {
                Piece piece = pieces[x];

                if (piece.getColor() != PieceColor.NO_COLOR) {
                    String obj = magicMap.get(piece);
                    row.add(Objects.requireNonNull(obj));
                } else {
                    String value = row.peekLast();
                    if (value != null && Character.isDigit(value.charAt(0))) {
                        row.removeLast();
                        row.add(String.valueOf(Integer.parseInt(value) + 1));
                    } else {
                        row.add("1");
                    }
                }
            }
            rows.add(String.join("", row));
        }

        Collections.reverse(rows);
        return String.join("/", rows);

    }

    public static void main(String[] args) {
//        System.out.println(fromFENFile("/home/kaappo/git/shakki2/src/main/resources/boards/test.fen"));
        Board board = Board.fromFEN("R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNN1KB1 w - - 1 2");
        System.out.println(board);
        System.out.println(board.toFEN());

        board = Board.getStartingPosition();
        System.out.println(board);
        System.out.println(board.toFEN());
//        System.out.println(getFENBoard(board));
    }
}
