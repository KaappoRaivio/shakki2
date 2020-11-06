package chess.board;

import chess.move.Move;

import java.util.List;

public class BoardHelpers {
    public static void executeSequenceOfMoves (Board board, List<String> moves) {
        for (String move : moves) {
            board.makeMove(Move.parseMove(move, board.getTurn(), board));
        }
    }
}
