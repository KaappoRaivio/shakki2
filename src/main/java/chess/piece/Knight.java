package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.move.NormalMove;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

import java.util.*;

public class Knight extends Piece {
    public static final List<Position> offsets = List.of(new Position(1, 2), new Position(2, 1), new Position(2, -1, false), new Position(1, -2, false), new Position(-1, -2, false), new Position(-2, -1, false), new Position(-2, 1, false), new Position(-1, 2, false));


    public Knight (PieceColor color) {
        super(PieceType.KNIGHT, color, "♞", 300);
    }

    @Override
    public Pair<Set<Move>, Set<Move>> getPossibleMoves(Board board, Position position, Move lastMove) {
        Pair<Set<Move>, Set<Move>> moves = new Pair<>(new HashSet<>(), new HashSet<>());

        for (Position offset : offsets) {
            Position destination = position.offset(offset, false);
            if (destination.verify()) {
                NormalMove move = new NormalMove(position, destination, board);
//                moves.getSecond().add(move);
//                if (!move.isSelfCapture()) {
                    moves.getFirst().add(move);
//                };
            }
        }

        return moves;
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {-1.0, -0.71, -0.43, -0.43, -0.43, -0.43, -0.71, -1.0},
                {-0.71, -0.14, 0.43, 0.57, 0.57, 0.43, -0.14, -0.71},
                {-0.43, 0.57, 0.71, 0.86, 0.86, 0.71, 0.57, -0.43},
                {-0.43, 0.43, 0.86, 1.0, 1.0, 0.86, 0.43, -0.43},
                {-0.43, 0.57, 0.86, 1.0, 1.0, 0.86, 0.57, -0.43},
                {-0.43, 0.43, 0.71, 0.86, 0.86, 0.71, 0.43, -0.43},
                {-0.71, -0.14, 0.43, 0.43, 0.43, 0.43, -0.14, -0.71},
                {-1.0, -0.71, -0.43, -0.43, -0.43, -0.43, -0.71, -1.0},

        };
    }
}
