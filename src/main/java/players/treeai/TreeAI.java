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
    private int amountOfProcessors = 1;
    private Openings openings;

    public TreeAI(PieceColor color, Board board, int depth, int amountOfProcessors) {
        this(color, board, depth, amountOfProcessors, true, 10000);
    }

    public TreeAI(PieceColor color, Board board, int depth, int amountOfProcessors, boolean useOpeningLibrary, int allocatedTime) {
        this.color = color;
        this.board = board;
        this.depth = depth;
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


        ConcurrentHashMap<Integer, TranspositionTableEntry> sharedTranspositionTable = new ConcurrentHashMap<>();

        //        depthIteration: for (int depthIteration = depth; depthIteration <= depth; depthIteration++) {
        List<TreeAIWorker> threads = new ArrayList<>();
        List<List<Move>> split = Splitter.splitListInto(board.getAllPossibleMoves(color), amountOfProcessors);
        System.out.println(split + ", " + board.getAllPossibleMoves(color));

        for (int i = 0; i < amountOfProcessors; i++) {
            TreeAIWorker thread = new TreeAIWorker(split.get(i), board.deepCopy(), i, depth, new BoardEvaluator(depth, color), sharedTranspositionTable);
            threads.add(thread);
            System.out.println("\tCreated " + thread);
            thread.start();
        }

        for (TreeAIWorker thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("\tJoined " + thread);

        }

        var moveHistory = new HashMap<Move, List<Move>>();

        for (TreeAIWorker worker : threads) {
            if (!worker.isReady()) throw new RuntimeException("Thread not ready!");
            values.putAll(worker.getResult());
            moveHistory.putAll(worker.moveHistorys);
        }
        System.out.println(moveHistory);

        List<Map.Entry<Move, Double>> top4 = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Map.Entry<Move, Double> value = values
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElse(Map.entry(NoMove.NO_MOVE, -1e40));
            values.remove(value.getKey());
            top4.add(value);
        }

        System.out.println(top4.stream().map(item -> item.getKey().getShortAlgebraic(board) + ": " + item.getValue() + ", " + moveHistory.get(item.getKey())).collect(Collectors.joining("\n")));

        threads.forEach(TreeAIWorker::_stop);
        return top4.get(0).getKey();

    }


    @Override
    public void updateValues(Board board, PieceColor turn, int moveCount) {
        if (turn != board.getTurn()) {
            throw new RuntimeException("Turns are not the same " + turn + ", " + board.getTurn());
        }

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
