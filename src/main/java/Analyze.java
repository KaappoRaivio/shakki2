import misc.ReadWriter;
import misc.TermColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Analyze {
    public static void main(String[] args) {
        List<String> paths = List.of(
                "/home/kaappo/git/shakki2/src/main/java/material_vs_candinate.txt",
                "/home/kaappo/git/shakki2/src/main/java/control vs candinate",
                "/home/kaappo/git/shakki2/src/main/java/control vs candinate (with openings).txt",
                "/home/kaappo/git/shakki2/src/main/java/control vs candinate round 2.txt"
        );
        paths.forEach(Analyze::analyze);
    }

    public static void analyze (String path) {
        String content = ReadWriter.readFile(path);
        Map<String, Integer> winners = new HashMap<>();
        Map<String, Integer> colors = new HashMap<>();

        for (String line : content.strip().split("\n")) {
            String winner = line.split(";")[0];
            String color = line.split(";")[1];
            winners.merge(winner, 1, Integer::sum);
            colors.merge(color, 1, Integer::sum);
        }



        String filename = path.split("/")[path.split("/").length - 1];
        System.out.println("Analysis for file " + TermColor.ANSI_BOLD + filename + TermColor.ANSI_RESET);

        System.out.println(subtitleFormat("Games played: "));
        System.out.println(content.strip().split("\n").length);

        System.out.println(subtitleFormat("Top winners:"));
        printPretty(getPercentages(winners));
        System.out.println(subtitleFormat("Most wins by color:"));
        printPretty(getPercentages(colors));
        System.out.println();
    }

    private static Map<String, Double> getPercentages(Map<String, Integer> winners) {
        double sum = winners.values().stream().reduce(Integer::sum).orElseThrow();
        return winners.keySet().stream().map(key -> Map.entry(key, winners.get(key) / sum)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String subtitleFormat (String string) {
        return TermColor.ANSI_BOLD.toString() + TermColor.ANSI_RED + string + TermColor.ANSI_RESET;
    }

    private static void printPretty (Map<?, Double> map) {
        int maxLength = map.keySet().stream().map(Object::toString).mapToInt(String::length).max().orElseThrow();
        for (String key : map.keySet().stream().map(Object::toString).map(key -> Map.entry(key, map.get(key))).sorted((a, b) -> -Double.compare(a.getValue(), b.getValue())).map(Map.Entry::getKey).collect(Collectors.toList())) {
            System.out.printf("%s%" + maxLength + "s %s: %s%.1f %% %s \n", TermColor.ANSI_ITALIC.getEscape(), key, TermColor.ANSI_RESET.getEscape(), TermColor.ANSI_BLUE, map.get(key) * 100, TermColor.ANSI_RESET);
        }
    }
}
