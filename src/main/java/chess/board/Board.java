package chess.board;

import misc.Position;
import misc.ReadWriter;
import misc.TermColor;
import misc.exceptions.ChessException;
import chess.move.Move;
import chess.move.NoMove;
import chess.piece.NoPiece;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;
import misc.Saver;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static chess.piece.basepiece.PieceColor.*;

public class Board implements Serializable{
    private Piece[][] board;

    private final int dimX;
    private final int dimY;

    private final RepetitionTracker repetitionTracker = new RepetitionTracker();

    private final BoardStateHistory stateHistory;
    private BoardHasher hasher = new BoardHasher();
    private int hashCode;
    private boolean useExpensiveDrawCalculation = true;

    private Board (Piece[][] board) {
        this(board, 0, "", WHITE, 0);
    }

    Board (Piece[][] board, int fiftyMoveReset, String lastMoveString, PieceColor turn, int moveCount) {
        this.dimX = board[0].length;
        this.dimY = board.length;

        initBoard(dimX, dimY);
        this.board = board;

        Pair<Position, Position> kingPositions = findKings();
        PieceColor newTurn = turn.invert();
        stateHistory = new BoardStateHistory(new BoardState(kingPositions.getFirst(), kingPositions.getSecond(), fiftyMoveReset, Move.parseMove(lastMoveString, newTurn, this), turn, moveCount));
        hashCode = hasher.getFullHash(this);

//        stateHistory.getCurrentState().setCheck(isCheck());
//        stateHistory.getCurrentState().setCheckmate(isCheckMate());
//        stateHistory.getCurrentState().setPossibleMoves(getAllPossibleMoves());
        repetitionTracker.add(this);
    }


    public static Board fromFile (String path) {
        return BoardIO.fromFile(path, BoardNotation.DEFAULT_NOTATION);
    }

    public static Board getStartingPosition () {
        return fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public static Board fromFEN (String FEN) {
        return BoardIO.fromFEN(FEN);
    }

    private Pair<Position, Position> findKings () {
        Position whiteKingPos = null;
        Position blackKingPos = null;

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                Piece piece = getPieceInSquare(x, y);

                if (piece.getType() == PieceType.KING && piece.getColor() == WHITE) {
                    whiteKingPos = new Position(x, y);
                } else if (piece.getType() == PieceType.KING && piece.getColor() == PieceColor.BLACK) {
                    blackKingPos = new Position(x, y);
                }
            }
        }

