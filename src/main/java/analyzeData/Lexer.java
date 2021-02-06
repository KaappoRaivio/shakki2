package analyzeData;

import misc.ReadWriter;

import java.nio.CharBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;

public class Lexer {
    private Tokens possibleTokens;
    private Deque<FoundToken> tokens = new LinkedList<>();
    private Deque<FoundToken> alreadyRequestedTokens = new LinkedList<>();

    @Override
    public String toString() {
        return tokens.toString() + " at " + alreadyRequestedTokens.size();
    }

    public Lexer (String input, Tokens possibleTokens) {
        this.possibleTokens = possibleTokens;
        tokens = processInput(CharBuffer.wrap(input), new LinkedList<>());
    }

    public static CharBuffer ltrim(CharBuffer s) {
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }

        return s.subSequence(i, s.length());

//        return s;
    }

    private Deque<FoundToken> processInput(CharBuffer input, Deque<FoundToken> alreadyProcessed) {
        while (true) {
            input = ltrim(input);
            if (input.length() == 0) {
                alreadyProcessed.add(new FoundToken(Token.Default.END, ""));
                return alreadyProcessed;
            }


//            input = input.replaceAll("^\\s+", "");
            Token token = possibleTokens.getToken(input);

            Matcher matcher = token.getRegex().matcher(input);
            matcher.lookingAt();
            String extracted = matcher.group();

//            System.out.println(extracted);

            FoundToken latestToken = new FoundToken(token, extracted);
            CharSequence inputBefore = input;
//            input = token
//                    .getRemoverRegex()
//                    .matcher(input)
//                    .replaceFirst("");

            input = input.subSequence(extracted.length(), input.length());
//            input = CharBuffer.wrap(input, extracted.length(), input.length());
//            System.out.println(input.length());

            if (input.length() == inputBefore.length()) {   // Aka nothing was recognized from the input string and it didn't change  // 16.7.2019
                throw new RuntimeException("Unrecognized input: " + input.subSequence(0, 200) + "!");
            }

            alreadyProcessed.add(latestToken);
//            processInput(input, alreadyProcessed, latestToken);
        }





    }


    public FoundToken getNextToken () {
        alreadyRequestedTokens.addFirst(tokens.peekFirst());
        return tokens.pop();
    }

    public void revert () {
        tokens.addFirst(alreadyRequestedTokens.pop());
    }

    public boolean isEmpty() {
        return tokens.size() == 1; // 1 for the END token
    }



    public static void main(String[] args) {
        Lexer lexer = new Lexer("[Event \"49998\"]\n" +
                "[Site \"kaggle.com\"]\n" +
                "[Date \"??\"]\n" +
                "[Round \"??\"]\n" +
                "[White \"??\"]\n" +
                "[Black \"??\"]\n" +
                "[Result \"1-0\"]\n" +
                "\n" +
                "1. e4 c5 2. Nf3 e6 3. d4 cxd4 4. Nxd4 Nc6 5. Nc3 Qc7 6. Be2 a6 7. O-O Nf6\n" +
                "8. Be3 Be7 9. f4 d6 10. Qe1 O-O 11. Qg3 Bd7 12. Rae1 b5 13. a3 Nxd4 14.\n" +
                "Bxd4 Bc6 15. Kh1 Qb7 16. Bd3 b4 17. Nd1 g6 18. Nf2 bxa3 19. bxa3 d5 20.\n" +
                "Bxf6 Bxf6 21. f5 Bg7 22. Ng4 exf5 23. exf5 Rfe8 24. Rb1 Qa7 25. Qh4 Rab8\n" +
                "26. Rbd1 Bb5 27. f6 Bf8 28. Bxg6 fxg6 29. f7+ Kh8 30. fxe8=Q Rxe8 31. Nf6\n" +
                "Rc8 32. Rf4 h6 33. Nxd5 g5 34. Rxf8+ Rxf8 35. Qxh6+ 1-0\n" +
                "\n" +
                "[Event \"49999\"]\n" +
                "[Site \"kaggle.com\"]\n" +
                "[Date \"??\"]\n" +
                "[Round \"??\"]\n" +
                "[White \"??\"]\n" +
                "[Black \"??\"]\n" +
                "[Result \"1-0\"]\n" +
                "\n" +
                "1. d4 d5 2. Nf3 Nf6 3. c4 dxc4 4. e3 e6 5. Bxc4 c5 6. O-O a6 7. Bb3 b5 8.\n" +
                "a4 Bb7 9. axb5 axb5 10. Rxa8 Bxa8 11. dxc5 Qxd1 12. Rxd1 Bxc5 13. Nc3 b4\n" +
                "14. Na4 Ba7 15. Nd4 Nc6 16. Nb5 Bb8 17. Nb6 Bb7 18. Ba4 Ke7 19. Bd2 Rd8 20.\n" +
                "Nd4 Nxd4 21. Bxb4+ Bd6 22. Rxd4 Bxb4 23. Rxb4 Bc6 24. Kf1 Bxa4 25. Nxa4 Rd2\n" +
                "26. Rb7+ Ke8 27. Nc3 Rc2 28. h3 Nd7 29. Ne4 Ke7 30. b4 Kd8 31. Ng5 Kc8 32.\n" +
                "Rb5 Nf6 33. Nxf7 Ne4 34. f3 Ng3+ 35. Kg1 Kd7 36. Rb7+ Kc6 37. Nd8+ Kd5 38.\n" +
                "Rxg7 1-0", PGN_TOKENS);

//        System.out.println(lexer);
        System.out.println(lexer.tokens.size());
    }
    public final static Token EVENT = new Token("Event", "Event", 20);
    public final static Tokens PGN_TOKENS = new Tokens(
            new Token("[a-zA-Z\\?\\.\\/\\-]+", "Symbol", -10, TokenType.SYMBOL),
            new Token("(\"|\\')", "Quote", 1, TokenType.NORMAL),

            new Token ("\\[", "Left square bracket", 1),
            new Token ("\\]", "Right square bracket", 1),

            EVENT,

            new Token("(1\\/2\\-1\\/2|\\d-\\d)", "Result", 10, TokenType.RESULT),
            new Token("[KNBRQa-h]?[a-h]?[1-8]?(x)?[a-h][1-8]" + "(\\#|\\+)?", "Chess normal move", 5, TokenType.CHESS_MOVE),
            new Token("([a-h]x)?[a-h](8|1)\\=[NBRQ](\\#|\\+)?", "Chess promotion move", 20, TokenType.CHESS_MOVE),
            new Token("(O\\-O\\-O|O\\-O)(\\#|\\+)?", "Chess castling move", 4, TokenType.CHESS_MOVE),
            new Token("([0123456789])+([.,]([0123456789])+)?(\\.)?", "Number", -10, TokenType.NUMBER),
            new Token ("\\+", "Plus", 1)
    );

}
