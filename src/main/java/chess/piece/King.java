package chess.piece;

import chess.board.Board;
import chess.misc.Position;
import chess.move.Move;
import chess.move.NormalMove;
import chess.piece.basepiece.Piece;
import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;
import misc.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class King extends Piece {
    public static final List<Position> offsets = List.of(
            new Position (1, 1),
            new Position (1, 0),
            new Position (1, -1, false),
            new Position (0, -1, false),
            new Position (-1, -1, false),
            new Position (-1, 0, false),
            new Position (-1, 1, false),
            new Position (0, 1)
    );

    public King (PieceColor color) {
        this(color, "♚", 100);
    }

    King (PieceColor color, String symbol, int value) {
        super(PieceType.KING, color, symbol, value);
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
//                }
            }
        }

        return moves;
    }

    @Override
    protected double[][] getPieceSquareTable () {
        return new double[][]{
                {0.75, 1.0, 0.5, 0.25, 0.25, 0.5, 1.0, 0.75},
                {0.75, 0.75, 0.25, 0.25, 0.25, 0.25, 0.75, 0.75},
                {0.0, -0.25, -0.25, -0.25, -0.25, -0.25, -0.25, 0.0},
                {-0.25, -0.5, -0.5, -0.75, -0.75, -0.5, -0.5, -0.25},
                {-0.5, -0.75, -0.75, -1.0, -1.0, -0.75, -0.75, -0.5},
                {-0.5, -0.75, -0.75, -1.0, -1.0, -0.75, -0.75, -0.5},
                {-0.5, -0.75, -0.75, -1.0, -1.0, -0.75, -0.75, -0.5},
                {-0.5, -0.75, -0.75, -1.0, -1.0, -0.75, -0.75, -0.5},

        };
    }


    private boolean canCastle (Board board, Position position) {
        return this instanceof CastlingKing;
    }

//    @Override
//    public int getIndex (Board board, Position position, Move lastMove) {
//        if (canCastle(board, position)) {
//            return getColor() == PieceColor.WHITE ? 14 : 15;
//        }
//        return super.getIndex(board, position, lastMove);
//    }
}
