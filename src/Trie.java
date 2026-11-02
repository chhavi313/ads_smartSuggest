
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trie {

    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert a word into the Trie
    public void insertWord(String word) {

        word = word.toLowerCase().trim();

        if (word.isEmpty()) {
            return;
        }

        TrieNode current = root;

        for (char ch : word.toCharArray()) {

            current.children.putIfAbsent(ch, new TrieNode());

            current = current.children.get(ch);
        }

        current.isEndOfWord = true;
    }

    // Find all words beginning with a prefix
    public List<String> getWordsWithPrefix(String prefix) {

        List<String> result = new ArrayList<>();

        prefix = prefix.toLowerCase().trim();

        TrieNode current = root;

        // Navigate to the prefix node
        for (char ch : prefix.toCharArray()) {

            if (!current.children.containsKey(ch)) {
                return result;
            }

            current = current.children.get(ch);
        }

        // Collect all words below the prefix node
        collectWords(current, prefix, result);

        return result;
    }

    // Recursive function to collect complete words
    private void collectWords(
            TrieNode node,
            String word,
            List<String> result) {

        if (node.isEndOfWord) {
            result.add(word);
        }

        for (Map.Entry<Character, TrieNode> entry
                : node.children.entrySet()) {

            collectWords(
                entry.getValue(),
                word + entry.getKey(),
                result
            );
        }
    }
}
