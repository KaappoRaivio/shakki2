package chess.board;

import chess.misc.Position;
import chess.move.Move;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardState implements Serializable {
    private Position whiteKingPosition;
    private Position blackKingPosition;

    private int movesSinceFiftyMoveReset;
    private Move lastMove;
    private PieceColor turn;


    private Boolean isCheckForWhite;
    private Boolean isCheckForBlack;

    private Boolean isCheckmateForWhite;
    private Boolean isCheckmateForBlack;

    private List<Move> possibleMovesWhite;
    private List<Move> possibleMovesBlack;


    private int moveCount;

    BoardState(Position whiteKingPosition, Position blackKingPosition, int movesSinceFiftyMoveReset, Move lastMove, PieceColor turn, int moveCount) {
        this.whiteKingPosition = whiteKingPosition;
        this.blackKingPosition = blackKingPosition;
        this.movesSinceFiftyMoveReset = movesSinceFiftyMoveReset;
        this.lastMove = lastMove;
        this.turn = turn;
        this.moveCount = moveCount;
//        possibleMovesWhite = null;
//        possibleMovesBlack = null;
    }

//    BoardState(Position whiteKingPosition, Position blackKingPosition, int movesSinceFiftyMoveReset, Move lastMove, PieceColor turn, int moveCount) {
//        this.whiteKingPosition = whiteKingPosition;
//        this.blackKingPosition = blackKingPosition;
//        this.movesSinceFiftyMoveReset = movesSinceFiftyMoveReset;
//        this.lastMove = lastMove;
//        this.turn = turn;
//        this.moveCount = moveCount;
////        this.isCheck = isCheck;
////        this.possibleMoves = possibleMoves;
////        this.isCheckmate = isCheckmate;
//    }

    public BoardState(BoardState other) {
        this.whiteKingPosition = other.whiteKingPosition;
        this.blackKingPosition = other.blackKingPosition;
        this.movesSinceFiftyMoveReset = other.movesSinceFiftyMoveReset;
        this.moveCount = other.moveCount;
        this.lastMove = other.lastMove;
        this.turn = other.turn;
//        possibleMovesWhite = new Pair<>(null, null);
//        possibleMovesBlack = new Pair<>(null, null);
//        this.isCheckForWhite = other.isCheckForWhite;
//        this.isCheckForBlack = other.isCheckForBlack;
//        this.isCheck = other.isCheck;
//        this.isCheckmate = other.isCheckmate;
//        this.possibleMoves = other.possibleMoves;
//        this.moveCount = other.moveCount;
    }

//    public BoardState(Position whiteKing, Position second, int fiftyMoveReset, Move parseMove, PieceColor turn, int moveCount) {

//    }

    public Position getKingPosition (PieceColor color) {
        switch (color) {
            case BLACK:
                return blackKingPosition;
            case WHITE:
                return whiteKingPosition;
            default:
                return null;
        }
    }

    public int getMovesSinceFiftyMoveReset () {
        return movesSinceFiftyMoveReset;
    }

    public Move getLastMove () {
        return lastMove;
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void setWhiteKingPosition (Position whiteKingPosition) {
        this.whiteKingPosition = whiteKingPosition;
    }

    public void setBlackKingPosition (Position blackKingPosition) {
        this.blackKingPosition = blackKingPosition;
    }
    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void setMovesSinceFiftyMoveReset (int movesSinceFiftyMoveReset) {
        this.movesSinceFiftyMoveReset = movesSinceFiftyMoveReset;
    }

    public void setTurn (PieceColor turn) {
        this.turn = turn;
    }

    public void setLastMove (Move lastMove) {
        this.lastMove = lastMove;
    }

    public Boolean isCheck (PieceColor turn) {
        switch (turn) {
            case BLACK:
                return isCheckForBlack;
            case WHITE:
                return isCheckForWhite;
            default:
                return null;
        }
    }

    public Boolean isCheckmate (PieceColor turn) {
        switch (turn) {
            case BLACK:
                return isCheckmateForBlack;
            case WHITE:
                return isCheckmateForWhite;
            default:
                return null;
        }
    }

    public void setCheckmate (PieceColor turn, boolean checkmate) {
        switch (turn) {
            case BLACK:
                isCheckmateForBlack = checkmate;
                break;
            case WHITE:
                isCheckmateForWhite = checkmate;
                break;
        }
    }

//    public Set<Move> getPossibleMoves() {
//        return possibleMoves;
//    }

    @Override
    public String toString() {
        return "BoardState{" +
                "whiteKingPosition=" + whiteKingPosition +
                ", blackKingPosition=" + blackKingPosition +
                ", movesSinceFiftyMoveReset=" + movesSinceFiftyMoveReset +
                ", lastMove=" + lastMove +
                ", turn=" + turn +
                ", isCheckForWhite=" + isCheckForWhite +
                ", isCheckForBlack=" + isCheckForBlack +
                ", moveCount=" + moveCount +
                '}';
    }

    public List<Move> getPossibleMoves (PieceColor color) {
        switch (color) {
            case BLACK:
                return possibleMovesBlack;
            case WHITE:
                return possibleMovesWhite;
            default:
                return null;
        }
    }

    public void setPossibleMoves (PieceColor color, List<Move> moves) {
        switch (color) {
            case BLACK:
                possibleMovesBlack = moves;
                break;
            case WHITE:
                possibleMovesWhite = moves;
                break;
        }
    }

//    public void setPossibleMoves(Set<Move> moves) {
//        possibleMoves = moves;
//    }

    public void setCheck(PieceColor color, boolean check) {
        switch (color) {
            case BLACK:
                isCheckForBlack = check;
                break;
            case WHITE:
                isCheckForWhite = check;
                break;
        }
    }
}
