package misc;

import chess.board.Board;
import chess.move.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Splitter {
    public static <T> List<List<T>> splitListInto(List<T> moves, int amountOfChunks) {
        int surfaceArea = moves.size();


        int remainder = surfaceArea % amountOfChunks;

        List<Integer> lengths = new ArrayList<>();
        for (int i = 0; i < amountOfChunks; i++) {
            lengths.add(surfaceArea / amountOfChunks);
        }

        for (int i = 0; i < remainder; i++) {
            lengths.set(i, lengths.get(i) + 1);
        }

        List<List<T>> result = new ArrayList<>();

        int x = 0;
        for (int chunk : lengths) {

            result.add(moves.subList(x, x + chunk));
            x += chunk;
        }


        return result;
    }

    public static void main(String[] args) {
//        Board board = Board.fromFEN("5K2/8/3P4/8/8/8/3r4/1k6 b - - 0 1");

        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i");
//        List<Move> list = board.getAllPossibleMoves();

        System.out.println(splitListInto(list, 3) + ", " + list);
    }
}
