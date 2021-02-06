package analyzeData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StockfishParser {
    private String input;
    public StockfishParser (String input) {
        this.input = input;
    }

    public List<List<Integer>> parseScores () {
        String[] rows = input.split("\n");

        List<List<Integer>> result = new ArrayList<>();

        for (String row : rows) {
            if (row.startsWith("Event")) continue;
            String[] split = row.split(",");
            int id = Integer.parseInt(split[0]);
            List<Integer> scores = Arrays.stream(split[1].split(" ")).map(s -> {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            }).collect(Collectors.toList());

            result.add(scores);
            if (result.size() != id) {
                throw new AssertionError("Problem");
            }
        }

        return result;
    }
}
