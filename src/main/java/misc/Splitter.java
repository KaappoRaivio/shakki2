package misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Splitter {
    public static <T> List<List<T>> splitListInto(List<T> moves, int amountOfChunks) {
        int surfaceArea = moves.size();


        int[] lengths = new int[amountOfChunks];
        int remainder = surfaceArea % amountOfChunks;

        for (int i = 0; i < amountOfChunks; i++) {
            lengths[i] = surfaceArea / amountOfChunks;
        }

        for (int i = 0; i < remainder; i++) {
            lengths[i] += 1;
        }

        List<List<T>> result = new ArrayList<>();


        for (int chunk = 0; chunk < amountOfChunks; chunk++) {
            result.add(moves.subList(lengths[chunk] * chunk, lengths[chunk] * chunk + lengths[chunk]));
        }


        return result;
    }

//    public static <T> T choice(Set<? extends T> collection) {
//        T item = null;
//        for (T t : collection) {
//            item = t;
//            break;
//        }
//        collection.remove(item);
//        return item;
//    }
}
