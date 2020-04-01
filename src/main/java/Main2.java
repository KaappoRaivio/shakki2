import chess.misc.Position;

import java.util.Random;

public class Main2 {
    public static void main(String[] args) {


        Random random = new Random();
        Position a = new Position(0, 0);

        while (true) {
            a = a.offset(1, 1, false);
        }
    }
}
