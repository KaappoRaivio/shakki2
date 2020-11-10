package players.treeai;

import chess.board.Board;
import chess.piece.basepiece.PieceColor;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameStageTest {
//    @Test
//    public void testGameStage() {
//        Board board = Board.fromFEN("6r1/K5pp/1p4Q1/1k2n3/1P2p2q/1P1PP1p1/8/3R4 w - - 0 1");
//        GameStage gameStage = GameStage.getGameStage(board);
//        assertEquals(0, gameStage.getOpeningWeight(), 0.000000001);
//        assertEquals(1, gameStage.getMiddlegameWeight(), 0.000000001);
//        assertEquals(0, gameStage.getEndgameWeight(), 0.000000001);
//
//        Board board1 = Board.fromFEN("4Nq2/2K5/2P3P1/8/3k4/8/6B1/8 w - - 0 1");
//        gameStage = GameStage.getGameStage(board1);
//        assertEquals(0, gameStage.getOpeningWeight(), 0.000000001);
//        assertEquals(0, gameStage.getMiddlegameWeight(), 0.000000001);
//        assertEquals(1, gameStage.getEndgameWeight(), 0.000000001);
//
//        Board board2 = Board.fromFEN("3r4/P6p/8/3k4/5Q1P/4P3/3K4/6b1 w - - 0 1");
//        gameStage = GameStage.getGameStage(board2);
//        assertEquals(0, gameStage.getOpeningWeight(), 0.000000001);
//        assertEquals(0.10833, gameStage.getMiddlegameWeight(), 0.0001);
//        assertEquals(0.89166, gameStage.getEndgameWeight(), 0.0001);
////        evaluateBoard(board2, PieceColor.WHITE);
//    }
}