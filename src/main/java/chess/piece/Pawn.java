package chess.piece;

import chess.board.Board;
import misc.exceptions.ChessException;
import misc.Position;
import chess.move.EnPassantMove;
import chess.move.Move;
import chess.move.NormalMove;
import chess.move.PromotionMove;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pawn extends Piece {
    public Pawn (PieceColor color) {
        super(PieceType.PAWN, color, "♟", 100);
    }

    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove) {
        Set<Move> moves = new HashSet<>();

        moves.addAll(handleStraightAhead(board, position));
        moves.addAll(handleCapture(board, position));
        moves.addAll(handleEnPassant(board, position, lastMove, -1));
        moves.addAll(handleEnPassant(board, position, lastMove, 1));
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

    private static final HashSet<PieceType> promotablePieces = new HashSet<>(Set.of(
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
    ));
    private List<Move> handlePromotion (Board board, Position position) {
        List<Move> moves = new ArrayList<>();
        if (position.getY() == getEndFlank() - getForwardDirection() && board.isSquareEmpty(position.offsetY(getForwardDirection()))) {
            for (PieceType pieceType : promotablePieces) {
                PromotionMove move = new PromotionMove(position, position.offsetY(getForwardDirection()), board, pieceType);
                moves.add(move);
            }
        }
        return moves;
    }

    private List<Move> handleStraightAhead (Board board, Position position) {
        List<Move> moves = new ArrayList<>();
        if (position.getY() == getEndFlank() - getForwardDirection()) {
            return moves;
        }

        Position offset = position.offsetY(getForwardDirection(), false);
        if (offset.verify() && board.isSquareEmpty(offset)) {
            NormalMove move = new NormalMove(position, offset, board);
            moves.add(move);

            if (!hasMoved(position) && board.isSquareEmpty(position.offsetY(getForwardDirection() * 2))) {
                NormalMove doubleMove = new NormalMove(position, position.offsetY(getForwardDirection() * 2), board);
                moves.add(doubleMove);
            }
        }


        return moves;
    }

    private boolean hasMoved (Position position) {
        return color == PieceColor.WHITE ? position.getY() != 1 : position.getY() != 6;
    }

    private List<Move> handleCapture(Board board, Position position) {
        List<Move> moves = new ArrayList<>();

        moves.addAll(getSuitableCaptureMoves(board, position, position.offset(1, getForwardDirection(), false)));
        moves.addAll(getSuitableCaptureMoves(board, position, position.offset(-1, getForwardDirection(), false)));


        return moves;
    }

    private final HashSet<PieceType> pieceTypes = new HashSet<>(Set.of(
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
    ));
    private List<Move> getSuitableCaptureMoves(Board board, Position position, Position offset) {
        List<Move> moves = new ArrayList<>();
        if (offset.verify()) {
            if (board.getPieceInSquare(offset).getColor() == color.invert()) {
                if (offset.getY() == getEndFlank()) {
                    for (PieceType type : pieceTypes) {
                        PromotionMove move = new PromotionMove(position, offset, board, type);
                        moves.add(move);
                    }
                } else {
                    NormalMove move = new NormalMove(position, offset, board);
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private List<Move> handleEnPassant (Board board, Position position, Move lastMove, int offsetX) {
        List<Move> moves = new ArrayList<>();

//        int offsetX = 1;
        Position offset = position.offsetX(offsetX, false);
        if (offset.verify()) {
            Piece piece = board.getPieceInSquare(offset);
            if (piece instanceof Pawn && isEnPassantPossible(lastMove) && piece.getColor() == color.invert()) {
                if (board.isSquareEmpty(position.offset(offsetX, getForwardDirection()))) {
                    EnPassantMove enPassantMove = new EnPassantMove(position, position.offset(offsetX, getForwardDirection()), board);
                    moves.add(enPassantMove);
                }
            }
        }
        return moves;
    }

//    @Override
//    public double getValue(int x, int y) {
////        return super.getValue(position) * (7 - getEndFlank()) - position.getY();
//        switch (color) {
//            case BLACK:
//                return super.getValue(x, y) + 5 * (7 - y);
//            case WHITE:
//                return super.getValue(x, y) + 5 * y;
//            default:
//                return 0;
//        }
//    }

    private static boolean isEnPassantPossible(Move lastMove) {
        if (lastMove instanceof NormalMove) {
            NormalMove move = (NormalMove) lastMove;
            return Math.abs(move.getOrigin().getY() - move.getDestination().getY()) == 2 && lastMove.getPiece().getType() == PieceType.PAWN;

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
