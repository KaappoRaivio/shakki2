package misc;

import chess.board.Board;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaverTest {

    @Test
    public void benchmarkDeepcopy() {
        Board board = Board.getStartingPosition();



        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            board.deepCopy();
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
}