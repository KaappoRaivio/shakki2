package chess.move;

import chess.board.Board;
import chess.piece.basepiece.PieceColor;

import java.util.stream.Collectors;

public class MoveParser {
    static Move parseMove(String shortAlgebraic, Board board) {
//        System.out.println();
        return board.getAllPossibleMoves().stream().filter(move -> move.getShortAlgebraicNotation(board).equals(shortAlgebraic)).findAny().orElseThrow(() -> {
            System.out.println(board);
            System.out.println(board.getAllPossibleMoves().stream().map(move -> move.getShortAlgebraicNotation(board)).collect(Collectors.toList()));
            return new RuntimeException(shortAlgebraic + "; "+ board.toFEN());
        });
    }

    public static void main(String[] args) {
//        System.out.println(parseMove(Board.getStartingPosition(), "Nc3"));
        Board board = Board.fromFEN("8/1k6/6K1/8/Q7/8/8/Q2Q4 w - - 0 1");
        System.out.println(Move.parseMove("a1d4", PieceColor.WHITE, board).getShortAlgebraicNotation(board));
    }
}
