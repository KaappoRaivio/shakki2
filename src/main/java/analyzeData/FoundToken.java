package analyzeData;

import java.util.List;
import java.util.Objects;

public class FoundToken {
    private Token tokenType;
    private String content;

    public FoundToken (Token tokenType, String content) {
        this.tokenType = tokenType;
        this.content = content;
    }

    public Token getTokenType() {
        return tokenType;
    }

    @Override
    public boolean equals (Object o) {
        if (o == null) return false;

        if (getClass() != o.getClass()) return false;
        if (this == o) return true;

        FoundToken that = (FoundToken) o;
        return tokenType == that.tokenType;
    }

    public boolean is (Object o) {
        if (o == null) return false;
        else if (o.getClass() == Token.class) return tokenType.equals(o);
        else return equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType);
    }

    @Override
    public String toString() {
        return tokenType + "{ " + content + " }";
    }

    public boolean isIn (List<Token> tokenList) {
        return tokenList.stream().anyMatch(this::is);
    }

    public String getContent() {
        return content;
    }
}

