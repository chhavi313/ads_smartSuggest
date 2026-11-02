
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionEngine {

    private Trie trie;
    private Map<String, Integer> frequencies;

    private static final Path DICTIONARY_FILE =
            Path.of("dictionary.txt");

    public SuggestionEngine() {
        trie = new Trie();
        frequencies = new HashMap<>();

        loadDictionary();
    }

    // Load words and frequencies from dictionary.txt
    private void loadDictionary() {

        try {
            if (!Files.exists(DICTIONARY_FILE)) {
                System.out.println("ERROR: dictionary.txt not found!");
                return;
            }

            List<String> lines =
                    Files.readAllLines(DICTIONARY_FILE);

            System.out.println("Dictionary file found!");
            System.out.println("Total lines: " + lines.size());

            for (String line : lines) {

                String[] parts = line.split(",", 2);

                if (parts.length != 2) {
                    continue;
                }

                String word = parts[0].trim().toLowerCase();

                try {
                    int frequency =
                            Integer.parseInt(parts[1].trim());

                    if (!word.isEmpty() && frequency >= 0
                            && !frequencies.containsKey(word)) {

                        trie.insertWord(word);
                        frequencies.put(word, frequency);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid frequency: " + line);
                }
            }

            System.out.println("Total words loaded: "
                    + frequencies.size());

            System.out.println("University exists: "
                    + frequencies.containsKey("university"));

        } catch (IOException e) {
            System.out.println("Error loading dictionary: "
                    + e.getMessage());
        }
    }

    // Save dictionary changes permanently
    private void saveDictionary() {

        StringBuilder data = new StringBuilder();

        for (Map.Entry<String, Integer> entry :
                frequencies.entrySet()) {

            data.append(entry.getKey())
                .append(",")
                .append(entry.getValue())
                .append(System.lineSeparator());
        }

        try {
            Files.writeString(
                    DICTIONARY_FILE,
                    data.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            System.out.println("Error saving dictionary: "
                    + e.getMessage());
        }
    }

    // Add a new word
    public void addWord(String word, int frequency) {

        if (word == null) {
            return;
        }

        word = word.trim().toLowerCase();

        if (word.isEmpty() || frequency < 0) {
            return;
        }

        if (!frequencies.containsKey(word)) {

            trie.insertWord(word);
            frequencies.put(word, frequency);

            saveDictionary();
        }
    }

    // Get top suggestions for a prefix
    public List<Suggestion> getSuggestions(
            String prefix, int limit) {

        List<Suggestion> result = new ArrayList<>();

        if (prefix == null || limit <= 0) {
            return result;
        }

        prefix = prefix.trim().toLowerCase();

        if (prefix.isEmpty()) {
            return result;
        }

        List<String> matchingWords =
                trie.getWordsWithPrefix(prefix);

        MaxHeap maxHeap = new MaxHeap();

        for (String word : matchingWords) {

            int frequency =
                    frequencies.getOrDefault(word, 0);

            maxHeap.insert(
                    new Suggestion(word, frequency)
            );
        }

        while (!maxHeap.isEmpty() && result.size() < limit) {
            result.add(maxHeap.removeMax());
        }

        return result;
    }

    // Increase frequency after selecting a suggestion
    public void recordSearch(String word) {

        if (word == null) {
            return;
        }

        word = word.trim().toLowerCase();

        if (frequencies.containsKey(word)) {

            frequencies.put(
                    word,
                    frequencies.get(word) + 1
            );

            saveDictionary();
        }
    }
}
