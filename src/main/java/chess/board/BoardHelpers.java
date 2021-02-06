package chess.board;

import chess.piece.basepiece.PieceColor;
import chess.piece.basepiece.PieceType;


import java.util.*;
import java.util.stream.Collectors;

public class BoardHelpers {
    public static void executeSequenceOfMoves (Board board, List<String> moves) {
        for (String move : moves) {
            board.makeMove(move);
        }
    }

    public static boolean hasInsufficientMaterial(Board board) {
        return hasInsufficientMaterial(board, PieceColor.WHITE) && hasInsufficientMaterial(board, PieceColor.BLACK);
    }

    private static boolean hasInsufficientMaterial (Board board, PieceColor perspective) {
        Map<PieceType, Integer> pieces = getPieceComposition(board, perspective);

        return pieces.get(PieceType.ROOK) == 0 && pieces.get(PieceType.QUEEN) == 0 && pieces.get(PieceType.PAWN) == 0 && pieces.get(PieceType.BISHOP) < 2;
    }

    public static Map<PieceType, Integer> getPieceComposition(Board board, PieceColor perspective) {
        Map<PieceType, Integer> pieces = new HashMap<>(Map.of(
                PieceType.KING, 0,
                PieceType.QUEEN, 0,
                PieceType.BISHOP, 0,
                PieceType.KNIGHT, 0,
                PieceType.ROOK, 0,
                PieceType.PAWN, 0,
                PieceType.NO_PIECE, 0
        ));

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board.getPieceInSquare(x, y).getColor() == perspective) {
                    pieces.merge(board.getPieceInSquare(x, y).getType(), 1, Integer::sum);
                }
            }
        }
        return pieces;
    }

    private static <T> boolean hasAll (List<T> target, List<T> probes) {
        Map<T, Boolean> contains = probes.stream().collect(Collectors.toMap(probe -> probe, probe -> false));

        for (T t : target) {
            if (contains.containsKey(t)) {
                contains.put(t, true);
            }
        }

        return contains.values().stream().allMatch(a -> a);
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("1k6/8/8/8/8/8/3B1N2/K7 w - - 0 1");
        System.out.println(hasInsufficientMaterial(board));
    }

    public static boolean hasTooLittleMaterial(Board board, int moveCount) {
        return false;
//        if (moveCount > 80) {
//        return new MaterialEvaluator().evaluateBoard(board, 0) < -550;
//        } else {
//            return false;
//        }
//        TreeAI ai = new TreeAI("", board.getTurn(), board, 2, 4, false, new MaterialEvaluator());
//        ai.updateValues(board, board.getTurn(), 0);
//        return ai.evalPosition() < -550;
    }
}
