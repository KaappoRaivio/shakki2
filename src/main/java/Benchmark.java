import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import misc.ReadWriter;
import players.RandomAI;
import players.treeai.BasicBoardEvaluator;
import players.treeai.BasicBoardEvaluator2;
import players.treeai.TreeAI;
import runner.CapableOfPlaying;
import runner.Runner;
import runner.UI;
import ui.TtyUI;

import java.util.Collections;
import java.util.Date;

public class Benchmark {
    public static void main(String... args) {

        boolean swap = false;
        int AIDepth = 2;
        int amountOfProcessors = 8;
        boolean useOpeningLibrary = true;

        String filename = "/home/kaappo/git/shakki2/src/main/java/result(" + new Date().toString() + ").txt";
        System.out.println(filename);
        int iteration = 0;
        while (true) {
            Board board = Board.getStartingPosition();
            System.out.println("iteration " + iteration);
            CapableOfPlaying[] players;
            UI ui = new TtyUI();
            if (!swap) {
                players = new CapableOfPlaying[]{
                        new TreeAI("experiment 2", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator2(AIDepth, PieceColor.WHITE)),
                        new TreeAI("control", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator(AIDepth, PieceColor.BLACK)),
                };
//                players = new CapableOfPlaying[]{
//                        new RandomAI(PieceColor.WHITE, "light", ui),
//                        new RandomAI(PieceColor.BLACK, "dark", ui),
//                };
            }
            else {
                players = new CapableOfPlaying[]{
                        new TreeAI("control", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator(AIDepth, PieceColor.WHITE)),
                        new TreeAI("experiment 2", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator2(AIDepth, PieceColor.BLACK)),
                };
//                players = new CapableOfPlaying[]{
//                        new RandomAI(PieceColor.WHITE, "white", ui),
//                        new RandomAI(PieceColor.BLACK, "black", ui),
//                };
            }

            swap = !swap;

            Runner runner = new Runner(board, players, ui, Collections.emptyList());
            PieceColor winner = runner.play(board.getTurn());

            String name;
            if (winner == PieceColor.NO_COLOR) {
                System.out.println("draw " + board);
                name = "draw";
            } else {
                name = players[winner == PieceColor.WHITE ? 0 : 1].getName();
            }

            System.out.println("winner " + name + ", " + board);
            ReadWriter.appendFile(filename, name + " (" + winner + ") wins" + board.conventionalToString() + "\n" + board.getMoveHistoryPretty() + "\n\n\n");
            iteration++;
        }

    }
}
