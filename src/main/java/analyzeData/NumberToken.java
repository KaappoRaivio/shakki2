package analyzeData;

public class NumberToken extends FoundToken {
    private String value;

    public String getValue() {
        return value;
    }



    @Override
    public String toString() {
        return super.toString() + "{" + value + "}";
    }

    public NumberToken(String value) {
        super(Token.Default.NUMBER, value);

        this.value = value;
    }
}
