package analyzeData;

import misc.ReadWriter;

import java.util.ArrayList;
import java.util.List;

import static analyzeData.Lexer.PGN_TOKENS;

public class PGNParser {
    private Lexer lexer;


    public PGNParser(String input) {
        System.out.println("Lexing");
        lexer = new Lexer(input, PGN_TOKENS);
        System.out.println("Lexed");
    }

    public List<PGNGame> parse () {
        return event();
    }

    private List<PGNGame> event() {
        String id;

        List<PGNGame> games = new ArrayList<>();

        while (true) {
            FoundToken a = lexer.getNextToken();
            if (a.getTokenType() == Token.Default.END) {
                break;
            }
            if (a.getTokenType() == Lexer.EVENT) {
                FoundToken quote = lexer.getNextToken();
                id = lexer.getNextToken().getContent();
                List<String> moves = game();

                PGNGame pgnGame = new PGNGame(moves.subList(0, moves.size() - 1), moves.get(moves.size() - 1), id);
                games.add(pgnGame);
            }
        }

        return games;
    }

    private List<String> game () {
        while (!lexer.getNextToken().getContent().equals("1."));
        lexer.revert();

        List<String> moves = new ArrayList<>();

        while (true) {
            FoundToken supposedMoveNumber = lexer.getNextToken();
            if (supposedMoveNumber.getTokenType().getTokenType() == TokenType.RESULT) {
                lexer.revert();
                break;
            }

            int moveNumber = Integer.parseInt(supposedMoveNumber.getContent().replace(".", ""));

            FoundToken supposedWhiteMove = lexer.getNextToken();
            if (supposedWhiteMove.getTokenType().getTokenType() == TokenType.CHESS_MOVE) {
                moves.add(supposedWhiteMove.getContent());
            } else {
                lexer.revert();
                break;
            }

            FoundToken supposedBlackMove = lexer.getNextToken();
            if (supposedBlackMove.getTokenType().getTokenType() == TokenType.CHESS_MOVE) {
                moves.add(supposedBlackMove.getContent());
            } else {
                lexer.revert();
                break;
            }
        }

        FoundToken supposedResult = lexer.getNextToken();
        if (supposedResult.getTokenType().getTokenType() == TokenType.RESULT) {
            moves.add(supposedResult.getContent());
        } else {
            throw new RuntimeException("No result found!");
        }

//        System.out.println(moves);
        return moves;
    }


