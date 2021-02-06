package chess.move;

import chess.board.Board;
import chess.board.BoardHelpers;
import chess.piece.basepiece.PieceColor;
import misc.Triple;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MoveParserTest {
    @Test
    public void testMoveParsing () {
        var testCases = List.of(
                new Triple<String, String, String>("r1b1rk2/p2nbpp1/2p2n1p/q3pBB1/1pP4P/1P2PNN1/P1Q2PP1/R3K2R w KQ - 3 29", "Rd1", "a1d1"),
                new Triple<String, String, String>("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", "Nc3", "b1c3"),
                new Triple<String, String, String>("3R3R/1k6/6K1/8/8/8/8/8 w - - 0 1", "Rdf8", "d8f8"),
                new Triple<String, String, String>("8/1k6/6K1/8/8/r7/8/r7 b - - 0 1", "R3a2", "a3a2"),
                new Triple<String, String, String>("8/1k6/6K1/8/Q7/8/8/Q2Q4 w - - 0 1", "Qa1d4", "a1d4"),
                new Triple<String, String, String>("rnbqkbnr/pppp1ppp/8/4p3/5PP1/8/PPPPP2P/RNBQKBNR b KQkq - 0 1", "Qh4#", "d8h4"),
                new Triple<String, String, String>("rnbqkbnr/ppp1pppp/8/3p4/4P3/1Q6/PPPP1PPP/RNB1KBNR w KQkq - 0 1", "Qb5+", "b3b5")
        );

        for (var testCase : testCases) {
            Board board = Board.fromFEN(testCase.getFirst());

            Move expected = Move.parseMove(testCase.getThird(), board.getTurn(), board);
            Move actual = null;
            try  {
                actual = Move.parseMove(testCase.getSecond(), board.getTurn(), board, true);
            } catch (Exception e) {
                System.out.println(expected.getShortAlgebraicNotation(board));
                System.out.println(testCase);
                throw e;
            }
            assertEquals(expected, actual);
        }


    }

    @Test
    public void testShortAlgebraicNotationConvention () {
//        new Triple<String, String, String>(, "Rd1", "a1d1");
//        Board board = Board.fromFEN("r1b1kb1r/pp1npppp/1qn5/3pP3/B1pP4/2P2N1P/PP3PP1/RNBQK2R b KQkq d3 0 18");
        Board board = Board.fromFEN("r1b1kb1r/pp1npppp/1qn5/3pP3/B1p5/2P2N1P/PP1P1PP1/RNBQK2R w KQkq - 0 9");
        BoardHelpers.executeSequenceOfMoves(board, List.of("d2d4"));
        System.out.println(board);
        System.out.println(board.toFEN());
        System.out.println(board.getLastMove());
        System.out.println(board.getAllPossibleMoves());
//        System.out.println(board);
//        System.out.println(board.getAllPossibleMoves());
//cxd3
//        Move move = Move.parseMove("a1d1", PieceColor.WHITE, board);
//        System.out.println(move.getShortAlgebraicNotation(board));
    }

}