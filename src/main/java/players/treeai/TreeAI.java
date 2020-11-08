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
import static java.lang.Math.sin;

public class TreeAI implements CapableOfPlaying {
    private PieceColor color;
    private Board board;
    private int depth;
    private BoardEvaluator evaluator;
    private int amountOfProcessors;

    public TreeAI(PieceColor color, Board board, int depth, int amountOfProcessors) {
        this.color = color;
        this.board = board;
        this.depth = depth - 1;
        evaluator = new BoardEvaluator(depth, color);
        this.amountOfProcessors = amountOfProcessors;
    }

    @Override
    public Move getMove() {
        Map<Move, Double> values = new HashMap<>();


        List<TreeAIWorker> threads = new ArrayList<>();
        List<Set<Move>> split = Splitter.splitListInto(board.getAllPossibleMoves(color), amountOfProcessors);

        TreeAIWorker.resetAlphaAndBeta();
        for (int i = 0; i < amountOfProcessors; i++) {
            TreeAIWorker thread = new TreeAIWorker(split.get(i), board.deepCopy(), i, depth, new BoardEvaluator(depth, color));
            threads.add(thread);
            System.out.println("created " + thread);
            thread.start();
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


        Board board = Board.getStartingPosition();
        TreeAI multiAI = new TreeAI(PieceColor.WHITE, board, 5, 8);
        TreeAI singleAI = new TreeAI(PieceColor.WHITE, board, 5, 1);

        multiAI.updateValues(board, PieceColor.WHITE, 30);
        singleAI.updateValues(board, PieceColor.WHITE, 30);

        long start = System.currentTimeMillis();
        Move move = multiAI.getMove();
        System.out.println("multicore took " + (System.currentTimeMillis() - start) + " ms");

        long start2 = System.currentTimeMillis();
        Move move2 = singleAI.getMove();
        System.out.println("singlecore took " + (System.currentTimeMillis() - start2) + " ms");
        System.out.println(move + ", " + move2);
        System.out.println(board);

//        int amountOfThreads = 8;




//        System.out.println(evaluator.evaluateBoard(board));

    }
}
