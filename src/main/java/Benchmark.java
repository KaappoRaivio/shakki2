import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import misc.ReadWriter;
import players.treeai.BasicBoardEvaluator;
import players.treeai.BasicBoardEvaluator2;
import players.treeai.TreeAI;
import runner.CapableOfPlaying;
import runner.Runner;
import runner.UI;
import ui.TtyUI;

import java.util.Collections;

public class Benchmark {
    public static void main(String... args) {

        boolean swap = false;


        int AIDepth = 3;
        int amountOfProcessors = 4;
        boolean useOpeningLibrary = true;

        String filename = "/home/kaappo/git/shakki2/src/main/java/result" + System.currentTimeMillis() + ".txt";

        for (int iteration = 0; iteration < 20; iteration++) {
            Board board = Board.getStartingPosition();
            System.out.println("iteration " + iteration);
            CapableOfPlaying[] players;
            if (swap)
                players = new CapableOfPlaying[]{
                        new TreeAI("control", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator(AIDepth, PieceColor.WHITE)),
                        new TreeAI("experiment", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator2(AIDepth, PieceColor.BLACK)),
                };
            else
                players = new CapableOfPlaying[]{
                        new TreeAI("experiment", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator2(AIDepth, PieceColor.WHITE)),
                        new TreeAI("control", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new BasicBoardEvaluator(AIDepth, PieceColor.BLACK)),
                };
            swap = !swap;

            UI ui = new TtyUI();
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
            ReadWriter.appendFile(filename, "\n" + name + " (" + winner + ") wins" + "\n" + board.conventionalToString() + "\n" + board.getMoveHistoryPretty() + "\n");
        }

    }
}
