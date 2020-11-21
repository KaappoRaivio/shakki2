package chess.board;

import misc.Pair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BoardIOTest {

    @Test
    public void testBoardFENConversion() {
        List<String> FENs = List.of(
                "8/6K1/3k4/8/5p2/8/4P3/8 w - - 0 1",
                "1q6/P7/1K4k1/8/5p2/8/8/8 w - - 0 1",
                "1k6/P7/1K6/8/5pq1/8/8/8 w - - 0 1",
                "8/5p1p/p6r/P4K2/1p1kp1RP/3B2P1/Q7/5N1b w - - 0 1",
                "1rk5/8/4n3/5B2/1N6/8/8/1Q1K4 b - - 0 1",
                "1rk5/8/4q3/8/1N6/7B/8/1Q1K4 b - - 0 1",
                "3Q4/1Q4Q1/4Q3/2Q4R/Q4Q2/3Q4/1Q4Rp/1K1BBNNk w - - 0 1",
                "R6R/3Q4/1Q4Q1/4Q3/2Q4Q/Q4Q2/pp1Q4/kBNN1KB1 w - - 0 1",
                "8/8/8/8/k2Pp2Q/8/8/3K4 b - d3 0 1",
                "8/8/8/7Q/k2Pp3/8/8/3K4 b - d3 0 1"
        );


        for (String FEN : FENs) {
            String[] candinate = Board.fromFEN(FEN).toFEN().split(" ");
            assertEquals(FEN.split(" ")[0], candinate[0]);
            assertEquals(FEN.split(" ")[1], candinate[1]);
            assertEquals(FEN.split(" ")[2], candinate[2]);
            assertEquals(FEN.split(" ")[3], candinate[3]);
        }
    }
}