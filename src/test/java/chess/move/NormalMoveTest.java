package chess.move;

import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import static org.junit.Assert.*;

public class NormalMoveTest {

    @Test
    public void testShortAlgebraicNotationGeneration() {
        Board board = Board.fromFEN("rnbqkbnr/pp1ppp1p/8/6p1/5N1P/2N1K3/PPPPPPP1/R6R w kq - 0 1");
        assertEquals("When two pieces can reach the same position and have same flanks, the file should be included",
                "Rac1", Move.parseMove("a1c1", PieceColor.WHITE, board).getShortAlgebraic(board));
        assertEquals("When two pieces don't share flanks of files, the file should be included",
                "Ncd5", Move.parseMove("c3d5", PieceColor.WHITE, board).getShortAlgebraic(board));
        assertEquals("Pawn moves should only include the destination square",
                "d4", Move.parseMove("d2d4", PieceColor.WHITE, board).getShortAlgebraic(board));
        assertEquals("Pawn captures should include the file", "hxg5", Move.parseMove("h4g5", PieceColor.WHITE, board).getShortAlgebraic(board));
    }
}