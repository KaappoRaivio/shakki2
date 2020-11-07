package chess.board;

import chess.misc.Position;
import chess.misc.ReadWriter;
import chess.misc.exceptions.ChessException;
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

public class Board implements Serializable{
    private Piece[][] board;

    private final int dimX;
    private final int dimY;

    private final RepetitionTracker repetitionTracker = new RepetitionTracker();

    private final BoardStateHistory stateHistory;
    private BoardHasher hasher = new BoardHasher();
    private int hashCode;

    private Board (Piece[][] board) {
        this(board, 0, "", PieceColor.WHITE, 0);
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

                if (piece.getType() == PieceType.KING && piece.getColor() == PieceColor.WHITE) {
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
        int x = position.getX();
        int y = position.getY();
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
        return isMoveLegal(move, true, false, false);
    }

    public boolean isMoveLegal (Move move, boolean pathCheck, boolean ignoreTurn, boolean includeSelfCapture) {

        boolean result = !move.capturesKing();

        if (!ignoreTurn) {
            result &= move.getColor() == getTurn();
        }

        if (pathCheck) {
            result &= getPieceInSquare(move.getOrigin()).getPossibleMoves(this, move.getOrigin(), getLastMove(), includeSelfCapture).contains(move);
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


//    public Set<Move> getAllPossibleMoves() {
//        return getAllPossibleMoves(getTurn());
//    }

    public Set<Move> getAllPossibleMoves () {
        return getAllPossibleMoves(getTurn());
    }

    public Set<Move> getAllPossibleMoves(PieceColor color) {
        return getAllPossibleMoves(color, false, false);
    }

    public Set<Move> getAllPossibleMoves (PieceColor color, boolean ignoreTurn, boolean includeSelfCapture) {
        if (stateHistory.getCurrentState().getPossibleMoves(color) != null && !includeSelfCapture) {
            return stateHistory.getCurrentState().getPossibleMoves(color);
        } else {
            var moves = calculateAllPossibleMoves(color, ignoreTurn, includeSelfCapture);
            if (!includeSelfCapture) {
                stateHistory.getCurrentState().setPossibleMoves(color, moves);
            }
            return moves;
        }
    }

    private Set<Move> calculateAllPossibleMoves(PieceColor color) {
        return calculateAllPossibleMoves(color, false, false);
    }

    private Set<Move> calculateAllPossibleMoves (PieceColor color, boolean ignoreTurn, boolean includeSelfCapture) {
        Set<Move> moves = new HashSet<>();

        for (int y = 0; y < dimX; y++) {
            for (int x = 0; x < dimY; x++) {
                Piece piece = getPieceInSquare(x, y);
//                Piece piece = getPieceInSquare(new Position(x, y));
                if (piece.getColor() != color) {
                    continue;
                }

                for (Move possibleMove : piece.getPossibleMoves(this, new Position(x, y), getLastMove(), includeSelfCapture)) {
                    if (isMoveLegal(possibleMove, false, ignoreTurn, includeSelfCapture)) {
                        moves.add(possibleMove);
                    }
                }
            }
        }

//        stateHistory.getCurrentState().setPossibleMoves(moves);
        return moves;
    }

    public void makeMove (Move move) {
        if (isMoveLegal(move, true, false, false)) {
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
        int currentMoveCCount = stateHistory.getCurrentState().getMoveCount();
        stateHistory.getCurrentState().setLastMove(move);
        stateHistory.getCurrentState().setTurn(newTurn.invert());
        stateHistory.getCurrentState().setMoveCount(currentMoveCCount + 1);

        repetitionTracker.add(this);
    }

    public void unMakeMove (int level) {
        for (int i = 0; i < level; i++) {
            undo(stateHistory.getCurrentState().getLastMove());
            stateHistory.pull();
        }
    }

    private void undo (Move lastMove) {
        repetitionTracker.subtract(this);
        lastMove.unmakeMove(board);
        hashCode = lastMove.getIncrementalHash(hashCode, hasher);
    }

    private boolean calculateCheckmate (PieceColor turn) {
        return isCheck(turn) && getAllPossibleMoves(turn).size() == 0;
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
        return repetitionTracker.isDraw() || stateHistory.getCurrentState().getMovesSinceFiftyMoveReset() >= 100
                || (!isCheck() && getAllPossibleMoves().size() == 0);
    }

    public RepetitionTracker getRepetitionTracker() {
        return repetitionTracker;
    }

    @Override
    public String toString() {
        String hPadding = " ";
        String vPadding = "";

        StringBuilder builder = new StringBuilder(vPadding).append(hPadding).append("\n  a b c d e f g h").append(hPadding).append("\n").append(vPadding);
        for (int y = dimY - 1; y >= 0; y--) {
            if (y < dimY - 1) {
                builder.append("\n");
            }

            builder.append(y + 1).append(hPadding);

            for (int x = 0; x < board[y].length; x++) {
                builder.append(board[y][x]);

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
        return hashCode;
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

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < states.size(); i++) {
            BoardState currentState = states.get(i);
            if (i % 2 == 0) {
                builder.append((currentState.getMoveCount() + 1) / 2).append(". ");
            }
            if (i == 0 && states.get(i).getLastMove().getColor() == PieceColor.BLACK) {
                builder.append("... ");
                i += 1;
            }

            builder.append(currentState.getLastMove()).append(" ");
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("rnb1kbnr/pppppppp/8/1q6/3PP3/8/PPP2PPP/RNBQKBNR b - - 0 1");
        System.out.println(board.hashCode());
        System.out.println(board);
        board.makeMove(Move.parseMove("b5b4", PieceColor.BLACK, board));
        System.out.println(board.hashCode());
        System.out.println(board);
        board.unMakeMove(1);
        System.out.println(board.hashCode());
        System.out.println(board);
    }

    public BoardHasher getHasher() {
        return hasher;
    }
}