    public static void main(String[] args) {



//        PGNParser parser = new PGNParser(ReadWriter.readFile("/home/kaappo/git/shakki2/src/main/resources/games/data.pgn"));
        PGNParser parser = new PGNParser("[Event \"1\"]\n" +
                "[Site \"kaggle.com\"]\n" +
                "[Date \"??\"]\n" +
                "[Round \"??\"]\n" +
                "[White \"??\"]\n" +
                "[Black \"??\"]\n" +
                "[Result \"1/2-1/2\"]\n" +
                "[WhiteElo \"2354\"]\n" +
                "[BlackElo \"2411\"]\n" +
                "\n" +
                "1. Nf3 Nf6 2. c4 c5 3. b3 g6 4. Bb2 Bg7 5. e3 O-O 6. Be2 b6 7. O-O Bb7 8.\n" +
                "Nc3 Nc6 9. Qc2 Rc8 10. Rac1 d5 11. Nxd5 Nxd5 12. Bxg7 Nf4 13. exf4 Kxg7 14.\n" +
                "Qc3+ Kg8 15. Rcd1 Qd6 16. d4 cxd4 17. Nxd4 Qxf4 18. Bf3 Qf6 19. Nb5 Qxc3\n" +
                "1/2-1/2\n" +
                "\n" +
                "[Event \"2\"]\n" +
                "[Site \"kaggle.com\"]\n" +
                "[Date \"??\"]\n" +
                "[Round \"??\"]\n" +
                "[White \"??\"]\n" +
                "[Black \"??\"]\n" +
                "[Result \"1/2-1/2\"]\n" +
                "[WhiteElo \"2523\"]\n" +
                "[BlackElo \"2460\"]\n" +
                "\n" +
                "1. e4 e5 2. Nf3 Nf6 3. d4 Nxe4 4. Nxe5 d6 5. Nf3 d5 6. Bd3 Nd6 7. O-O\n" +
                "1/2-1/2\n" +
                "\n" +
                "[Event \"3\"]\n" +
                "[Site \"kaggle.com\"]\n" +
                "[Date \"??\"]\n" +
                "[Round \"??\"]\n" +
                "[White \"??\"]\n" +
                "[Black \"??\"]\n" +
                "[Result \"0-1\"]\n" +
                "[WhiteElo \"1915\"]\n" +
                "[BlackElo \"1999\"]\n" +
                "\n" +
                "1. e4 d5 2. exd5 Nf6 3. d4 Nxd5 4. Nf3 g6 5. Be2 Bg7 6. c4 Nb6 7. Nc3 O-O\n" +
                "8. O-O Nc6 9. Be3 Bg4 10. d5 Bxf3 11. Bxf3 Ne5 12. Bxb6 Nxf3+ 13. Qxf3 axb6\n" +
                "14. a3 Qd7 15. Rad1 Qf5 16. Qxf5 gxf5 17. Rd3 Rfd8 18. Rfd1 Kf8 19. R1d2\n" +
                "Rd7 20. f4 Rad8 21. Kf2 e6 22. b3 Bxc3 23. Rxc3 c6 24. Ke3 exd5 25. cxd5\n" +
                "Rxd5 26. Rxd5 Rxd5 27. Kf3 Ke7 28. h3 h5 29. Kg3 Rd4 30. Kf3 h4 31. Ke3 Rd6\n" +
                "32. Kf3 Rg6 33. Kf2 Kd6 34. Rd3+ Kc5 35. Rd7 Rg3 36. b4+ Kb5 37. a4+ Kxa4\n" +
                "38. Rxb7 b5 39. Rc7 Rc3 40. Rxf7 Kxb4 41. Rxf5 c5 42. Re5 Ra3 43. f5 Ra7\n" +
                "44. Ke3 c4 45. f6 c3 46. Kd3 Rd7+ 47. Kc2 Rd2+ 48. Kc1 Rf2 49. Re4+ Kb3 50.\n" +
                "Re1 Rxf6 51. Kd1 b4 52. Re4 c2+ 53. Kd2 Rd6+ 0-1");

//        StockfishParser parser2 = new StockfishParser(ReadWriter.readFile("/home/kaappo/git/shakki2/src/main/resources/games/stockfish.csv"));
        StockfishParser parser2 = new StockfishParser("1,18 17 12 8 -5 12 3 -2 22 21 20 13 8 21 11 3 -6 5 1 -10 -21 -1 -26 18 48 48 53 73 46 68 51 60 54 46 70 62 35 54\n" +
                "2,26 44 26 18 14 34 36 31 37 35 42 52 55\n" +
                "3,26 51 68 57 65 77 48 93 61 63 63 58 53 46 69 29 30 27 -2 12 -11 0 -17 24 5 15 16 31 44 25 18 20 27 26 18 14 18 20 10 2 7 22 -28 -26 -30 -34 -29 -28 -49 -55 -51 -50 -82 -74 -83 -77 -92 -72 -105 -99 -106 -93 -129 -79 -87 -63 -66 -23 -34 -47 -37 -49 -76 -110 -90 -116 -193 -103 -144 -106 -108 -69 -80 -93 -96 -86 -105 -111 -138 25 -342 -335 -507 -534 -523 -494 -2427 -3977 -5009 -5103 -6282 -1273 -10851 -10958 -10866 -11544");

        List<PGNGame> games = parser.parse();
//        System.out.println(games);
        List<List<Integer>> scores = parser2.parseScores();


        for (int i = 0; i < games.size(); i++) {
            games.get(i).attachScores(scores.get(i));
        }

        System.out.println(games);
//        System.out.println(parser.parse().size());
    }
}
