package players.treeai;

import chess.board.Board;
import chess.move.Move;
import chess.move.NoMove;
import chess.piece.basepiece.PieceColor;
import misc.Splitter;
import runner.CapableOfPlaying;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.max;

public class TreeAI implements CapableOfPlaying {
    private PieceColor color;
    private Board board;
    private int depth;
    private BoardEvaluator evaluator;

    public TreeAI(PieceColor color, Board board, int depth) {
        this.color = color;
        this.board = board;
        this.depth = depth - 1;
        evaluator = new BoardEvaluator(depth);
    }

    @Override
    public Move getMove() {
        Map<Move, Double> values = new HashMap<>();


        List<TreeAIWorker> threads = new ArrayList<>();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
//        int availableProcessors = 1;
        List<Set<Move>> split = Splitter.splitListInto(board.getAllPossibleMoves(color), availableProcessors);

        for (int i = 0; i < availableProcessors; i++) {
            TreeAIWorker thread = new TreeAIWorker(split.get(i), board.deepCopy(), i, depth, new BoardEvaluator(depth));
            threads.add(thread);
            thread.start();
            System.out.println("created " + thread);
        }
        for (TreeAIWorker thread : threads) {
            try {
                thread.join();
                System.out.println("Joined " + thread);
            } catch (InterruptedException e) {
            }
        }

        for (TreeAIWorker worker : threads) {
            values.putAll(worker.getResult());
        }
//        for (Move move : board.getAllPossibleMoves(color)) {
//            board.executeMoveNoChecks(move);
//            values.put(move, -deepEvaluateBoard(board));
//            board.unMakeMove(1);
//        }
//o-o
//        System.out.println(values);
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        Map<Move, Double> top4 = new HashMap<>();
        List<Map.Entry<Move, Double>> top4 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Map.Entry<Move, Double> value = values
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElse(Map.entry(NoMove.NO_MOVE, -1e40));
            values.remove(value.getKey());
            top4.add(value);
        }

        System.out.println(top4.stream().map(item -> item.getKey() + ": " + item.getValue()).collect(Collectors.joining("\n")));
        return top4.get(0).getKey();
    }


    @Override
    public void updateValues(Board board, PieceColor turn, int moveCount) {
        this.board = board;
    }

    @Override
    public PieceColor getColor() {
        return color;
    }

    public static void main(String[] args) {
        //        Board board = Board.getStartingPosition();
//        BoardEvaluator evaluator = new BoardEvaluator(4);
//        Board board = Board.fromFEN("1k1R4/8/1K6/8/8/8/8/8 b - - 0 1");
        Board board = Board.fromFEN("k7/8/2K5/8/8/4Q3/8/8 w - - 0 1");
        System.out.println(board);
        TreeAI ai = new TreeAI(PieceColor.WHITE, board, 3);
        ai.updateValues(board, PieceColor.WHITE, 30);
        Move move = ai.getMove();
        System.out.println(move);
        board.makeMove(move);

        System.out.println(board);
//        System.out.println(evaluator.evaluateBoard(board));

    }
}
