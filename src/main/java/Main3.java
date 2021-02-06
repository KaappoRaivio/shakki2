
import xyz.niflheim.stockfish.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Query;
import xyz.niflheim.stockfish.engine.enums.QueryType;
import xyz.niflheim.stockfish.engine.enums.Variant;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

public class Main3 {
    public static void main(String[] args) throws StockfishInitException {
//        Board board = Board.fromFEN("Q3Q3/5qpk/5p1p/5P1K/6PP/8/8/8 w - - 1 7");
//        BoardEvaluator evaluator = new CandinateEvaluator(4, board.getTurn());

//        TreeAI ai = new TreeAI("ai", board.getTurn(), board, depth, 1, false, evaluator);
//        System.out.println(evaluator.evaluateBoard(board, 0));
        StockfishClient client = new StockfishClient.Builder()
                .setInstances(4)
                .setVariant(Variant.BMI2)
                .setPath("/home/kaappo/git/shakki2/assets/engines/")
                .build();

        Query query = new Query.Builder(QueryType.Score)
                .setFen("1k6/2q5/8/8/8/8/8/1K6 b - - 0 1")
                .build();

        client.submit(query, System.out::println);
    }
}
