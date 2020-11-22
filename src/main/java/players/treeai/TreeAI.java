package players.treeai;

import chess.board.Board;
import chess.board.Openings;
import chess.move.Move;
import chess.move.NoMove;
import chess.piece.basepiece.PieceColor;
import misc.Splitter;
import misc.TermColor;
import org.apache.cayenne.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import runner.CapableOfPlaying;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TreeAI implements CapableOfPlaying {
    private String name;
    private final PieceColor color;
    private Board board;
    private final int depth;
    private final boolean useOpeningLibrary;
    private BoardEvaluator evaluator;
    private int amountOfProcessors = 1;
    private Openings openings;


    public TreeAI(String name, PieceColor color, Board board, int depth, int amountOfProcessors, boolean useOpeningLibrary, BoardEvaluator evaluator) {
        this.name = name;
        this.color = color;
        this.board = board;
        this.depth = depth;
        this.useOpeningLibrary = useOpeningLibrary;
        this.evaluator = evaluator;
        this.amountOfProcessors = amountOfProcessors;

        openings = new Openings();
    }


    @Override
    public Move getMove() {
        var possibleOpeningMove = openings.getOpeningMove(board, color);
        if (possibleOpeningMove != null && useOpeningLibrary) {
            System.out.println("Found move from library! " + possibleOpeningMove.getShortAlgebraicNotation(board));
            return possibleOpeningMove;
        }


        Map<Move, Double> values = new HashMap<>();


//        ConcurrentHashMap<Integer, TranspositionTableEntry> sharedTranspositionTable = new ConcurrentHashMap<>();
        ConcurrentLinkedHashMap<Integer, TranspositionTableEntry> sharedTranspositionTable = new ConcurrentLinkedHashMap.Builder<Integer, TranspositionTableEntry>()
                .maximumWeightedCapacity(1_000_000)
                .build();

        //        depthIteration: for (int depthIteration = depth; depthIteration <= depth; depthIteration++) {
        List<TreeAIWorker> threads = new ArrayList<>();
        List<Move> allPossibleMoves = board.getAllPossibleMoves(color);
//        List<Move> allPossibleMoves = List.of(Move.parseMove("f8c5", PieceColor.WHITE, board));
        List<List<Move>> split = Splitter.splitListInto(allPossibleMoves, amountOfProcessors);

//        BasicBoardEvaluator evaluator = new BasicBoardEvaluator(depth, color);
        try {
            System.out.println(((BasicBoardEvaluator2) evaluator).getGameStage(board));
        } catch (Exception e) {
            System.out.println(((BasicBoardEvaluator) evaluator).getGameStage(board));
        }
        board.useExpensiveDrawCalculation(false);
        System.out.println(TermColor.ANSI_FAINT.getEscape());
        for (int i = 0; i < amountOfProcessors; i++) {
            TreeAIWorker thread = new TreeAIWorker(split.get(i), board.deepCopy(), i, depth, evaluator, sharedTranspositionTable);
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
        System.out.println(TermColor.ANSI_RESET.getEscape());

        var moveHistory = new HashMap<Move, String>();
        System.out.println(sharedTranspositionTable.size());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (TreeAIWorker worker : threads) {
            if (!worker.isReady()) throw new RuntimeException("Thread not ready!");
            values.putAll(worker.getResult());
//            System.out.println(worker.evaluated);
            moveHistory.putAll(worker.moveHistorys);
        }
//        System.out.println(moveHistory);
//        System.out.println("values: " + values);

        board.useExpensiveDrawCalculation(true);
        for (Move move : values.keySet()) {
            board.makeMove(move);
            if (board.isDraw()) {
                values.put(move, 0.0);
            }
            board.unMakeMove(1);
        }

        List<Map.Entry<Move, Double>> top4 = new ArrayList<>();
        while (values.size() > 0) {
            Map.Entry<Move, Double> value = values
                    .entrySet()
                    .stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElse(Map.entry(NoMove.NO_MOVE, -1e40));
            values.remove(value.getKey());
            top4.add(value);
        }

        System.out.println(TermColor.ANSI_BOLD.getEscape() + "Evaluation for position " + board.toFEN() + TermColor.ANSI_RESET.getEscape());

        System.out.println(top4.stream().map(item -> {
            String val1 = TermColor.ANSI_BOLD.getEscape() + item.getKey().getShortAlgebraicNotation(board) + TermColor.ANSI_RESET.getEscape();
            double value = item.getValue();
            String val2 = value > 1e6 ? "↑↑↑↑↑↑↑↑" : value < -1e6 ? "↓↓↓↓↓↓↓↓" : String.format("%8.2f", value);
            String val3 = moveHistory.get(item.getKey());

            return String.format("%15s: %s, %-100s", val1, val2, val3);
        }).collect(Collectors.joining("\n")));

        threads.forEach(TreeAIWorker::_stop);
        return top4.get(0).getKey();

    }


    @Override
    public void updateValues(Board board, PieceColor turn, int moveCount) {
        if (turn != board.getTurn()) {
            System.exit(1);
            throw new RuntimeException("Turns are not the same " + turn + ", " + board.getTurn());
        }

        this.board = board;
    }

    @Override
    public PieceColor getColor() {
        return color;
    }

    @Override
    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        //        Board board = Board.getStartingPosition();
//        BoardEvaluator evaluator = new BoardEvaluator(4);
//        Board board = Board.fromFEN("1k1R4/8/1K6/8/8/8/8/8 b - - 0 1");


//        Board board = Board.getStartingPosition();
//        TreeAI multiAI = new TreeAI(PieceColor.WHITE, board, 5, 8);
//        TreeAI singleAI = new TreeAI(PieceColor.WHITE, board, 5, 1);
//
//        multiAI.updateValues(board, PieceColor.WHITE, 30);
//        singleAI.updateValues(board, PieceColor.WHITE, 30);
//
//        long start = System.currentTimeMillis();
//        Move move = multiAI.getMove();
//        System.out.println("multicore took " + (System.currentTimeMillis() - start) + " ms");
//
//        long start2 = System.currentTimeMillis();
//        Move move2 = singleAI.getMove();
//        System.out.println("singlecore took " + (System.currentTimeMillis() - start2) + " ms");
//        System.out.println(move + ", " + move2);
//        System.out.println(board);

//        int amountOfThreads = 8;




//        System.out.println(evaluator.evaluateBoard(board));

    }

    public BoardEvaluator getEvaluator() {
        return evaluator;
    }
}
