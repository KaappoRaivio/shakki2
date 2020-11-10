package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.move.NormalMove;
import chess.move.CastlingRookMove;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CastlingRook extends Rook {
    public CastlingRook (PieceColor color) {
        super(color, "♜̅");
    }

    @Override
    public Pair<Set<Move>, Set<Move>> getPossibleMoves(Board board, Position position, Move lastMove) {
        Pair<Set<Move>, Set<Move>> possibleMoves = super.getPossibleMoves(board, position, lastMove);
        Pair<Set<Move>, Set<Move>> result = new Pair<>(new HashSet<>(), new HashSet<>());
        mergePairs(result, new Pair<>(possibleMoves.getFirst().stream().map(move -> {
                    NormalMove normalMove = ((NormalMove) move);
                    return new CastlingRookMove(normalMove.getOrigin(), normalMove.getDestination(), board); }).collect(Collectors.toSet()),
                possibleMoves.getSecond().stream().map(move -> {
                    NormalMove normalMove = ((NormalMove) move);
                    return new CastlingRookMove(normalMove.getOrigin(), normalMove.getDestination(), board); }).collect(Collectors.toSet()))
        );
        return result;
    }
}
