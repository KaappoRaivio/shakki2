package chess.misc;

import chess.misc.exceptions.ChessException;
import misc.Pair;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public class Position implements Serializable {
    private final int x;
    private final int y;

    private static final Pattern stringPattern = Pattern.compile("^[a-hA-H]\\d$");

    public Position (int x, int y) {
        this(x, y, true);
    }

    public Position (int x, int y, boolean check) {
        this.x = check ? limit(0, x, 8, true) : x;
        this.y = check ? limit(0, y, 8, true) : y;
    }

    public static Position fromString (String position) {
        if (!stringPattern.matcher(position).matches()) {
            throw new ChessException("String " + position + " is not a valid notation for a board position!");
        }

        int x = position.toUpperCase().charAt(0) - 65; //  65 for converting from ASCII to int
        int y = Integer.parseInt(String.valueOf(position.charAt(1))) - 1;

        return new Position(x, y);
    }

    public Position offset (int x, int y) {
        return offset(x, y, true);
    }

    public Position offset (int x, int y, boolean check) {
        return offsetX(x, check).offsetY(y, check);
    }

    public Position offset (Position position) {
        return offset(position, true);
    }

    public Position offset (Position position, boolean check) {
        return offset(position.getX(), position.getY(), check);
    }

    public Position offsetX (int x) {
        return offsetX(x, true);
    }

    public Position offsetX (int x, boolean check) {
        return new Position(getX() + x, getY(), check);
    }

    public Position offsetY (int y) {
        return offsetY(y, true);
    }

    public Position offsetY (int y, boolean check) {
        return new Position(getX(), getY() + y, check);
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
    }

    private static int limit (int lower, int value, int higher, boolean thr) {
        if (value < lower) {
//            System.out.println("Value " + value + " is not within its bounds! (too low) (BAD!!!!)");
//            throw new ChessException("asd");
            if (thr) {
                throw new RuntimeException("asd");
            } else {
                return lower;
            }
        } else if (value >= higher) {
//            System.out.println("Value " + value + " is not within its bounds! (too high) (BAD!!!!)");
            if (thr) {
                throw new RuntimeException("asd");
            } else {
                return  higher - 1;
            }
        } else {
            return value;
        }
    }

    public boolean verify () {
        return limit(0, getX(), 8, false) == getX() && limit(0, getY(), 8, false) == getY();
    }

    @Override
    public String toString () {
        char xString = (char) (getX() + 65);
        return xString + "" + (getY() + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
