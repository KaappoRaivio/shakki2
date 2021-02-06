package analyzeData;

import chess.board.Board;
import chess.move.Move;
import misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PGNGame {
    private List<Pair<String, Integer>> boards = new ArrayList<>();
    private PGNGameResult winner;
    private String id;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (var pair : boards) {
            builder.append(pair.getFirst()).append(", ").append(pair.getSecond());
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    public PGNGame (List<String> moves, String result, String id) {
        this.id = id;
        switch (result) {
            case "1-0":
                winner = PGNGameResult.WHITE;
                break;
            case "0-1":
                winner = PGNGameResult.BLACK;
                break;
            case "1/2-1/2":
                winner = PGNGameResult.DRAW;
                break;
            default:
                throw new RuntimeException("Unknown result " + result);
        }

        Board board = Board.getStartingPosition();
        for (String moveString : moves) {
            try {
                Move move = Move.parseMove(moveString, board.getTurn(), board, true);
                board.makeMove(move);
                boards.add(new Pair<>(board.toFEN(), null));
            } catch (RuntimeException e) {
                System.out.println("ID: " + id);
                System.out.println("MOVES: " + moves);
                throw e;
            }
        }

    }

    public void attachScores (List<Integer> centipawnScores) {
        for (int i = 0; i < boards.size(); i++) {
            boards.set(i, new Pair<>(boards.get(i).getFirst(), centipawnScores.get(i)));
        }
    }
}
