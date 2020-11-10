import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import players.Player;
import players.treeai.TreeAI;
import runner.CapableOfPlaying;
import runner.Runner;
import runner.UI;
import ui.TtyUI;

import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main (String[] args) {
//        Board orig = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/starting_position.txt");
        Board board = Board.getStartingPosition();
//        Board board = Board.fromFEN("rnbq1rk1/ppp1ppb1/3p1n1p/6N1/3PP2B/2NB4/PPP2PPP/R2QK2R b KQ - 0 1");
//        board.makeMove(Move.parseMove("h6g5", PieceColor.BLACK, board));

//        Board board = Board.fromFEN("r1bq1knr/1pp3pp/p1nb4/4p2Q/4N3/PB6/1PPP1PPP/R1B1K1NR w KQ - 0 1");
//        Board board = Board.fromFEN("5rk1/6pp/8/4N3/3Q4/8/8/3K4 w - - 0 1");
//        System.out.println(board.getAllPossibleMoves());
//        System.out.println(board);
//        System.exit(0);

        UI ui = new TtyUI();

        CapableOfPlaying[] players;
        Scanner scanner = new Scanner(System.in);
        int AIDepth = 10;
        int allocatedTime = 25000;
        boolean useOpeningLibrary = true;
        while (true) {
            System.out.print("Ai plays as: ");
//            String line = scanner.nextLine();
            String line = "none";

            if (line.equals("white")) {
                players = new CapableOfPlaying[]{
                        new TreeAI(PieceColor.WHITE, board, AIDepth, 8, useOpeningLibrary, allocatedTime),
                        new Player(PieceColor.BLACK, "chess.com", ui),
        //                new TreeAI(PieceColor.WHITE, board, 3, 8),
                };
                break;
            } else if (line.equals("black")) {
                players = new CapableOfPlaying[]{
                        new Player(PieceColor.WHITE, "chess.com", ui),
                        new TreeAI(PieceColor.BLACK, board, AIDepth, 8),
                };
                break;
            } else if (line.equals("none")) {
                players = new CapableOfPlaying[]{
                        new TreeAI(PieceColor.WHITE, board, AIDepth, 8, useOpeningLibrary, allocatedTime),
                        new TreeAI(PieceColor.BLACK, board, AIDepth, 8, useOpeningLibrary, allocatedTime),
                };
                break;
            }
        }


        Runner runner = new Runner(board, players, ui, Collections.emptyList());
        runner.play(board.getTurn());

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

