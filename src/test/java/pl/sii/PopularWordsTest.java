package pl.sii;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PopularWordsTest {
    private static final PopularWords testee = new PopularWords();

    @Test
    public void shouldReturnOneThousandMostPopularWords() {
        //given
        Map<String, Long> wordsFrequencyListCreatedByAdamKilgarriff = getWordsFrequencyListCreatedByAdamKilgarriff();

        //when
        Map<String, Long> result = testee.findOneThousandMostPopularWords();

        //then
        assertFalse(result.isEmpty());
        assertEquals(1000, result.size());
        compareWordListsFrequency(wordsFrequencyListCreatedByAdamKilgarriff, result);
    }

    private void compareWordListsFrequency(Map<String, Long> wordsFrequencyListCreatedByAdamKilgarriff, Map<String, Long> result) {
        long totalFrequencyByKilgarriff = wordsFrequencyListCreatedByAdamKilgarriff.values().stream().reduce(0L, Long::sum);
        long totalFrequencyInAResult = result.values().stream().reduce(0L, Long::sum);
        System.out.println("totalFrequencyByKilgarriff = " + totalFrequencyByKilgarriff);
        System.out.println("totalFrequencyInAResult = " + totalFrequencyInAResult);

        result.forEach((key, value) -> {
            if (wordsFrequencyListCreatedByAdamKilgarriff.get(key) != null) {
                BigDecimal valueUsagePercentage = calculatePercentage(value, totalFrequencyInAResult);
                BigDecimal kilgarriffUsagePercentage = calculatePercentage(wordsFrequencyListCreatedByAdamKilgarriff.get(key), totalFrequencyByKilgarriff);
                BigDecimal diff = kilgarriffUsagePercentage.subtract(valueUsagePercentage);
                System.out.println(key + "," + valueUsagePercentage + "%," + kilgarriffUsagePercentage + "%," + (new BigDecimal(0.5).compareTo(diff.abs()) > 0) + " " + diff);
            } else {
                System.out.println("Word " + key + " was not found in all.num file!");
            }
        });
    }

    private BigDecimal calculatePercentage(double obtained, double total) {
        return BigDecimal.valueOf(obtained * 100 / total).setScale(4, RoundingMode.HALF_UP);
    }

    private Map<String, Long> getWordsFrequencyListCreatedByAdamKilgarriff() {

        Map<String, Long> hashMap = new HashMap<>();
        String filePath = new File("src/test/resources/all.num").getAbsolutePath();
        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = null;
            while (((line = br.readLine()) != null)) {
                String edit[] = line.toLowerCase().split(" ");

                if (hashMap.containsKey(edit[1])) {
                    long x = hashMap.remove(edit[1]);
                    hashMap.put(edit[1], x + Long.parseLong(edit[0]));
                } else {
                    hashMap.put(edit[1], Long.parseLong(edit[0]));
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading data from the file!");
            e.printStackTrace();
        }

        Optional<String> fileTitle = null;
        try {
            fileTitle = Files.lines(Paths.get(filePath))
                    .map(line -> line.toLowerCase().split(" ")[1]).findFirst();
        } catch (IOException e) {
            System.out.println(
                    "Error loading data from the file!");
            e.printStackTrace();
        }

        hashMap.remove(fileTitle.get());
        return hashMap;
    }
}
