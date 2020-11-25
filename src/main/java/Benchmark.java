import chess.board.Board;
import chess.board.MaterialEvaluator;
import chess.piece.basepiece.PieceColor;
import misc.ReadWriter;
import org.apache.commons.lang3.ArrayUtils;
import players.treeai.CandinateEvaluator;
import players.treeai.CurrentBestEvaluator;
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
        int AIDepth = 3;
        int amountOfProcessors = 4;
        boolean useOpeningLibrary = true;

        String filename = "/home/kaappo/git/shakki2/src/main/java/result (" + new Date().toString() + ").txt";
        System.out.println(filename);
        int iteration = 0;
        Board board = Board.getStartingPosition();

        while (true) {
            board = Board.getStartingPosition();
            CapableOfPlaying[] players = getPlayers(board, AIDepth, amountOfProcessors, useOpeningLibrary, swap);

            System.out.println("iteration " + iteration);
            UI ui = new TtyUI();


            Runner runner = new Runner(board, players, ui, Collections.emptyList());
            PieceColor winner = runner.play(board.getTurn(), 50);

            String name;
            if (winner == PieceColor.NO_COLOR) {
                System.out.println("draw " + board);
                name = "draw";
            } else {
                name = players[winner == PieceColor.WHITE ? 0 : 1].getName();
            }

            System.out.println("winner " + name + ", " + board);
            ReadWriter.appendFile(filename, String.format("\n%s; %s; %s; %s", name, winner, board.toFEN(), board.getMoveHistoryPretty()));
            iteration++;
            swap = !swap;
        }

    }

    private static CapableOfPlaying[] getPlayers (Board board, int AIDepth, int amountOfProcessors, boolean useOpeningLibrary, boolean swap) {
        if (swap) {
            return new CapableOfPlaying[]{
                    new TreeAI("control", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new MaterialEvaluator(AIDepth, PieceColor.WHITE)),
                    new TreeAI("candinate", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new CandinateEvaluator(AIDepth, PieceColor.BLACK)),
            };
        } else {
            return new CapableOfPlaying[]{
                    new TreeAI("candinate", PieceColor.WHITE, board, AIDepth, amountOfProcessors, useOpeningLibrary, new CandinateEvaluator(AIDepth, PieceColor.WHITE)),
                    new TreeAI("control", PieceColor.BLACK, board, AIDepth, amountOfProcessors, useOpeningLibrary, new MaterialEvaluator(AIDepth, PieceColor.BLACK)),
            };
        }
    }
}
