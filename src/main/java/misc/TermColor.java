package misc;

import java.util.Objects;

public enum TermColor {
    ANSI_RESET("\u001B[0m"),
    ANSI_BOLD("\u001B[1m"),
    ANSI_FAINT("\u001B[2m"),
    ANSI_ITALIC("\u001B[3m"),
    ANSI_BLACK("\u001B[30m", "\u001B[40m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_LIGHT_YELLOW("\u001B[93m", "\u001B[103m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[97m", "\u001B[107m"),
    ANSI_HOME ("\u001B[Hm"),
    ANSI_LIGHT_GRAY ("\u001B[37m", "\u001B[47m"),
    ANSI_DARK_GRAY ("\u001B[90m", "\u001B[100m");


    public String getEscape() {
        return getEscape(false);
    }

    public String getEscape(boolean background) {
        return Objects.requireNonNull(background ? backgroundEscape : escape);
    }

    private final String escape;
    private String backgroundEscape;

    TermColor(String escape) {
        this(escape, null);
    }

    TermColor(String escape, String backgroundEscape) {
        this.escape = escape;
        this.backgroundEscape = backgroundEscape;
    }
}
