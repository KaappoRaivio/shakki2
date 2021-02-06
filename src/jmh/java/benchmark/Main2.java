package benchmark;

import chess.board.Board;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import players.BoardEvaluator;
import players.CurrentBestEvaluator;
import players.evaluators.StockfishEvaluator;

import java.io.IOException;
import java.util.List;

public class Main2 {


    @State(Scope.Thread)
    public static class MyState {
        public final BoardEvaluator evaluator = new CurrentBestEvaluator(0, PieceColor.WHITE);
        public final BoardEvaluator evaluator2 = new StockfishEvaluator();
        public final Board board = Board.getStartingPosition();
    }


//    @Benchmark
//    @Fork(value = 3, warmups = 3)
//    public double currentEvaluator (MyState state) {
//        return state.evaluator.evaluateBoard(state.board, 0);
//    }
//
//    @Benchmark
//    @Fork(value = 3, warmups = 3)
//    public double stockfishEvaluator (MyState state) {
//        return state.evaluator2.evaluateBoard(state.board, 0);
//    }

    @Benchmark
    @Fork(value = 3, warmups = 3)
    @Warmup(iterations = 5)
    public List<Move> benchmarkMoveGeneration (MyState state) {
        return state.board.calculateAllPossibleMoves(PieceColor.WHITE, false);
    }

    public static void main(String[] args) throws IOException, RunnerException {
//        Board board = Board.fromFEN("4Q3/Pq4pk/5p1p/5P1K/6PP/8/8/8 w - - 0 1");

//        CapableOfPlaying a = new TreeAI(null, PieceColor.WHITE, board, 3, 8, false, new CurrentBestEvaluator(3, PieceColor.WHITE));
//        org.openjdk.jmh.Main.main(args);
//        System.out.println(evaluator.evaluateBoard(board, 0));
//        System.out.println(evaluator.evaluateBoard(board, 0));
//
//        CapableOfPlaying a = new TreeAI(null, PieceColor.WHITE, board, 3, 8, false, evaluator);
//        a.updateValues(board, PieceColor.WHITE, 0);
//        System.out.println(a.getMove());

//        BoardHelpers.executeSequenceOfMoves(board, List.of("e8g6", "h7g8", "g6e8", "g8h7"));
//        System.out.println(board);
//        int depth = 0;
//        BoardEvaluator blackEvaluator = new CandinateEvaluator(depth, board.getTurn());
//
//        TreeAI ai = new TreeAI("ai", board.getTurn(), board, depth, 1, false, blackEvaluator);
//        System.out.println(ai.evalPosition());
    }
}
