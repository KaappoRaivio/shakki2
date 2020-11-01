package chess.board;

import chess.misc.ReadWriter;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import java.nio.file.Files;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testStalemate() {
        Board board = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos1.txt");
        assertTrue(board.isDraw());

        Board board2 = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos2.txt");
        assertTrue(board2.isDraw());
    }

    @Test
    public void testCheck() {
        Board board = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos3.txt");
        assertTrue(board.isCheck(PieceColor.BLACK));

        Board board2 = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos4.txt");
        System.out.println(board2.getAllPossibleMoves(PieceColor.WHITE));
        assertTrue(board2.isCheck(PieceColor.WHITE));
    }

    @Test
    public void testMovegeneration() {
        Board board = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos5.txt");
        assertEquals(
                readMoveSequence("/home/kaappo/git/shakki2/src/main/resources/moves/test_pos5.txt"),
                board.getAllPossibleMoves(PieceColor.WHITE).toString()
        );
    }

    private String readMoveSequence (String path) {
        return ReadWriter.readFile(path).strip();
    }
}