package players;

import chess.board.Board;

public interface BoardEvaluator {
    double evaluateBoard(Board board, int currentDepth);
}
