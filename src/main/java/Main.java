import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import players.Player;
import players.treeai.TreeAI;
import runner.CapableOfPlaying;
import runner.Runner;
import runner.UI;
import ui.TtyUI;

import java.util.Collections;

public class Main {
    public static void main (String[] args) {
//        Board orig = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/starting_position.txt");
        Board board = Board.getStartingPosition();

        UI ui = new TtyUI();

        CapableOfPlaying[] players = {
                new TreeAI(PieceColor.WHITE, board, 3),
                new Player(PieceColor.BLACK, "kaappo", ui),
        };

        Runner runner = new Runner(board, players, ui, Collections.emptyList());
        runner.play(PieceColor.WHITE);

//        Board board = orig.deepCopy();

//        System.out.println(board.getPieceInSquare(Position.fromString("B1")).getPossibleMoves(board, Position.fromString("B1"), new NoMove()));
//        System.out.println(board.isMoveLegal(Move.parseMove("B1A3", PieceColor.WHITE, board)));

//        PieceColor turn = PieceColor.WHITE;
//        int a = 0;
//
//        long starttime = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) {
//            var moves = board.getAllPossibleMoves();
//            if (moves.size() == 0) {
//                board = orig.deepCopy();
//                a++;
//                continue;
//            }
//
//            board.makeMove(moves.stream().findFirst().orElseThrow());
//            System.out.println(board.getStateHistory().getCurrentState());
//            turn = turn.invert();
////            System.out.println(board);
//
//        }

//        System.out.println(a);
//
//        long endtime = System.currentTimeMillis();
//        System.out.println((endtime - starttime) / 1000.0);

    }
}

