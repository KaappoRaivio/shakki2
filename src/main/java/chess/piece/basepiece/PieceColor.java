package chess.piece.basepiece;

import java.io.Serializable;

public enum PieceColor implements Serializable {
    BLACK (0), WHITE (1), NO_COLOR (-1);

    private static int count = 0;



    private int ordinal;

    PieceColor(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public PieceColor invert () {
        switch (this) {
            case BLACK:
                return WHITE;
            case WHITE:
                return BLACK;
            default:
//                throw new RuntimeException("Cannot invert NO_COLOR!");
//                System.out.println("Inverting no_color!");
                return NO_COLOR;
        }
    }
}
