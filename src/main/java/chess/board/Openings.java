package chess.board;

import misc.ReadWriter;
import misc.Triple;
import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import misc.Pair;

import java.util.*;

public class Openings {
    private List<Triple<String, String, PieceColor>> openings = new ArrayList<>();

    public Openings() {
        String[] lines = ReadWriter.readFile("/home/kaappo/git/shakki2/src/main/resources/openings/openings.opn").split("\n");
        for (String line : lines) {
            String[] split = line.split("1");
            PieceColor dontPlayWithColor;
            if (split[0].strip().startsWith("NOBLACK")) {
                dontPlayWithColor = PieceColor.BLACK;
            } else if (split[0].strip().startsWith("NOWHITE")){
                dontPlayWithColor = PieceColor.WHITE;
            } else {
                dontPlayWithColor = PieceColor.NO_COLOR;
            }

            openings.add(new Triple<>(split[0].strip(),"1" + split[1].strip(), dontPlayWithColor));
        }

//        System.out.println(openings);
    }

    public List<Move> getPossibleOpenings (Board board, PieceColor perspective) {
        List<Pair<String, String>> openingLines = new ArrayList<>();

        String moveHistoryPretty = board.getMoveHistoryPretty();
        System.out.println("Searching for " + moveHistoryPretty);
        for (var opening : openings) {
//            System.out.println(opening.getSecond());
            if (opening.getThird() != perspective && opening.getSecond().startsWith(moveHistoryPretty)) {
                openingLines.add(new Pair<>(opening.getSecond(), opening.getSecond().substring(moveHistoryPretty.length())));
            }
        }
        List<Move> possibleMoves = new ArrayList<>();
        System.out.println(perspective);
        for (var openingLine : openingLines) {
//            System.out.println(openingLine);
            for (Move move : board.getAllPossibleMoves(perspective)) {
                board.executeMoveNoChecks(move);
                String queryString = (perspective == PieceColor.WHITE ? board.getStateHistory().getCurrentState().getMoveCount() / 2 + ". " : "") + board.getLastMove().toString();
//                System.out.println(openingLine);
//                System.out.println(queryString);
                if (openingLine.getSecond().startsWith(queryString)) {
                    possibleMoves.add(move);
                }
                board.unMakeMove(1);
            }
        }
        return possibleMoves;
    }

    public Move getOpeningMove (Board board, PieceColor perspective) {
        List<Move> possibleOpenings = getPossibleOpenings(board, perspective);
        if (possibleOpenings.size() == 0) return null;
        return possibleOpenings.get(new Random().nextInt(possibleOpenings.size()));
    }

    public static void main(String[] args) {
        Openings openings = new Openings();
        Board board = Board.getStartingPosition();
//        board.makeMove("e2e4");
//        board.makeMove("e7e5");
//        board.makeMove("g1f3");
        System.out.println(board.getMoveHistoryPretty());

        while (true) {
            Move openingMove = openings.getOpeningMove(board, board.getTurn());
//        System.out.println(openingMove);
            if (openingMove == null) break;
            board.makeMove(openingMove);
            System.out.println(board);
        }
//        System.out.println(board);
//        System.out.println(board.getStateHistory().getCurrentState().getMoveCount());
//        board.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board));
//        List<Move> possibleOpenings = openings.getPossibleOpenings(board, PieceColor.BLACK);
//        System.out.println(possibleOpenings);
//        System.out.println(possibleOpenings.get(new Random().nextInt(possibleOpenings.size())));
    }
}
