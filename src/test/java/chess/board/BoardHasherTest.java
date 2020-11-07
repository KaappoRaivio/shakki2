package chess.board;

import chess.piece.CastlingKing;
import chess.piece.CastlingRook;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardHasherTest {
    @Test
    public void testIncrementalHash () {
        Board board1 = Board.getStartingPosition();

        int hash1 = board1.hashCode();
        int rook = board1.getHasher().getPartHash(0, 0, new CastlingRook(PieceColor.WHITE));
        hash1 ^= rook;

        Board board2 = Board.fromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w Kkq - 0 1");
        int hash2 = board2.hashCode();

        assertEquals(hash1, hash2);
    }
}