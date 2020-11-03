package chess.board;

import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import java.lang.reflect.Field;
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

        Map<Board, Integer> fieldValue = (Map<Board, Integer>) privateStringField.get(orig.getRepetitionTracker());
        assertEquals(2, fieldValue.size());

//        System.out.println(orig.hashCode());
//        System.out.println(orig.hashCode());
    }
}