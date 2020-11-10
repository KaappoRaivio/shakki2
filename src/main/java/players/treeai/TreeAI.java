package players.treeai;

import chess.board.Board;
import chess.board.Openings;
import chess.move.Move;
import chess.move.NoMove;
import chess.piece.basepiece.PieceColor;
import misc.Splitter;
import runner.CapableOfPlaying;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;

public class TreeAI implements CapableOfPlaying {
    private PieceColor color;
    private Board board;
    private int depth;
    private boolean useOpeningLibrary;
    private int allocatedTime;
    private BoardEvaluator evaluator;
    private int amountOfProcessors;
    private Openings openings;

    public TreeAI(PieceColor color, Board board, int depth, int amountOfProcessors) {
        this(color, board, depth, amountOfProcessors, true, 10000);
    }

    public TreeAI(PieceColor color, Board board, int depth, int amountOfProcessors, boolean useOpeningLibrary, int allocatedTime) {
        this.color = color;
        this.board = board;
        this.depth = depth - 1;
        this.useOpeningLibrary = useOpeningLibrary;
        this.allocatedTime = allocatedTime;
        evaluator = new BoardEvaluator(depth, color);
        this.amountOfProcessors = amountOfProcessors;
        openings = new Openings();
    }

    @Override
    public Move getMove() {
        var possibleOpeningMove = openings.getOpeningMove(board, color);
        if (possibleOpeningMove != null && useOpeningLibrary) {
            System.out.println("Found move from library! " + possibleOpeningMove.getShortAlgebraic(board));
            return possibleOpeningMove;
        }


        Map<Move, Double> values = new HashMap<>();

        long evaluationStart = System.currentTimeMillis();
        Move currentlyBestMove = null ;

        ConcurrentHashMap<Integer, TranspositionTableEntry> sharedTranspositionTable = new ConcurrentHashMap<>();

        List<TreeAIWorker> threads = new ArrayList<>();
        depthIteration: for (int depthIteration = 2; depthIteration <= depth; depthIteration++) {
            threads = new ArrayList<>();
            List<Set<Move>> split = Splitter.splitListInto(board.getAllPossibleMoves(color), amountOfProcessors);

            TreeAIWorker.resetAlphaAndBeta();
            for (int i = 0; i < amountOfProcessors; i++) {
                TreeAIWorker thread = new TreeAIWorker(split.get(i), board.deepCopy(), i, depthIteration, new BoardEvaluator(depth, color), sharedTranspositionTable);
                threads.add(thread);
                System.out.println("\tCreated " + thread);
                thread.start();
            }
//            try {
//                Thread.sleep(wait);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            long wait = allocatedTime - (System.currentTimeMillis() - evaluationStart);
            System.out.println("Waiting for " + wait + " ms");
            for (TreeAIWorker thread : threads) {

                    if (wait <= 0) {
                        System.out.println("Making move with depth " + (depthIteration - 1));
                        break depthIteration;
                    }
                    synchronized (TreeAIWorker.lock) {
                        try {
                            TreeAIWorker.lock.wait(wait);
                        } catch (InterruptedException ignored) {}
                    }
                    wait = allocatedTime - (System.currentTimeMillis() - evaluationStart);

                    System.out.println("\tJoined " + thread + ", " + wait);

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

            System.out.println("Iteration " + depthIteration + ":");
            System.out.println(top4.stream().map(item -> item.getKey().getShortAlgebraic(board) + ": " + item.getValue()).collect(Collectors.joining("\n")));
            currentlyBestMove = top4.get(0).getKey();
        }
        threads.forEach(TreeAIWorker::_stop);
        return currentlyBestMove;

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
