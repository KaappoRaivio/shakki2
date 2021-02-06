package analyzeData;

import java.util.Objects;
import java.util.regex.Pattern;

public class Token {
    private final Pattern regex;
    private final String repr;
    private final int precedence;
    private final TokenType tokenType;
    private Pattern removerRegex;


    public Token(final String regex, final String repr, final int precedence) {
        this(regex, repr, precedence, TokenType.NORMAL);
    }

    public Token (final String regex, final String repr, final int precedence, final TokenType tokenType) {
        this.regex = Pattern.compile("^" + regex);
        this.repr = repr;
        this.precedence = precedence;
        this.tokenType = tokenType;
        this.removerRegex = Pattern.compile(this.regex.pattern() + "(.*?)");
    }

    public boolean matches (CharSequence another) {
        return regex.matcher(another).lookingAt();
    }

    public Pattern getRemoverRegex () {
        return removerRegex;
    }

    @Override
    public String toString () {
        return repr;
    }

    public Pattern getRegex () {
        return regex;
    }

    public int getPrecedence () {
        return precedence;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return precedence == token.precedence &&
                Objects.equals(regex, token.regex) &&
                Objects.equals(repr, token.repr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex, repr, precedence);
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public static class Default {
        public static final Token END = new Token ("$", "End", 1);
        public static final Token NUMBER = new Token("([0123456789])+([.,]([0123456789])+)?(\\.)?", "Number", 4, TokenType.NUMBER);
        public static final Token SYMBOL = new Token("[a-zA-Z\\?\\.\\/\\-]+", "Symbol", -10, TokenType.SYMBOL);
        public static final Token QUOTE = new Token("(\"|\\')", "Quote", 1, TokenType.NORMAL);

        public static final Token PLUS = new Token ("\\+", "Plus", 1);


        public static final Token LEFT_SQUARE_BRACKET = new Token ("\\[", "Left square bracket", 1);
        public static final Token RIGHT_SQUARE_BRACKET = new Token ("\\]", "Right square bracket", 1);
    }

}
