import chess.board.Board;
import chess.move.Move;

public class Qperft {
    private static int count;
    
    public static void main(String[] args) {
        Board board = Board.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w QqKk - 0 1");
        board.useExpensiveDrawCalculation(true);
//        Board board = Board.fromFEN("8/4p1PP/p2P4/b1p1K1p1/Nk4BR/4P1p1/2p5/8 w - - 0 1");
//        while (true) {
            System.out.println("Started");
            long start = System.currentTimeMillis();
            dfs(board, 6);
            long end = System.currentTimeMillis();

            System.out.println(count + ", " + count * 1000.0 / (end - start) + ", " + (end - start));
            count = 0;
            System.out.println(board);
//        }
    }

    private static void dfs(Board board, int depth) {
        dfs(board, depth, depth);
    }

    private static void dfs (Board board, int depth, int current) {
        if (current == 0 || board.isCheckmate() || board.isDraw()) {
            count++;
        } else {
            for (Move move : board.getAllPossibleMoves()) {
//                board.makeMove(move);
                board.executeMoveNoChecks(move);
                dfs(board, depth, current - 1);
                board.unMakeMove(1);
            }
        }
    }
}
