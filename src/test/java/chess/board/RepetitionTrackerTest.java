package chess.board;

import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RepetitionTrackerTest {

    @Test
    public void repetitionTrackerTest() throws NoSuchFieldException, IllegalAccessException {
        Board orig = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/starting_position.txt");
        System.out.println(orig.hashCode());
        orig.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, orig));
        System.out.println(orig.hashCode());
        System.out.println(orig.getRepetitionTracker());
        orig.makeMove(Move.parseMove("e7e5", PieceColor.WHITE, orig));
        System.out.println(orig.getRepetitionTracker());


        Field privateStringField = RepetitionTracker.class.getDeclaredField("counts");
        privateStringField.setAccessible(true);

        Object supposedMap = privateStringField.get(orig.getRepetitionTracker());
        if (supposedMap instanceof Map) {
            Map fieldValue = (Map) supposedMap;
            assertEquals(3, fieldValue.size());
        }

//        System.out.println(orig.hashCode());
//        System.out.println(orig.hashCode());
    }

    @Test
    public void testCastlingKingHash () {
        Board board = Board.fromFEN("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
//        System.out.println(board.hashCode());
        board.makeMove(Move.parseMove("e1e2", PieceColor.WHITE, board));
        board.makeMove(Move.parseMove("e8d8", PieceColor.BLACK, board));
        board.makeMove(Move.parseMove("e2e1", PieceColor.WHITE, board));
        board.makeMove(Move.parseMove("d8e8", PieceColor.BLACK, board));
        board.unMakeMove(4);
//        System.out.println(board.hashCode());

    }

    @Test
    public void testCastlingKingHash2() {
        Board board = Board.fromFEN("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        int initialHash = board.hashCode();
//        System.out.println(board);
//        System.out.println(board.hashCode());
//        board.makeMove(Move.parseMove("O-O", PieceColor.WHITE, board));
//        board.makeMove(Move.parseMove("O-O-O", PieceColor.BLACK, board));
        BoardHelpers.executeSequenceOfMoves(board, List.of("O-O", "O-O-O"));
        assertEquals("incremental hashing after castling should work", new BoardHasher().getFullHash(board), board.hashCode());
        board.unMakeMove(2);
        assertEquals("the resulting hash after undoing the castlings should equal the initial hash", initialHash, board.hashCode());

//        System.out.println(board.hashCode() + ", " + new BoardHasher().getFullHash(board) + ", " + (board.hashCode() == new BoardHasher().getFullHash(board)));
//        System.out.println(board);
    }

    @Test
    public void b() {
        Board board = Board.getStartingPosition();
        BoardHelpers.executeSequenceOfMoves(board, List.of("e2e4", "d7d5", "f1d3", "d5d4", "d3b5"));
        board.unMakeMove(1);
        System.out.println(board);
//        board.unMakeMove(3);

//        Board board2 = Board.getStartingPosition();
//        BoardHelpers.executeSequenceOfMoves(board2, List.of("e2e4", "e7e5", "g1f3", "g8f6", "f1c4"));
//        board2.unMakeMove(1);
//        System.out.println(board2);
    }
}