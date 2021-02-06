package chess.piece;

import chess.board.Board;
import misc.Position;
import chess.move.*;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;

import java.util.*;
import java.util.stream.Collectors;

public class CastlingKing extends King {

    public CastlingKing (PieceColor color) {
        super(color, "♚̅", 100);
    }


    @Override
    public Set<Move> getPossibleMoves(Board board, Position position, Move lastMove, boolean includeSelfCapture, Position barrier) {
        Set<Move> possibleMoves = super.getPossibleMoves(board, position, lastMove, includeSelfCapture, barrier);
        Set<Move> result = new HashSet<>();
        result.addAll(handleKingSideCastling(board, position));
        result.addAll(handleQueenSideCastling(board, position));
        result.addAll(possibleMoves.stream().map(move -> {
            NormalMove normalMove = ((NormalMove) move);
            return new CastlingKingMove(normalMove.getOrigin(), normalMove.getDestination(), board); }).collect(Collectors.toSet())
        );

        return result;
    }

    @Override
    public String conventionalToString() {
        return super.conventionalToString() + "̅";
    }

    private List<Move> handleKingSideCastling (Board board, Position position) {
            Piece supposedRook = board.getPieceInSquare(position.offsetX(3));

            if (supposedRook.getType() != PieceType.ROOK || !(supposedRook instanceof CastlingRook)) {
                return Collections.emptyList();
            }

            if (board.isSquareUnderThreat(position)
                    || board.isSquareUnderThreat(position.offsetX(1), color)
                    || board.isSquareUnderThreat(position.offsetX(2), color)
                    || !board.isSquareEmpty(position.offsetX(1))
                    || !board.isSquareEmpty(position.offsetX(2))) {
                return Collections.emptyList();
            }

            return Collections.singletonList(new CastlingMove(CastlingType.KING_SIDE, color, board));
    }

    private List<Move> handleQueenSideCastling (Board board, Position position) {
        Piece supposedRook = board.getPieceInSquare(position.offsetX(-4));

        if (supposedRook.getType() != PieceType.ROOK || !(supposedRook instanceof CastlingRook)) {
            return Collections.emptyList();
        }

        if (board.isSquareUnderThreat(position)
                || board.isSquareUnderThreat(position.offsetX(-1), color)
                || board.isSquareUnderThreat(position.offsetX(-2), color)
                || !board.isSquareEmpty(position.offsetX(-1))
                || !board.isSquareEmpty(position.offsetX(-2))
                || !board.isSquareEmpty(position.offsetX(-3))) { // Rook's path can be threatened, so no threat check for position.offsetX(-3)
            return Collections.emptyList();
        }

        return Collections.singletonList(new CastlingMove(CastlingType.QUEEN_SIDE, color, board));
    }
}
