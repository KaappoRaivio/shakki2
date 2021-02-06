package analyzeData;

public class SymbolToken extends FoundToken {

    private String value;

    public String getValue() {
        return value;
    }


    @Override
    public boolean equals (Object o) {
        try {
            return super.equals(o) && value.equals(((SymbolToken) o).value);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "{" + value + "}";
    }

    public SymbolToken (String value) {
        super(Token.Default.SYMBOL, value);

        this.value = value;
    }
}

