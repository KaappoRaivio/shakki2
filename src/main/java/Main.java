import chess.board.Board;
import chess.piece.basepiece.PieceColor;

public class Main {
    public static void main (String[] args) {
        Board orig = Board.fromFile("/home/kaappo/git/shakki2/src/main/resources/boards/starting_position.txt");
        Board board = orig.deepCopy();

//        System.out.println(board.getPieceInSquare(Position.fromString("B1")).getPossibleMoves(board, Position.fromString("B1"), new NoMove()));
//        System.out.println(board.isMoveLegal(Move.parseMove("B1A3", PieceColor.WHITE, board)));

        PieceColor turn = PieceColor.WHITE;
        int a = 0;

        long starttime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            var moves = board.getAllPossibleMoves(turn);
            if (moves.size() == 0) {
                board = orig.deepCopy();
                a++;
                continue;
            }

            board.makeMove(moves.stream().findFirst().orElseThrow());
            System.out.println(board.getStateHistory().getCurrentState());
            turn = turn.invert();
//            System.out.println(board);

        }

        System.out.println(a);

        long endtime = System.currentTimeMillis();
        System.out.println((endtime - starttime) / 1000.0);
    }
}

