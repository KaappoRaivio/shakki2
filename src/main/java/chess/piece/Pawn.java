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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(PieceType.PAWN, color, color == PieceColor.WHITE ? "♙" : "♟", 100);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture) {
        Set<Move> moves = new HashSet<>();

        moves.addAll(handleStraightAhead(board, position));
        moves.addAll(handleCapture(board, position, includeSelfCapture));
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
            return getPromotablePieces()
                    .stream()
                    .map(pieceType -> new PromotionMove(position, position.offsetY(getForwardDirection()), board, pieceType))
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    private Set<PieceType> getPromotablePieces () {
        return new HashSet<>(Set.of(
                PieceType.QUEEN,
                PieceType.ROOK,
                PieceType.BISHOP,
                PieceType.KNIGHT
        ));
    }

    private Set<Move> handleStraightAhead (Board board, Position position) {
        Set<Move> moves = new HashSet<>();
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

    private Set<Move> handleCapture (Board board, Position position, boolean includeSelfcapture) {
        Set<Move> moves = new HashSet<>();

        moves.addAll(getSuitableCaptureMoves(board, position, position.offset(1, getForwardDirection(), false), includeSelfcapture));
        moves.addAll(getSuitableCaptureMoves(board, position, position.offset(-1, getForwardDirection(), false), includeSelfcapture));


        return moves;
    }

    private Set<Move> getSuitableCaptureMoves (Board board, Position position, Position offset, boolean includeSelfCapture) {
        Set<Move> moves = new HashSet<>();
        if (offset.verify()) {
            if (board.getPieceInSquare(offset).getColor() == color.invert() || (includeSelfCapture && board.getPieceInSquare(offset).getColor() != PieceColor.NO_COLOR)) {
                if (offset.getY() == getEndFlank()) {
                    moves.addAll(getPromotablePieces()
                            .stream()
                            .map(type -> new PromotionMove(position, offset, board, type))
                            .collect(Collectors.toSet())
                    );
                } else {
                    moves.add(new NormalMove(position, offset, board));
                }
            }
        }
        return moves;
    }

    private Set<Move> handleEnPassant (Board board, Position position, Move lastMove) {
        Set<Move> moves = new HashSet<>();

        Position offset = position.offsetX(1, false);
        if (offset.verify()) {
            Piece piece = board.getPieceInSquare(offset);
            if (piece instanceof Pawn && isEnPassantPossible(lastMove) && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(1, getForwardDirection()))) {
                    moves.add(new EnPassantMove(position, position.offset(1, getForwardDirection()), board));
                }
            }
        }

        offset = position.offsetX(-1, false);
        if (offset.verify()) {
            Piece piece = board.getPieceInSquare(offset);
            if (piece instanceof Pawn && isEnPassantPossible(lastMove) && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(-1, getForwardDirection()))) {
                    moves.add(new EnPassantMove(position, position.offset(-1, getForwardDirection()), board));
                }
            }
        }

        return moves;
    }

    private static boolean isEnPassantPossible(Move lastMove) {
        if (lastMove instanceof NormalMove) {
            NormalMove move = (NormalMove) lastMove;
            return Math.abs(move.getOrigin().getY() - move.getDestination().getY()) == 2;

        } else {
            return false;
        }
    }

//    @Override
//    public int getIndex (Board board, Position position, Move lastMove) {
//        if (handleEnPassant(board, position, lastMove).size() > 0) {
//            return getColor() == PieceColor.WHITE ? 12 : 13;
//        } else {
//            return super.getIndex(board, position, lastMove);
//        }
//    }

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
