package chess.piece;

import chess.board.Board;
import misc.Position;
import chess.move.Move;
import chess.move.NormalMove;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.HashSet;
import java.util.Set;

public class Bishop extends Piece {
    public Bishop (PieceColor color) {
        super(PieceType.BISHOP, color, "♝", 300);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture, Position barrier) {
        Set<Move> moves = new HashSet<>();

        moves.addAll(getStraightPathMoves(board, position, 1, 1, includeSelfCapture, barrier));
        moves.addAll(getStraightPathMoves(board, position, -1, 1, includeSelfCapture, barrier));
        moves.addAll(getStraightPathMoves(board, position, -1, -1, includeSelfCapture, barrier));
        moves.addAll(getStraightPathMoves(board, position, 1, -1, includeSelfCapture, barrier));

        return moves;
    }



    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, -1.0},
                {-0.33, 0.67, 0.33, 0.33, 0.33, 0.33, 0.67, -0.33},
                {-0.33, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -0.33},
                {-0.33, 0.33, 1.0, 1.0, 1.0, 1.0, 0.33, -0.33},
                {-0.33, 0.67, 0.67, 1.0, 1.0, 0.67, 0.67, -0.33},
                {-0.33, 0.33, 0.67, 1.0, 1.0, 0.67, 0.33, -0.33},
                {-0.33, 0.33, 0.33, 0.33, 0.33, 0.33, 0.33, -0.33},
                {-1.0, -0.33, -0.33, -0.33, -0.33, -0.33, -0.33, 0.2},
        };
    }

    private Set<Move> handleUpRight (Board board, Position position) {
        Set<Move> moves = new HashSet<>();
        int checkX = position.getX() + 1;
        int checkY = position.getY() + 1;

        while (checkX < board.getDimX() && checkY < board.getDimY()) {
            Position newPosition = new Position(checkX, checkY);
            Piece currentPiece = board.getPieceInSquare(newPosition);

            if (currentPiece.getColor() == color) {
                return moves;
            } else if (currentPiece.getColor() == color.invert()) {
                moves.add(new NormalMove(position, newPosition, board));
                return moves;
            } else {
                moves.add(new NormalMove(position, newPosition, board));
            }
            checkX += 1;
            checkY += 1;
        }

        return moves;
    }

    private Set<Move> handleUpLeft (Board board, Position position) {
        Set<Move> moves = new HashSet<>();
        int checkX = position.getX() - 1;
        int checkY = position.getY() + 1;

        while (checkX >= 0 && checkY < board.getDimY()) {
            Position newPosition = new Position(checkX, checkY);
            Piece currentPiece = board.getPieceInSquare(newPosition);

            if (currentPiece.getColor() == color) {
                return moves;
            } else if (currentPiece.getColor() == color.invert()) {
                moves.add(new NormalMove(position, newPosition, board));
                return moves;
            } else {
                moves.add(new NormalMove(position, newPosition, board));
            }
            checkX -= 1;
            checkY += 1;
        }

        return moves;
    }

    private Set<Move> handleDownLeft (Board board, Position position) {
        Set<Move> moves = new HashSet<>();
        int checkX = position.getX() - 1;
        int checkY = position.getY() - 1;

        while (checkX >= 0 && checkY >= 0) {
            Position newPosition = new Position(checkX, checkY);
            Piece currentPiece = board.getPieceInSquare(newPosition);

            if (currentPiece.getColor() == color) {
                return moves;
            } else if (currentPiece.getColor() == color.invert()) {
                moves.add(new NormalMove(position, newPosition, board));
                return moves;
            } else {
                moves.add(new NormalMove(position, newPosition, board));
            }
            checkX -= 1;
            checkY -= 1;
        }

        return moves;
    }

    private Set<Move> handleDownRight (Board board, Position position) {
        Set<Move> moves = new HashSet<>();
        int checkX = position.getX() + 1;
        int checkY = position.getY() - 1;

        while (checkX < board.getDimX() && checkY >= 0) {
            Position newPosition = new Position(checkX, checkY);
            Piece currentPiece = board.getPieceInSquare(newPosition);

            if (currentPiece.getColor() == color) {
                return moves;
            } else if (currentPiece.getColor() == color.invert()) {
                moves.add(new NormalMove(position, newPosition, board));
                return moves;
            } else {
                moves.add(new NormalMove(position, newPosition, board));
            }
            checkX += 1;
            checkY -= 1;
        }

        return moves;
    }
}
