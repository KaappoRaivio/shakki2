import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import players.BoardEvaluator;
import players.CandinateEvaluator;
import players.Player;

import players.evaluators.StockfishEvaluator;
import players.treeai.TreeAI;
import runner.CapableOfPlaying;
import runner.Runner;
import runner.UI;
import ui.TtyUI;

import java.util.Collections;
import java.util.Scanner;

public class Main {
    public static void main (String[] args) {
        Board board = Board.getStartingPosition();
//        Board board = Board.fromFEN("4Q3/Pq4pk/5p1p/5P1K/6PP/8/8/8 w - - 0 1");
//        board.makeMove("e8g6");
//        System.out.println(board);
//        BoardEvaluator blackEvaluator = new CandinateEvaluator(4, PieceColor.BLACK);
//        System.out.println(blackEvaluator.evaluateBoard(board, 4));
//        TreeAI ai = new TreeAI("ai", PieceColor.BLACK, board, 4, 8, false, blackEvaluator);
//        System.out.println(ai.evalPosition());
//        System.exit(0);


//        board.makeMove("a7a8=B");
//        Board board = Board.fromFEN("r2q3r/1pp1ppk1/pn3np1/4N3/3P2Q1/8/PPP2P2/2K3RR w - - 0 1");
//        BoardHelpers.executeSequenceOfMoves(board, List.of("e3b3", "f3b3", "e1e3", "b3e3"));
//        board.useExpensiveDrawCalculation(false);

//        Board board = Board.fromFEN("r2qkbnr/ppp2ppp/2np4/4N2b/2B1P3/2N4P/PPPP1PP1/R1BQK2R b KQkq - 0 1");
//        BoardHelpers.executeSequenceOfMoves(board, List.of("f6e4", "c3e4", "b7e4", "f1e2"));

//        Board board = Board.fromFEN("5QRR/8/1Q6/8/8/5PPP/8/K6k w - - 0 1");

//        System.out.println(board.getAllPossibleMoves());
//        board.makeMove(Move.parseMove("f8a3", PieceColor.BLACK, board));
//        Board board = Board.fromFEN("r3kb1r/1bpq1ppp/p3pn2/1p4B1/2pPP3/P1N5/1P3PPP/R2QKB1R w KQkq - 0 11");

//        board.makeMove(Move.parseMove("h6g5", PieceColor.BLACK, board));

//        Board board = Board.fromFEN("r1bq1knr/1pp3pp/p1nb4/4p2Q/4N3/PB6/1PPP1PPP/R1B1K1NR w KQ - 0 1");
//        Board board = Board.fromFEN("5rk1/6pp/8/4N3/3Q4/8/8/3K4 w - - 0 1");
//        System.out.println(board.getAllPossibleMoves());
//        System.out.println(board);
//        System.exit(0);

        UI ui = new TtyUI();

        CapableOfPlaying[] players;
        Scanner scanner = new Scanner(System.in);
        int AIDepth = 0;
        int allocatedTime = 30000;
        boolean useOpeningLibrary = false;
        label:
        while (true) {
            System.out.print("Ai plays as: ");
//            String line = scanner.nextLine();
//            String line = board.getTurn().toString().toLowerCase();
            String line = "both";
//            String line = "white";
//            String line = "black";

            BoardEvaluator whiteEvaluator = new CandinateEvaluator(AIDepth, PieceColor.WHITE);
            BoardEvaluator blackEvaluator = new StockfishEvaluator();
            switch (line) {
                case "white":
                    players = new CapableOfPlaying[]{
                            new TreeAI("ai", PieceColor.WHITE, board, AIDepth, 8, useOpeningLibrary, whiteEvaluator),
                            new Player(PieceColor.BLACK, "chess.com", ui),
                            //                new TreeAI(PieceColor.WHITE, board, 3, 8),
                    };
                    break label;
                case "black":
                    players = new CapableOfPlaying[]{
                            new Player(PieceColor.WHITE, "chess.com", ui),
                            new TreeAI("ai", PieceColor.BLACK, board, AIDepth, 8, useOpeningLibrary, blackEvaluator),
                    };
                    break label;
                case "both":
                    players = new CapableOfPlaying[]{
                            new TreeAI("ai", PieceColor.WHITE, board, AIDepth, 8, useOpeningLibrary, whiteEvaluator),
                            new TreeAI("ai", PieceColor.BLACK, board, AIDepth, 8, useOpeningLibrary, blackEvaluator),
                    };
                    break label;
                case "none":
                    players = new CapableOfPlaying[]{
                            new Player(PieceColor.WHITE, "white", ui),
                            new Player(PieceColor.BLACK, "black", ui),
                    };
                    break label;
            }
        }


        Runner runner = new Runner(board, players, ui, Collections.emptyList());
        System.out.println(runner.play(board.getTurn()));

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

