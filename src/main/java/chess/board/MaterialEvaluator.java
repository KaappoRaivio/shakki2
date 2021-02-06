package chess.board;

import chess.piece.basepiece.PieceColor;
import players.BoardEvaluator;
import players.BoardEvaluatorHelpers;


public class MaterialEvaluator implements BoardEvaluator {
    public MaterialEvaluator() {
        this(0, PieceColor.WHITE);
    }

    public MaterialEvaluator(int aiDepth, PieceColor white) {

    }
    

    @Override
        public double evaluateBoard(Board board1, int currentDepth) {
            return BoardEvaluatorHelpers.getMaterialAmount(board1, board1.getTurn())
                    - BoardEvaluatorHelpers.getMaterialAmount(board1, board1.getTurn().invert());
        }
}
