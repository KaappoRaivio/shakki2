package misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Splitter {
    public static <T> List<Set<T>> splitListInto(Set<T> moves, int amountOfChunks) {
        int surfaceArea = moves.size();


        int[] lengths = new int[amountOfChunks];
        int remainder = surfaceArea % amountOfChunks;

        for (int i = 0; i < amountOfChunks; i++) {
            lengths[i] = surfaceArea / amountOfChunks;
        }

        for (int i = 0; i < remainder; i++) {
            lengths[i] += 1;
        }

        List<Set<T>> result = new ArrayList<>();



        for (int length : lengths) {
            Set<T> temp = new HashSet<>();
            for (int i = 0; i < length; i++) {
                temp.add(choice(moves));
            }

            result.add(temp);
        }

        return result;
    }

    private static <T> T choice (Set<? extends T> collection) {
        T item = null;
        for (T t : collection) {
            item = t;
            break;
        }
        collection.remove(item);
        return item;
    }
}