        return new Pair<>(Optional.ofNullable(whiteKingPos).orElseThrow(() -> new ChessException("Couldn't find white king from board " + toString() + "!")),
                Optional.ofNullable(blackKingPos).orElseThrow(() -> new ChessException("Couldn't find black king from board " + toString() + "!")));
    }




    private void initBoard (int dimX, int dimY) {
        board = new Piece[dimY][dimX];

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                board[y][x] = new NoPiece();
            }
        }
    }

    public Piece getPieceInSquare (Position position) {
        return getPieceInSquare(position.getX(), position.getY());
    }

    public Piece getPieceInSquare (int x, int y) {
        try {
            return board[y][x];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChessException("Position (" + x + ", " + y + ") is invalid!");
        }
    }

    public Piece getPieceInSquareRelativeTo(PieceColor color, Position position) {
        return getPieceInSquareRelativeTo(color, position, false);
    }

    public Piece getPieceInSquareRelativeTo(PieceColor color, Position position, boolean flip) {
        return getPieceInSquareRelativeTo(color, position.getX(), position.getY(), flip);
    }

        public Piece getPieceInSquareRelativeTo(PieceColor color, int x, int y, boolean flip) {
        if (flip && color == PieceColor.BLACK) x = 7 - x;

        return getPieceInSquareRelativeTo(color, x, y);
    }

    public Piece getPieceInSquareRelativeTo (PieceColor color, int x, int y) {
        switch (color) {
            case BLACK:
                return getPieceInSquare(7- x, 7 - y);
            case WHITE:
                return getPieceInSquare(x, y);
            default:
                return null;
        }
    }

    public boolean isSquareEmpty (Position position) {
        return isSquareEmpty(position.getX(), position.getY());
    }

    public boolean isSquareEmpty (int x, int y) {
        return getPieceInSquare(x, y).getType() == PieceType.NO_PIECE;
    }

    public boolean isSquareUnderThreat(Position position) {
        return isSquareUnderThreat(position, getPieceInSquare(position).getColor());
    }

    public boolean isSquareUnderThreat (Position position, PieceColor color) {
        return ThreatChecker.isUnderThreat(position, this, color);
    }


    public boolean isMoveLegal(Move move) {
        return isMoveLegal(move, true, false);
    }

    public boolean isMoveLegal (Move move, boolean pathCheck, boolean ignoreTurn) {

        boolean result = !move.capturesKing();

        if (!ignoreTurn) {
            result &= move.getColor() == getTurn();
        }

        if (pathCheck) {
            result &= getPieceInSquare(move.getOrigin()).getPossibleMoves(this, move.getOrigin(), getLastMove()).contains(move);
        }

        if (result) {
            executeMoveNoChecks(move);
            boolean isStateLegal = !isCheck(move.getColor());
            unMakeMove(1);

            return isStateLegal;
        } else {
            return false;
        }
    }

    public List<Move> getAllPossibleMoves () {
        return getAllPossibleMoves(getTurn());
    }

    public List<Move> getAllPossibleMoves(PieceColor color) {
        return getAllPossibleMoves(color, false);
    }

    public List<Move> getAllPossibleMoves (PieceColor color, boolean ignoreTurn) {
        List<Move> possibleMoves = stateHistory.getCurrentState().getPossibleMoves(color);
        if (possibleMoves != null) {
            return possibleMoves;
        } else {
            var moves = calculateAllPossibleMoves(color, ignoreTurn);
            stateHistory.getCurrentState().setPossibleMoves(color, moves);
            return moves;
        }
    }

    public List<Move> calculateAllPossibleMoves (PieceColor color, boolean ignoreTurn) {
        List<Move> moves = new ArrayList<>();

        for (int y = 0; y < dimX; y++) {
            for (int x = 0; x < dimY; x++) {
                Piece piece = getPieceInSquare(x, y);
                if (piece.getColor() != color) {
                    continue;
                }

                Set<Move> possibleMoves = piece.getPossibleMoves(this, new Position(x, y), getLastMove());
                for (Move possibleMove : possibleMoves) {
                    if (isMoveLegal(possibleMove, false, ignoreTurn)) {
                        moves.add(possibleMove);
                    }
                }
//                for (Move possibleMove : possibleMoves.getSecond()) {
//                    if (isMoveLegal(possibleMove, false, ignoreTurn, true)) {
//                        moves.getSecond().add(possibleMove);
//                    }
//                };
            }
        }

//        stateHistory.getCurrentState().setPossibleMoves(moves);
        return moves;
    }

    public void useExpensiveDrawCalculation(boolean use) {
        useExpensiveDrawCalculation = use;
    }

    public void makeMove (Move move) {
        if (isMoveLegal(move, true, false)) {
            executeMoveNoChecks(move);
        } else {
            throw new ChessException("Move " + move + " is not legal for position " + this + "!");
        }
    }

    public void executeMoveNoChecks(Move move) {
        move.makeMove(board);
        hashCode = move.getIncrementalHash(hashCode, hasher);

        stateHistory.push();

        if (move.affectsKingPosition()) {
            var pair = move.getNewKingPosition();

            switch (pair.getFirst()) {
                case WHITE:
                    stateHistory.getCurrentState().setWhiteKingPosition(pair.getSecond());
                    break;
                case BLACK:
                    stateHistory.getCurrentState().setBlackKingPosition(pair.getSecond());
                    break;
            }
        }

        if (move.resetsFiftyMoveRule()) {
            stateHistory.getCurrentState().setMovesSinceFiftyMoveReset(0);
        } else {
            stateHistory.getCurrentState().setMovesSinceFiftyMoveReset(stateHistory.getCurrentState().getMovesSinceFiftyMoveReset() + 1);
        }

        PieceColor newTurn = stateHistory.getCurrentState().getTurn();
        int currentMoveCount = stateHistory.getCurrentState().getMoveCount();
        stateHistory.getCurrentState().setLastMove(move);
        stateHistory.getCurrentState().setTurn(newTurn.invert());
        stateHistory.getCurrentState().setMoveCount(currentMoveCount + 1);

        if (useExpensiveDrawCalculation) {
            repetitionTracker.add(this);
        }
    }

    public void unMakeMove (int level) {
        for (int i = 0; i < level; i++) {
            undo(stateHistory.getCurrentState().getLastMove());
            stateHistory.pull();
        }
    }

    private void undo (Move lastMove) {
        if (useExpensiveDrawCalculation) repetitionTracker.subtract(this);
        lastMove.unmakeMove(board);
        hashCode = lastMove.getIncrementalHash(hashCode, hasher);
    }

    private boolean calculateCheckmate (PieceColor perspective) {
        return isCheck(perspective) && getAllPossibleMoves(perspective).size() == 0;
    }

    private boolean calculateCheck (PieceColor turn) {
        Position kingPosition = stateHistory.getCurrentState().getKingPosition(turn);
        return isSquareUnderThreat(Optional.ofNullable(kingPosition).orElseThrow());
    }


    public boolean isCheck () {
        return isCheck(getTurn());
    }

    public boolean isCheck (PieceColor turn) {
        if (stateHistory.getCurrentState().isCheck(turn) != null) {
            return stateHistory.getCurrentState().isCheck(turn);
        } else {
            boolean check = calculateCheck(turn);
            stateHistory.getCurrentState().setCheck(turn, check);
            return check;
        }
    }

