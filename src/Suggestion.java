
public class Suggestion {

    private String word;
    private int frequency;

    public Suggestion(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return word + " (" + frequency + " searches)";
    }
}

