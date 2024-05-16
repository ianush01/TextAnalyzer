package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>");
            return;
        }

        String filePath = null;
        int topCount = 0;
        int phraseSize = 0;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--file":
                        filePath = args[++i];
                        break;
                    case "--top":
                        topCount = Integer.parseInt(args[++i]);
                        break;
                    case "--phraseSize":
                        phraseSize = Integer.parseInt(args[++i]);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>");
            return;
        }

        if (filePath == null || topCount <= 0 || phraseSize < 2) {
            System.out.println("Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>");
            return;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            List<String> words = tokenizeWords(content);
            List<String> sentences = tokenizeSentences(content);

            System.out.println(formatOutput(words.size(), sentences.size(), topCount, phraseSize, words));

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static List<String> tokenizeWords(String content) {
        String[] tokens = content.split("\\W+");
        List<String> words = new ArrayList<>();
        for (String token : tokens) {
            if (!token.isEmpty()) {
                words.add(token.toLowerCase());
            }
        }
        return words;
    }

    public static List<String> tokenizeSentences(String content) {
        String[] tokens = content.split("[.!?]\\s*");
        return Arrays.asList(tokens);
    }

    public static Map<String, Integer> countFrequency(List<String> items) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String item : items) {
            frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
        }
        return frequencyMap;
    }

    public static List<Map.Entry<String, Integer>> getMostCommon(Map<String, Integer> frequencyMap, int n) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return entryList.subList(0, Math.min(n, entryList.size()));
    }

    public static List<String> generatePhrases(List<String> words, int phraseLength) {
        List<String> phrases = new ArrayList<>();
        for (int i = 0; i <= words.size() - phraseLength; i++) {
            StringBuilder phrase = new StringBuilder();
            for (int j = 0; j < phraseLength; j++) {
                if (j > 0) phrase.append(" ");
                phrase.append(words.get(i + j));
            }
            phrases.add(phrase.toString());
        }
        return phrases;
    }

    public static String formatOutput(int wordCount, int sentenceCount, int topCount, int phraseSize, List<String> words) {
        List<String> phrases = generatePhrases(words, phraseSize);
        Map<String, Integer> phraseFrequency = countFrequency(phrases);
        List<Map.Entry<String, Integer>> mostCommonPhrases = getMostCommon(phraseFrequency, topCount);

        int maxPhraseLength = mostCommonPhrases.stream()
                .mapToInt(entry -> entry.getKey().length())
                .max()
                .orElse(0);

        StringBuilder output = new StringBuilder();
        output.append("+---------------------+-----+\n");
        output.append(String.format("| Number of words:    | %d |\n", wordCount));
        output.append("+---------------------+-----+\n");
        output.append(String.format("| Number of sentences:| %d |\n", sentenceCount));
        output.append("+---------------------+-----+\n\n");
        output.append("+").append("-".repeat(maxPhraseLength + 4)).append("+-------+\n");
        output.append(String.format("| %-" + (maxPhraseLength + 2) + "s | %5s |\n", "Phrases", "Count"));
        output.append("+").append("-".repeat(maxPhraseLength + 4)).append("+-------+\n");

        for (Map.Entry<String, Integer> entry : mostCommonPhrases) {
            output.append(String.format("| %-" + (maxPhraseLength + 2) + "s | %5d |\n", entry.getKey(), entry.getValue()));
        }

        output.append("+").append("-".repeat(maxPhraseLength + 4)).append("+-------+\n");
        return output.toString();
    }
}