package pl.sii;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PopularWords {

    public static void main(String[] args) {
        PopularWords popularWords = new PopularWords();
        Map<String, Long> result = popularWords.findOneThousandMostPopularWords();
        result.entrySet().forEach(System.out::println);
    }

    public Map<String, Long> findOneThousandMostPopularWords() {

        final String filePath = new File("src/main/resources/3esl.txt").getAbsolutePath();
        System.out.println(filePath);
        List<String> list = new ArrayList<>();

        try {
            list = Files.lines(Paths.get(filePath))
                    .filter(line -> line.length() > 1)
                    .filter(line -> {
                        boolean isNotShorcut = true;
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == '.')
                                isNotShorcut = false;
                        }
                        return isNotShorcut;
                    })
                    .map(line -> line.toLowerCase())
                    .map(line -> line.replace(' ', '-'))
                    .map(line -> line.split("-"))
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error loading data from the file!");
            e.printStackTrace();
        }

        Map<String, Long> hashMap = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Map.Entry<String, Long>> entryList = hashMap.entrySet()
                .stream()
                .filter(stringLongEntry -> stringLongEntry.getKey().length() >= 2)
                .filter(stringLongEntry -> {
                    String tmp = stringLongEntry.getKey();
                    boolean isLetter = true;
                    for (int i = 0; i < tmp.length(); i++) {
                        if (!Character.isLetter(tmp.charAt(i)))
                            isLetter = false;
                    }
                    return isLetter;
                })
                .sorted(new Comparator<Map.Entry<String, Long>>() {
                    @Override
                    public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                        int result = o2.getValue().compareTo(o1.getValue());
                        if (result == 0)
                            result = o2.getKey().compareTo(o1.getKey());
                        return result;
                    }
                })
                .limit(1000)
                .collect(Collectors.toList());

        Map<String, Long> linkedHashMap = new LinkedHashMap<>();

        for (Map.Entry<String, Long> entry : entryList) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }

        return linkedHashMap;
    }
}
