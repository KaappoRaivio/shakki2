package chess.board;

import chess.move.Move;
import chess.piece.basepiece.PieceColor;
import misc.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testStalemate() {
//        Board board = Board.fromF
//        ile("/home/kaappo/git/shakki2/src/main/resources/boards/test_pos1.txt");

        Board board = Board.fromFEN("k7/2Q5/8/3K4/8/8/8/8 b - - 0 1");
        assertTrue(board.isDraw());

        Board board2 = Board.fromFEN("k7/P7/1K6/8/8/8/8/8 b - - 0 1");
        assertTrue(board2.isDraw());

        Board board3 = Board.getStartingPosition();
        assertFalse(board3.isDraw());
        System.out.println(board3);
        System.out.println(board3.getAllPossibleMoves());
        board3.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board3));
        System.out.println(board3.getAllPossibleMoves());
        assertFalse(board3.isDraw());
    }

    @Test
    public void testDrawByRepetition() {
        Board board = Board.fromFEN("8/7R/4K3/8/4k3/8/8/8 w - - 0 1");

//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E6E7", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E4E3", PieceColor.BLACK, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E7E6", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E3E4", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());

        board.makeMove(Move.parseMove("E6E7", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E4E3", PieceColor.BLACK, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E7E6", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());
        board.makeMove(Move.parseMove("E3E4", PieceColor.WHITE, board));
//        System.out.println(board + ", " + board.isDraw());
        System.out.println(board.getRepetitionTracker());

        assertTrue(board.isDraw());

    }

    @Test
    public void testUndo() {
        Board board = Board.getStartingPosition();
        assertEquals(PieceColor.WHITE, board.getTurn());

        board.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board));
        assertEquals(PieceColor.BLACK, board.getTurn());

        board.makeMove(Move.parseMove("e7e5", PieceColor.BLACK, board));
        assertEquals(PieceColor.WHITE, board.getTurn());

        board.unMakeMove(1);
        assertEquals(PieceColor.BLACK, board.getTurn());

        board.unMakeMove(1);
        assertEquals(PieceColor.WHITE, board.getTurn());

        board.makeMove(Move.parseMove("d2d4", PieceColor.WHITE, board));
        assertEquals(PieceColor.BLACK, board.getTurn());
    }

    @Test
    public void testCheck() {
        Board board = Board.fromFEN("1k6/P7/1K6/8/8/8/8/8 b - - 0 1");
        System.out.println(board);
        assertTrue(board.isCheck());

        Board board2 = Board.fromFEN("8/8/8/8/2K5/5k2/3n4/8 w - - 0 1");
        assertTrue(board2.isCheck());
    }



    @Test
    public void testCheckmate() {
        List<String> FENs = List.of(
                "rn1qkbnr/ppp1pppp/3p4/8/5PPb/8/PPPPP2P/RNBQKBNR w KQkq - 1 2",
                "rn1q1bnr/ppp1kBp1/3p3p/3NN3/4P3/8/PPPP1PPP/R1BbK2R b KQkq - 2 4",
                "6rk/1p1p1Npp/1bp5/p7/4P3/PP4P1/1K5P/8 b - - 0 1"
        );

        for (String FEN : FENs) {
            Board board = Board.fromFEN(FEN);
            System.out.println(board);
            assertTrue(board.isCheckmate());
//            assertFalse(board.isCheckMate(board.getTurn().invert()));
        }
    }

    @Test
    public void testHashCode1() {
        Board board1 = Board.getStartingPosition();
        Board board2 = Board.getStartingPosition();

        assertEquals(board1.hashCode(), board2.hashCode());

        board1.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board1));
        board1.unMakeMove(1);

        assertEquals(board1.hashCode(), board2.hashCode());
    }

    @Test
    public void testHashCode2() {
        Board board1 = Board.getStartingPosition();
        Board board2 = Board.getStartingPosition();

        BoardHelpers.executeSequenceOfMoves(board1, List.of("e2e4", "e7e5", "g1f3", "b7b6"));
        BoardHelpers.executeSequenceOfMoves(board2, List.of("g1f3", "b7b6", "e2e4", "e7e5"));

        assertEquals("two different boards should have the same hashcode even if they have been achieved through a different path",
                board1.hashCode(), board2.hashCode());
    }

    @Test
    public void testHashCode3() {
        Board board1 = Board.getStartingPosition();
//        BoardHelpers.executeSequenceOfMoves(board1, List.of("e2e4"));
        board1.makeMove(Move.parseMove("e2e4", PieceColor.WHITE, board1));
        Board board2 = Board.fromFEN("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");

        System.out.println(board1 + ", " + board1.hashCode() + ", " + new BoardHasher().getFullHash(board1));
        System.out.println(board2 + ", " + board2.hashCode() + ", " + new BoardHasher().getFullHash(board2));
        assertEquals("incremental hashcode should equal a full incremental hashcode", board1.hashCode(), board2.hashCode());
    }

    @Test
    public void testMoveGeneration1() {
        List<Pair<String, String>> FENs = List.of(
                new Pair<>("8/6K1/3k4/8/5p2/8/4P3/8 w - - 0 1", "e3, e4, Kh8, Kh7, Kh6, Kg6, Kf6, Kf7, Kf8, Kg8"),
                new Pair<>("1q6/P7/1K4k1/8/5p2/8/8/8 w - - 0 1", "Kc6, Kc5, Ka5, Ka6, axb8=Q, axb8=B, axb8=N, axb8=R"),
                new Pair<>("1k6/P7/1K6/8/5pq1/8/8/8 w - - 0 1", "Kc6, Kc5, Kb5, Ka5, Ka6, a8=Q, a8=R, a8=B, a8=N"),
                new Pair<>("8/5p1p/p6r/P4K2/1p1kp1RP/3B2P1/Q7/5N1b w - - 0 1", "Nh2, Nd2, Ne3, Qa3, Qa4, Qa1, Qb2, Qc2, Qd2, Qe2, Qf2, Qg2, Qh2, Qb3, Qc4, Qd5, Qe6, Qxf7, Qb1, Bxe4, Bc4, Bb5, Bxa6, Bc2, Bb1, Be2, Rg5, Rg6, Rg7, Rg8, Rf4, Rxe4, h5, Kg5, Kf4"),
                new Pair<>("1rk5/8/4n3/5B2/1N6/8/8/1Q1K4 b - - 0 1", "Rb7, Rb6, Rb5, Rxb4, Ra8, Kd8, Kd7, Kc7, Kb7"),
                new Pair<>("1rk5/8/4q3/8/1N6/7B/8/1Q1K4 b - - 0 1", "Qd7, Qf5, Qg4, Qxh3, Rb7, Rb6, Rb5, Rxb4, Ra8, Kd8, Kd7, Kc7, Kb7"),
                new Pair<>("3Q4/1Q4Q1/4Q3/2Q4R/Q4Q2/3Q4/1Q4Rp/1K1BBNNk w - - 0 1", "Kc2, Kc1, Ka1, Ka2, Be2, Bf3, Bg4, Bc2, Bb3, Bf2, Bg3, Bh4, Bd2, Bc3, Bb4, Ba5, Ng3, Nxh2, Nd2, Ne3, Nh3, Ne2, Nf3, Qb3, Qb4, Qb5, Qb6, Qc2, Qd2, Qe2, Qf2, Qa2, Qc3, Qd4, Qe5, Qf6, Qa3, Qa1, Qc1, Rg3, Rg4, Rg5, Rg6, Rxh2, Rf2, Re2, Rd2, Rc2, Qd4, Qd5, Qd6, Qd7, Qd2, Qe3, Qf3, Qg3, Qh3, Qc3, Qb3, Qa3, Qe4, Qf5, Qg6, Qh7, Qc4, Qb5, Qa6, Qc2, Qe2, Qa5, Qa6, Qa7, Qa8, Qa3, Qa2, Qa1, Qb4, Qc4, Qd4, Qe4, Qb5, Qc6, Qd7, Qe8, Qb3, Qc2, Qf5, Qf6, Qf7, Qf8, Qf3, Qf2, Qg4, Qh4, Qe4, Qd4, Qc4, Qb4, Qg5, Qh6, Qe5, Qd6, Qc7, Qb8, Qe3, Qd2, Qc1, Qg3, Qxh2, Qc6, Qc7, Qc8, Qc4, Qc3, Qc2, Qc1, Qd5, Qe5, Qf5, Qg5, Qb5, Qa5, Qd6, Qe7, Qf8, Qb6, Qa7, Qb4, Qa3, Qd4, Qe3, Qf2, Rh6, Rh7, Rh8, Rh4, Rh3, Rxh2, Rg5, Rf5, Re5, Rd5, Qe7, Qe8, Qe5, Qe4, Qe3, Qe2, Qf6, Qg6, Qh6, Qd6, Qc6, Qb6, Qa6, Qf7, Qg8, Qd7, Qc8, Qd5, Qc4, Qb3, Qa2, Qf5, Qg4, Qh3, Qb8, Qb6, Qb5, Qb4, Qb3, Qc7, Qd7, Qe7, Qf7, Qa7, Qc8, Qa8, Qa6, Qc6, Qd5, Qe4, Qf3, Qg8, Qg6, Qg5, Qg4, Qg3, Qh7, Qf7, Qe7, Qd7, Qc7, Qh8, Qf8, Qf6, Qe5, Qd4, Qc3, Qh6, Qd7, Qd6, Qd5, Qd4, Qe8, Qf8, Qg8, Qh8, Qc8, Qb8, Qa8, Qc7, Qb6, Qa5, Qe7, Qf6, Qg5, Qh4"),
                new Pair<>("R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNN1KB1 w - - 0 1", "Bc2, Bd3, Be4, Bf5, Bxa2, Nd3, Ne2, Nxa2, Nb3, Ne3, Nf2, Nxb2, Nc3, Kg2, Ke1, Ke2, Kf2, Bh2, Bf2, Be3, Bd4, Bc5, Qd3, Qd4, Qd5, Qd6, Qe2, Qf2, Qg2, Qh2, Qc2, Qxb2, Qe3, Qf4, Qg5, Qh6, Qc3, Qb4, Qa5, Qe1, Qa4, Qa5, Qa6, Qa7, Qxa2, Qb3, Qc3, Qd3, Qe3, Qb4, Qc5, Qd6, Qe7, Qf8, Qxb2, Qf4, Qf5, Qf6, Qf7, Qf8, Qf2, Qg3, Qh3, Qe3, Qd3, Qc3, Qb3, Qg4, Qh5, Qe4, Qd5, Qc6, Qb7, Qe2, Qg2, Qh1, Qc5, Qc6, Qc7, Qc8, Qc3, Qc2, Qd4, Qe4, Qf4, Qg4, Qb4, Qa4, Qd5, Qe6, Qf7, Qg8, Qb5, Qa6, Qb3, Qxa2, Qd3, Qe2, Qh5, Qh6, Qh7, Qh3, Qh2, Qh1, Qg4, Qf4, Qe4, Qd4, Qg5, Qf6, Qe7, Qd8, Qg3, Qf2, Qe1, Qe6, Qe7, Qe8, Qe4, Qe3, Qe2, Qe1, Qf5, Qg5, Qh5, Qd5, Qc5, Qb5, Qa5, Qf6, Qg7, Qd6, Qc7, Qb8, Qd4, Qc3, Qxb2, Qf4, Qg3, Qh2, Qb7, Qb8, Qb5, Qb4, Qb3, Qxb2, Qc6, Qd6, Qe6, Qf6, Qa6, Qc7, Qd8, Qa7, Qa5, Qc5, Qd4, Qe3, Qf2, Qg7, Qg8, Qg5, Qg4, Qg3, Qg2, Qh6, Qf6, Qe6, Qd6, Qc6, Qh7, Qf7, Qe8, Qf5, Qe4, Qd3, Qc2, Qh5, Qd8, Qd6, Qd5, Qd4, Qd3, Qe7, Qf7, Qg7, Qh7, Qc7, Qb7, Qa7, Qe8, Qc8, Qc6, Qb5, Qa4, Qe6, Qf5, Qg4, Qh3, Ra7, Ra6, Ra5, Ra4, Rb8, Rc8, Rd8, Re8, Rf8, Rg8, Rh7, Rh6, Rh5, Rg8, Rf8, Re8, Rd8, Rc8, Rb8"),
                new Pair<>("8/8/8/8/k2Pp2Q/8/8/3K4 b - d3 0 1", "Kb5, Kb4, Kb3, Ka3, Ka5, e3"),
                new Pair<>("8/8/8/7Q/k2Pp3/8/8/3K4 b - d3 0 1", "Kb4, Kb3, Ka3, e3, d3")
        );

        int index = 0;
        for (var FENAndMoves : FENs) {
            Board board = Board.fromFEN(FENAndMoves.getFirst());
            System.out.println(index + ": " + board + ", \n" +  board.getAllPossibleMoves() + ", \n" + FENAndMoves.getSecond() + "\n" + board.getLastMove() + "\n");
//            System.out.println(index + ": " + board + ", \n" + board.getAllPossibleMoves().size() + ", \n" + board.getAllPossibleMoves() + ", \n" + FENAndMoves.getSecond() + "\n\n");
            assertTrue(
                    movesEqual(
                            board.getAllPossibleMoves(),
                            FENAndMoves.getSecond()
                    )
            );
            index++;
        }
    }

    @Test
    public void testCastling () {
        Board boardWhite = Board.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");
        Board boardBlack = Board.fromFEN("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        assertTrue("king side castling should be possible",
                boardWhite.isMoveLegal(Move.parseMove("O-O", PieceColor.WHITE, boardWhite))
                        && boardBlack.isMoveLegal(Move.parseMove("O-O", PieceColor.BLACK, boardBlack)));
        assertTrue("queen side castling should be possible",
                boardWhite.isMoveLegal(Move.parseMove("O-O-O", PieceColor.WHITE, boardWhite))
                        && boardBlack.isMoveLegal(Move.parseMove("O-O-O", PieceColor.BLACK, boardBlack)));

        boardWhite = Board.fromFEN("rnbqkbn1/pppppppp/8/3r4/8/8/PPP1PPPP/R3K2R w KQkq - 0 1");
        boardBlack = Board.fromFEN("r3k2r/ppp1pppp/8/8/3R4/8/PPPPPPPP/1NBQKBNR b KqKq - 0 1");
        assertFalse("castling should not be possible if the king has to move over a checked square",
                boardWhite.isMoveLegal(Move.parseMove("O-O-O", PieceColor.WHITE, boardWhite))
                        || boardBlack.isMoveLegal(Move.parseMove("O-O-O", PieceColor.BLACK, boardBlack)));

        boardWhite = Board.fromFEN("rnbqkbn1/pppppppp/8/2r5/8/8/PP2PPPP/R3K2R w KQkq - 0 1");
        boardBlack = Board.fromFEN("r3k2r/pp2pppp/8/8/2R5/8/PPPPPPPP/1NBQKBNR b KQkq - 0 1");
        assertFalse("castling should not be possible if the king has to move into a checked square",
                boardWhite.isMoveLegal(Move.parseMove("O-O-O", PieceColor.WHITE, boardWhite))
                        || boardBlack.isMoveLegal(Move.parseMove("O-O-O", PieceColor.BLACK, boardBlack)));

        boardWhite = Board.fromFEN("rnbqkbn1/pppppppp/8/1r3B2/8/8/P3PPPP/R3K2R w KQkq - 0 1");
        boardBlack = Board.fromFEN("r3k2r/p3pppp/8/8/1R6/8/PPPPPPPP/1NBQKBNR, b KQkq - 0 1");
        assertTrue("castling should be possible, even if the rook moves over threatened square",
                boardWhite.isMoveLegal(Move.parseMove("O-O-O", PieceColor.WHITE, boardWhite))
                        && boardBlack.isMoveLegal(Move.parseMove("O-O-O", PieceColor.BLACK, boardBlack)));
    }

    private boolean movesEqual(List<Move> moves, String movesRepresentation) {
        Set<String> collect = moves.stream().map(Move::toString).collect(Collectors.toSet());
        Set<String> o = readMoveSequence(movesRepresentation);
        boolean result = collect.equals(o);

        if (!result) {
            System.out.println("Problem");

            Set<String> temp1 = new HashSet<>(Set.copyOf(collect));
            Set<String> temp2 = new HashSet<>(Set.copyOf(o));

            temp2.removeAll(temp1);

            System.out.println(temp2);
        }
        return result;
    }

    private Set<String> readMoveSequence (String moveSequence) {
        return Arrays.stream(moveSequence.strip().split(", ")).collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        Board board = Board.fromFEN("rnb1kbnr/pppppppp/8/1q6/3PP3/8/PPP2PPP/RNBQKBNR b - - 0 1");
        board.makeMove(Move.parseMove("b5b4", PieceColor.BLACK, board));
        System.out.println(board);
    }
}