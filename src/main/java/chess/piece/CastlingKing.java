package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.misc.exceptions.ChessException;
import chess.move.*;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CastlingKing extends King {

    public CastlingKing (PieceColor color) {
        super(color, "♚̅", 500);
    }


    @Override
    public Pair<Set<Move>, Set<Move>> getPossibleMoves(Board board, Position position, Move lastMove) {
        Pair<Set<Move>, Set<Move>> possibleMoves = super.getPossibleMoves(board, position, lastMove);
        Pair<Set<Move>, Set<Move>> result = new Pair<>(new HashSet<>(), new HashSet<>());
        mergePairs(result, handleKingSideCastling(board, position));
        mergePairs(result, handleQueenSideCastling(board, position));
        mergePairs(result, new Pair<>(possibleMoves.getFirst().stream().map(move -> {
            NormalMove normalMove = ((NormalMove) move);
            return new CastlingKingMove(normalMove.getOrigin(), normalMove.getDestination(), board); }).collect(Collectors.toSet()),
                possibleMoves.getSecond().stream().map(move -> {
                    NormalMove normalMove = ((NormalMove) move);
                    return new CastlingKingMove(normalMove.getOrigin(), normalMove.getDestination(), board); }).collect(Collectors.toSet()))
        );

        return result;
    }

    private Pair<Set<Move>, Set<Move>> handleKingSideCastling (Board board, Position position) {

        try {

            Piece supposedRook = board.getPieceInSquare(position.offsetX(3));

            if (supposedRook.getType() != PieceType.ROOK || !(supposedRook instanceof CastlingRook)) {
                return new Pair<>(Collections.emptySet(), Collections.emptySet());
            }

            if (board.isSquareUnderThreat(position)
                    || board.isSquareUnderThreat(position.offsetX(1), color)
                    || board.isSquareUnderThreat(position.offsetX(2), color)
                    || !board.isSquareEmpty(position.offsetX(1))
                    || !board.isSquareEmpty(position.offsetX(2))) {
                return new Pair<>(Collections.emptySet(), Collections.emptySet());
            }

            return new Pair<>(Set.of(new CastlingMove(CastlingType.KING_SIDE, color, board)), Set.of());
        } catch (ChessException e) {
            return new Pair<>(Collections.emptySet(), Collections.emptySet());
        }


    }

    private Pair<Set<Move>, Set<Move>> handleQueenSideCastling (Board board, Position position) {

        try {
            Piece supposedRook = board.getPieceInSquare(position.offsetX(-4));

            if (supposedRook.getType() != PieceType.ROOK || !(supposedRook instanceof CastlingRook)) {
                return new Pair<>(Collections.emptySet(), Collections.emptySet());
            }

            if (board.isSquareUnderThreat(position)
                    || board.isSquareUnderThreat(position.offsetX(-1), color)
                    || board.isSquareUnderThreat(position.offsetX(-2), color)
                    || !board.isSquareEmpty(position.offsetX(-1))
                    || !board.isSquareEmpty(position.offsetX(-2))
                    || !board.isSquareEmpty(position.offsetX(-3))) { // Rook's path can be threatened, so no threat check for position.offsetX(-3)
                return new Pair<>(Collections.emptySet(), Collections.emptySet());
            }

            return new Pair<>(Set.of(new CastlingMove(CastlingType.QUEEN_SIDE, color, board)), Collections.emptySet());
        } catch (ChessException e) {
            return new Pair<>(Collections.emptySet(), Collections.emptySet());
        }
    }

}
