package analyzeData;

import com.sun.java.accessibility.util.TopLevelWindowListener;

import java.util.*;
import java.util.stream.Collectors;

public class Tokens {
    public static final Tokens DEFAULT_TOKENS = new Tokens(
            Token.Default.SYMBOL,
            Token.Default.QUOTE,

            Token.Default.LEFT_SQUARE_BRACKET,
            Token.Default.RIGHT_SQUARE_BRACKET,

            Token.Default.NUMBER,
            Token.Default.PLUS
    );

    private List<Token> tokens;
    private Map<String, Token> tokenMap = new HashMap<>();

    public Tokens (Token... tokens) {
        this(Arrays.asList(tokens));
    }

    public Tokens (List<Token> tokens) {
        this.tokens = tokens;

        tokens.forEach(item -> tokenMap.put(item.toString().toLowerCase(), item));
    }

    private List<Token> sortedValues () {
        return tokens.stream().sorted((token1, token2) -> token2.getPrecedence() - token1.getPrecedence()).collect(Collectors.toList());
    }

    public Token getToken (CharSequence pattern) {
        return sortedValues().stream()
                .filter(token -> token.matches(pattern))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unrecognized pattern " + (pattern.length() > 100 ? pattern.subSequence(0, 100) : pattern) + "!"));
    }

    public Token getByName (String name) {
        return Optional.ofNullable(tokenMap.get(name.toLowerCase())).orElseThrow(() -> new NullPointerException("Unknown name " + name + "!" + tokenMap));
    }

    public void addToken (Token token) {
        tokens.add(token);
    }
}

