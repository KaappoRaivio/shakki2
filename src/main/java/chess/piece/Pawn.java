package chess.piece;

import chess.board.Board;
import chess.misc.exceptions.ChessException;
import chess.misc.Position;
import chess.move.EnPassantMove;
import chess.move.Move;
import chess.move.NormalMove;
import chess.move.PromotionMove;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(PieceType.PAWN, color, color == PieceColor.WHITE ? "♙" : "♟", 100);
    }

    @Override
    public Set<Move> getPossibleMoves (Board board, Position position, Move lastMove) {
        Set<Move> moves = new LinkedHashSet<>();

        moves.addAll(handleStraightAhead(board, position));
        moves.addAll(handleCapture(board, position));
        moves.addAll(handleEnPassant(board, position, lastMove));
        moves.addAll(handlePromotion(board, position));

        return moves;
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {-0.43, -0.43, -0.43, -0.43, -0.43, -0.43, -0.43, -0.43},
                {-0.29, -0.14, -0.14, -1.0 , -1.0 , -0.14, -0.14, -0.29},
                {-0.29, -0.57, -0.71, -0.43, -0.43, -0.71, -0.57, -0.29},
                {-0.43, -0.43, -0.43,  0.14,  0.14, -0.43, -0.43, -0.43},
                {-0.29, -0.29, -0.14,  0.29,  0.29, -0.14, -0.29, -0.29},
                { 0.14,  0.14,  0.14,  0.43,  0.43,  0.14,  0.14,  0.14},
                { 3.0,   3.0 ,  3.0 ,  3.0 ,  3.0 ,  3.0 ,  3.0 ,  3.0 },
                {-0.43, -0.43, -0.43, -0.43, -0.43, -0.43, -0.43, -0.43},

        };
    }

    private Set<Move> handlePromotion (Board board, Position position) {
        if (position.getY() == getEndFlank() - getForwardDirection() && board.isSquareEmpty(position.offsetY(getForwardDirection()))) {
            return Set.of(new PromotionMove(position, position.offsetY(getForwardDirection()), board, PieceType.QUEEN),
                    new PromotionMove(position, position.offsetY(getForwardDirection()), board, PieceType.ROOK),
                    new PromotionMove(position, position.offsetY(getForwardDirection()), board, PieceType.BISHOP),
                    new PromotionMove(position, position.offsetY(getForwardDirection()), board, PieceType.KNIGHT)
            );
        } else {
            return Collections.emptySet();
        }
    }

    private Set<Move> handleStraightAhead (Board board, Position position) {
        Set<Move> moves = new LinkedHashSet<>();
        if (position.getY() == getEndFlank() - getForwardDirection()) {
            return moves;
        }

        Position offset = position.offsetY(getForwardDirection(), false);
        if (offset.verify() && board.isSquareEmpty(offset)) {
            moves.add(new NormalMove(position, offset, board));

            if (!hasMoved(position) && board.isSquareEmpty(position.offsetY(getForwardDirection() * 2))) {
                moves.add(new NormalMove(position, position.offsetY(getForwardDirection() * 2), board));
            }

        }


        return moves;
    }

    private boolean hasMoved (Position position) {
        return color == PieceColor.WHITE ? position.getY() != 1 : position.getY() != 6;
    }

    private Set<Move> handleCapture (Board board, Position position) {
        Set<Move> moves = new LinkedHashSet<>();


        Position offset = position.offset(1, getForwardDirection(), false);
        if (offset.verify()) {
            if (board.getPieceInSquare(offset).getColor() == color.invert()) {
                moves.add(new NormalMove(position, offset, board));
            }
        }

        offset = position.offset(-1, getForwardDirection(), false);
        if (offset.verify()) {
            if (board.getPieceInSquare(offset).getColor() == color.invert()) {
                moves.add(new NormalMove(position, offset, board));
            }
        }

        return moves;
    }

    private Set<Move> handleEnPassant (Board board, Position position, Move lastMove) {
        Set<Move> moves = new LinkedHashSet<>();

        Position offset = position.offsetX(1, false);
        if (offset.verify()) {
            Piece piece = board.getPieceInSquare(offset);
            if (piece instanceof Pawn && isEnPassantLegal(lastMove) && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(1, getForwardDirection()))) {
                    moves.add(new EnPassantMove(position, position.offset(1, getForwardDirection()), board));
                }
            }
        }

        offset = position.offsetX(-1, false);
        if (offset.verify()) {
            Piece piece = board.getPieceInSquare(offset);
            if (piece instanceof Pawn && isEnPassantLegal(lastMove) && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(-1, getForwardDirection()))) {
                    moves.add(new EnPassantMove(position, position.offset(-1, getForwardDirection()), board));
                }
            }
        }

        return moves;
    }

    private static boolean isEnPassantLegal (Move lastMove) {
        if (lastMove instanceof NormalMove) {
            NormalMove move = (NormalMove) lastMove;
            return Math.abs(move.getOrigin().getY() - move.getDestination().getY()) == 2;

        } else {
            return false;
        }
    }

    @Override
    public int getIndex (Board board, Position position, Move lastMove) {
        if (handleEnPassant(board, position, lastMove).size() > 0) {
            return getColor() == PieceColor.WHITE ? 12 : 13;
        } else {
            return super.getIndex(board, position, lastMove);
        }
    }

    private int getEndFlank () {
        switch (color) {
            case WHITE:
                return 7;
            case BLACK:
                return 0;
            default:
                throw new ChessException("");
        }
    }
}