//    public boolean isCheckMate() {
//        return isCheckMate(getTurn());
//    }

    public boolean isCheckmate() {
        return isCheckmate(getTurn());
    }

    public boolean isCheckmate(PieceColor turn) {
        if (stateHistory.getCurrentState().isCheckmate(turn) != null) {
            return stateHistory.getCurrentState().isCheckmate(turn);
        } else {
            boolean checkmate = calculateCheckmate(turn);
            stateHistory.getCurrentState().setCheckmate(turn, checkmate);
            return checkmate;
        }
    }

    public boolean isDraw () {
        // 100 half moves equal 50 whole moves
        boolean result = repetitionTracker.isDraw() || stateHistory.getCurrentState().getMovesSinceFiftyMoveReset() >= 100
                || (!isCheck() && getAllPossibleMoves().size() == 0);

        if (useExpensiveDrawCalculation) {
            result |= BoardHelpers.hasInsufficientMaterial(this);
        }

        return result;
    }

    public RepetitionTracker getRepetitionTracker() {
        return repetitionTracker;
    }

    @Override
    public String toString() {
        String[] s1 = toString(WHITE).split("\n");
        String[] s2 = toString(BLACK).split("\n");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < s1.length; i++) {
            builder.append(s1[i]).append("    ").append(s2[i]).append("\n");
        }

        try {
            builder.append(getTurn()).append(" to move\n");
        } catch (NullPointerException e) {
            builder.append("Turn not available");
        }

        return builder.toString();
    }

    private String toString (PieceColor perspective) {
        String hPadding = " ";
        String vPadding = "";

        String letters = perspective == WHITE ? " a b c d e f g h " : " h g f e d c b a ";
        StringBuilder builder = new StringBuilder(vPadding).append(hPadding).append("\n ").append(letters).append(hPadding).append("\n").append(vPadding);

        PieceColor squareColor = BLACK;
        for (int y = dimY - 1; y >= 0; y--) {
            squareColor = squareColor.invert();
            if (y < dimY - 1) {
                builder.append("\n");
            }

            int currentRow = perspective == WHITE ? y + 1 : 8 - y;
            builder.append(currentRow);

            for (int x = 0; x < dimX; x++) {
                builder.append(getSquare(getPieceInSquareRelativeTo(perspective, x, y), squareColor, x == 0, x == 7));
                squareColor = squareColor.invert();

            }

            builder.append(currentRow);
        }

        return builder.append("\n").append(vPadding).append(hPadding).append(letters).append(hPadding).toString();
    }

    public String conventionalToString() {
        String hPadding = " ";
        String vPadding = "";

        StringBuilder builder = new StringBuilder(vPadding).append(hPadding).append("\n  a b c d e f g h").append(hPadding).append("\n").append(vPadding);
        for (int y = dimY - 1; y >= 0; y--) {
            if (y < dimY - 1) {
                builder.append("\n");
            }

            builder.append(y + 1).append(hPadding);

            for (int x = 0; x < board[y].length; x++) {
                builder.append(board[y][x].conventionalToString());

                if (x + 1 < board[y].length) {
                    builder.append(" ");
                }
            }

            builder.append(hPadding).append(y + 1);
        }

        return builder.append("\n").append(vPadding).append(hPadding).append(" A B C D E F G H").append(hPadding).toString();
    }


    @Override
    public int hashCode() {
//        return Arrays.deepHashCode(board);
        return getTurn() == WHITE ? hashCode : -hashCode;
    }

    @Override
    public boolean equals (Object object) {
        if (object == null) {
            return false;
        }

        return getClass() == object.getClass() && hashCode() == object.hashCode();
    }

    public Board deepCopy () {
        return Saver.deepCopy(this, Board.class);
    }

    public Move getLastMove () {
        return stateHistory.getCurrentState().getLastMove();
    }

    public PieceColor getTurn () {
        return stateHistory.getCurrentState().getTurn();
    }

    public int getDimX () {
        return dimX;
    }

    public int getDimY () {
        return dimY;
    }

    Piece[][] getBoard () {
        return board;
    }

    public BoardStateHistory getStateHistory () {
        return stateHistory;
    }

    private String dumpHumanReadable() {
        return BoardIO.getHumanReadableDump(board, dimX, dimY, stateHistory, getLastMove());
    }

    public void saveHumanReadable (String path) {
        ReadWriter.writeFile(path, dumpHumanReadable());
    }

    public List<Move> getMoveHistory () {
        return stateHistory.getPreviousStates().stream().map(BoardState::getLastMove).filter(move -> !NoMove.NO_MOVE.equals(move)).collect(Collectors.toList());
    }

    public String getMoveHistoryPretty () {
        var states = stateHistory.getPreviousStates().stream().filter(state -> !NoMove.NO_MOVE.equals(state.getLastMove())).collect(Collectors.toList());
        Collections.reverse(states);
        Board originalPosition = deepCopy();
        originalPosition.unMakeMove(states.size());

        StringBuilder builder = new StringBuilder();

        if (states.size() > 0 && states.get(0).getLastMove().getColor() == PieceColor.BLACK) {
            states.add(0, new BoardState(null, null, 0, NoMove.NO_MOVE, WHITE, states.get(0).getMoveCount() - 1));
            states.forEach(state -> state.setMoveCount(state.getMoveCount() + 1));
        }

        for (int i = 0; i < states.size(); i++) {
            BoardState currentState = states.get(i);
            if (i % 2 == 0) {
                builder.append((currentState.getMoveCount() + 1) / 2).append(". ");
            }

            builder.append(currentState.getLastMove().getShortAlgebraic(originalPosition)).append(" ");
            if (!(currentState.getLastMove() instanceof NoMove)) {
                originalPosition.makeMove(currentState.getLastMove());
            }
        }

        return builder.toString();
    }

    private static String getSquare (Piece piece, PieceColor squareColor, boolean firstInRow, boolean lastInRow) {
        String res;
        String color;
        TermColor lightSquareColor = TermColor.ANSI_LIGHT_GRAY;
        TermColor darkSquareColor = TermColor.ANSI_DARK_GRAY;
        String pieceColor = piece.getColor() == WHITE ? TermColor.ANSI_WHITE.getEscape() : TermColor.ANSI_BLACK.getEscape();
        String firstInsert = "";
        String lastInsert = "";
        if (firstInRow) {
            if (squareColor == WHITE) {
                res = "▌";
                color = lightSquareColor.getEscape(true) + pieceColor;
                firstInsert = TermColor.ANSI_WHITE.getEscape();
                firstInsert += lightSquareColor.getEscape(true);
            } else {
                res = "▐";
                color = darkSquareColor.getEscape(true) + pieceColor;
                firstInsert = darkSquareColor.getEscape();
                firstInsert += TermColor.ANSI_WHITE.getEscape(true);
            }
        } else if (lastInRow) {
            if (squareColor == WHITE) {
                res = "▌";
                color = lightSquareColor.getEscape(true) + pieceColor;
                lastInsert = lightSquareColor.getEscape(true) + TermColor.ANSI_WHITE.getEscape();
                lastInsert += "▐";
            } else {
                res = "▐";
                color = darkSquareColor.getEscape(true) + pieceColor;
                lastInsert = darkSquareColor.getEscape(true) + TermColor.ANSI_WHITE.getEscape();
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

    public static void main(String[] args) {
//        Board board = Board.fromFEN("r1b1k2r/pp2q2p/2p2npb/2np4/4pP2/4P2N/PPPNB1PP/R1BQK2R w - - 0 1");
//        Board board = Board.getStartingPosition();
        Board board = Board.fromFEN("r3kb1r/1bpq1pp1/p3pn1p/1p6/2pPP3/P1N5/1P3PPP/R2QKB1R b KQkq - 0 11");

//        System.out.println(board.getAllPossibleMoves(PieceColor.WHITE));
        BoardHelpers.executeSequenceOfMoves(board,
                List.of("f6e4", "c3e4", "b7e4"));
//        BoardHelpers.executeSequenceOfMoves(board,
//                List.of("g1f3", "d7d5", "e2e3", "f7f6", "d2d4", "e7e5", "b1d2", "d8e7", "d4e5", "f6e5", "f1b5", "c7c6", "b5e2", "g8f6", "f3g5", "e5e4", "e2h5", "g7g6", "h5e2", "f8h6", "f2f4", "b8d7", "g5h3"));
        System.out.println(board.toString());
        System.out.println("asdasdasd");
//
//
//        String s1 = "▐█";
//        String s2 = "▌ ";
//
//        for (int i = 0; i < 10; i++) {
////            System.out.print(s1);
////            System.out.print(s2);
//            System.out.print(getSquare(new King(BLACK), WHITE));
//            System.out.print(getSquare(new King(WHITE), BLACK));
//        }
//        System.out.println();
//        for (int i = 0; i < 10; i++) {
////            System.out.print(s1);
////            System.out.print(s2);
//            System.out.print(getSquare(new King(WHITE), BLACK));
//            System.out.print(getSquare(new King(BLACK), WHITE));
//        }



//        board.makeMove(Move.parseMove(""));
//        CapableOfPlaying ai = new TreeAI(PieceColor.WHITE, board, 4, 8);
//        ai.updateValues(board, PieceColor.WHITE, 30);
//        System.out.println(ai.getMove());
//        System.out.println(board.hashCode());
//        System.out.println(board);
//        board.makeMove(Move.parseMove("b5b4", PieceColor.BLACK, board));
//        System.out.println(board.hashCode());
//        System.out.println(board);
//        board.unMakeMove(1);
//        System.out.println(board.hashCode());
//        Board board = Board.getStartingPosition();
//        BoardHelpers.executeSequenceOfMoves(x, List.of("e2e4", "e7e5"));
//        board.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board));
        System.out.println(board.getMoveHistoryPretty());
        System.out.println(board.conventionalToString());

//        board.makeMove(Move.parseMove("e7e5", PieceColor.WHITE, board));
//        System.out.println(board.getMoveHistoryPretty());
//        System.out.println(board);
//        Board board = Board.getStartingPosition();
//        System.out.println(board);
//        board.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board));
//        System.out.println(board);
    }

    public BoardHasher getHasher() {
        return hasher;
    }
}
