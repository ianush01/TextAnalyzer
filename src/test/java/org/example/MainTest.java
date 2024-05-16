package org.example;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testMainWithInsufficientArguments() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{});
        assertTrue(outContent.toString().contains("Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>"));
        System.setOut(System.out);
    }

    @Test
    public void testMainWithInvalidArgumentsException() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{"--file", "dummy.txt", "--top", "ten", "--phraseSize", "2"});
        String expectedOutput = "Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    @Test
    public void testMainWithInvalidArguments() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{"--file", "dummy.txt", "--top", "10"});
        assertFalse(outContent.toString().contains("Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>"));
        System.setOut(System.out);
    }

    @Test
    public void testMainWithNullFilePath() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{"--top", "10", "--phraseSize", "2"});
        String expectedOutput = "Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>";
        assertFalse(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    @Test
    public void testMainWithInvalidTopCount() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{"--file", "dummy.txt", "--top", "0", "--phraseSize", "2"});
        String expectedOutput = "Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    @Test
    public void testMainWithNonExistentFile() {

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        Main.main(new String[]{"--file", "non_existent_file.txt", "--top", "10", "--phraseSize", "2"});
        assertTrue(errContent.toString().contains("Error reading file"));
        System.setErr(System.err);
    }

    @Test
    public void testMainWithInvalidPhraseSize() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{"--file", "dummy.txt", "--top", "10", "--phraseSize", "1"});
        String expectedOutput = "Invalid arguments. Usage: java -jar TextAnalyzer.jar --file <file_path> --top <top_count> --phraseSize <phrase_size>";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }


    @Test
    public void testMainWithValidArguments() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, List.of("This is a test file. It contains some text for testing."));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(new String[]{"--file", tempFile.toString(), "--top", "5", "--phraseSize", "2"});

        assertTrue(outContent.toString().contains("Number of words"));
        assertTrue(outContent.toString().contains("Number of sentences"));

        System.setOut(System.out);
        Files.delete(tempFile);
    }
    
    @Test
    public void testTokenizeWords() {
        String content = "Hello, world! Hello, everyone.";
        List<String> words = Main.tokenizeWords(content);
        assertEquals(Arrays.asList("hello", "world", "hello", "everyone"), words);
    }

    @Test
    public void testTokenizeSentences() {
        String content = "Hello, world! Hello, everyone. How are you?";
        List<String> sentences = Main.tokenizeSentences(content);
        assertEquals(Arrays.asList("Hello, world", "Hello, everyone", "How are you"), sentences);
    }

    @Test
    public void testCountFrequency() {
        List<String> words = Arrays.asList("hello", "world", "hello", "everyone");
        Map<String, Integer> frequency = Main.countFrequency(words);
        assertEquals(2, (int) frequency.get("hello"));
        assertEquals(1, (int) frequency.get("world"));
        assertEquals(1, (int) frequency.get("everyone"));
    }

    @Test
    public void testGetMostCommon() {
        Map<String, Integer> frequencyMap = new HashMap<>();
        frequencyMap.put("apple", 2);
        frequencyMap.put("banana", 3);
        frequencyMap.put("orange", 1);
        List<Map.Entry<String, Integer>> expected = Arrays.asList(
                new AbstractMap.SimpleEntry<>("banana", 3),
                new AbstractMap.SimpleEntry<>("apple", 2)
        );
        assertEquals(expected, Main.getMostCommon(frequencyMap, 2));
    }

    @Test
    public void testGeneratePhrases() {
        List<String> words = Arrays.asList("hello", "world", "hello", "everyone");
        List<String> phrases = Main.generatePhrases(words, 2);
        assertEquals(Arrays.asList("hello world", "world hello", "hello everyone"), phrases);
    }

    @Test
    public void testFormatOutput() {
        List<String> words = Arrays.asList("this", "is", "a", "test", "this", "is", "only", "a", "test");
        String expectedOutput =
                "+---------------------+-----+\n" +
                        "| Number of words:    | 9 |\n" +
                        "+---------------------+-----+\n" +
                        "| Number of sentences:| 1 |\n" +
                        "+---------------------+-----+\n\n" +
                        "+-----------+-------+\n" +
                        "| Phrases   | Count |\n" +
                        "+-----------+-------+\n" +
                        "| this is   |     2 |\n" +
                        "| a test    |     2 |\n" +
                        "| is only   |     1 |\n" +
                        "| only a    |     1 |\n" +
                        "| is a      |     1 |\n" +
                        "+-----------+-------+\n";

        List<String> sentences = List.of("this is a test this is only a test");
        String actualOutput = Main.formatOutput(words.size(), sentences.size(), 5, 2, words);
        assertEquals(expectedOutput, actualOutput);
    }
}