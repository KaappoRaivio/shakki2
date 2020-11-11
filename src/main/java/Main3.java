import chess.board.Board;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import players.treeai.TreeAI;

public class Main3 {
    public static void main(String[] args) {
//        Board board = Board.fromFEN("2k5/8/8/4p2q/2B5/8/2R1P3/K7 w - - 0 1");
//        Board board = Board.fromFEN("5k2/8/3p4/8/8/8/3R4/1K6 w - - 0 1");
//        Board board = Board.fromFEN("5K2/8/3P4/8/8/8/3r4/1k6 b - - 0 1");

        Board board = Board.fromFEN("r3kb1r/1bpq1ppp/p3pn2/1p4B1/2pPP3/P1N5/1P3PPP/R2QKN1R b KQkq - 0 1");


        int depth = 4;
        PieceColor color = PieceColor.BLACK;
        while (true) {
            TreeAI ai = new TreeAI(color, board, depth, 8, false, 0);
            ai.updateValues(board, board.getTurn(), 10);
            System.out.println(board);
            Move move = ai.getMove();
            System.out.println(move);
            board.makeMove(move);
            System.out.println(board);

            depth -= 1;
            color = color.invert();

            if (depth < 1) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        BoardEvaluator evaluator = new BoardEvaluator(4, PieceColor.WHITE);
//        board.makeMove(Move.parseMove("d7
    }
}
