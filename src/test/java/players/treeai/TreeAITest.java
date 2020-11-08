package players.treeai;

import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TreeAITest {
    @Test
    public void testParallerBoards () {
        long[] counts = new long[8];

        final int mastercount = 10;
        for (int count = 0; count < mastercount; count++) {
            for (int amountOfThreads = 1; amountOfThreads <= 8; amountOfThreads++) {
                List<Thread> threads = new ArrayList<>();
                for (int i = 0; i < amountOfThreads; i++) {
                    Thread thread = new Thread(() -> {
                        Board board = Board.getStartingPosition();
                        for (int a = 0; a < 10000; a++) {
//                            Math.sin(a);
                            board.calculateAllPossibleMoves(PieceColor.WHITE, false ,false);
                            //                        board.getAllPossibleMoves();
                            //                        for (Move move : board.getAllPossibleMoves()) {
                            ////                            board.executeMoveNoChecks(move);
                            ////                            board.unMakeMove(1);
                            //                        }
                        }
                    });

                    threads.add(thread);
                    thread.start();
                }
                long start = System.currentTimeMillis();
                for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException ignored) {}
                }
                long end = System.currentTimeMillis();
                //            System.out.println(amountOfThreads + " " + (end - start));
                counts[amountOfThreads - 1] += (end - start) / amountOfThreads;
                //            System.out.println("took " + (end - start) + " ms with " + amountOfThreads + " cores");
            }
        }

        System.out.println(Arrays.stream(counts).mapToObj(item -> String.valueOf(item / mastercount)).collect(Collectors.joining("\n")));
    }
}