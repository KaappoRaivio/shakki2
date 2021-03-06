package chess.move;

import chess.board.Board;
import chess.board.BoardHasher;
import misc.Position;
import misc.exceptions.ChessException;
import chess.piece.*;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceType;

import java.util.Objects;

public class PromotionMove extends NormalMove {
    final private Piece promoted;

    public PromotionMove(Position origin, Position destination, Board board, PieceType promoted) {
        super(origin, destination, board);
        switch (promoted) {
            case QUEEN:
                this.promoted = new Queen(color);
                break;
            case ROOK:
                this.promoted = new Rook(color);
                break;
            case BISHOP:
                this.promoted = new Bishop(color);
                break;
            case KNIGHT:
                this.promoted = new Knight(color);
                break;
            default:
                throw new ChessException("Can't promote to " + promoted + "!");
        }
    }

    @Override
    public void makeMove(Piece[][] buffer) {
        super.makeMove(buffer);
        buffer[getDestination().getY()][getDestination().getX()] = promoted;
    }

    @Override
    public void unmakeMove(Piece[][] buffer) {
        super.unmakeMove(buffer);
        buffer[getOrigin().getY()][getOrigin().getX()] = new Pawn(color);
    }

    @Override
    public int getIncrementalHash(int oldHash, BoardHasher hasher) {
        int newHash = super.getIncrementalHash(oldHash, hasher);
        newHash ^= hasher.getPartHash(destination, promoted);

        return newHash;
    }

    @Override
    public String getShortAlgebraicNotation(Board board) {
        String shortAlgebraicNotation = super.getShortAlgebraicNotation(board).replaceAll("\\+", "").replaceAll("#", "");
        shortAlgebraicNotation += "=" + MoveHashMap.moveHashMap.get(promoted.getType()).toUpperCase();

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PromotionMove that = (PromotionMove) o;
        return promoted.equals(that.promoted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), promoted);
    }

    @Override
    public String toString() {
        return super.toString() + "=" + MoveHashMap.moveHashMap.get(promoted.getType()).toUpperCase();
    }
}
