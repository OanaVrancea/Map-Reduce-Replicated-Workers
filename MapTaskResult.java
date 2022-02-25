import java.util.ArrayList;
import java.util.HashMap;

public class MapTaskResult {
    private String filename;
    private HashMap<Integer, Integer> dictionary = new HashMap<>();
    private ArrayList<String> maxLenWords = new ArrayList<>();
    private int maxLen;

    public MapTaskResult(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public HashMap<Integer, Integer> getDictionary() {
        return dictionary;
    }

    public void setDictionary(HashMap<Integer, Integer> dictionary) {
        this.dictionary = dictionary;
    }

    public void addDictionary(int key, int value) {
        this.dictionary.put(key, value);
    }

    public ArrayList<String> getMaxLenWords() {
        return maxLenWords;
    }

    public void setMaxLenWords(ArrayList<String> maxLenWords) {
        this.maxLenWords = maxLenWords;
    }

    public void addMaxLenWords(String word) {
        this.maxLenWords.add(word);
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    @Override
    public String toString() {
        return "MapTaskResult{" +
                "filename='" + filename + '\'' +
                ", dictionary=" + dictionary +
                ", maxLenWords=" + maxLenWords +
                ", maxLen=" + maxLen +
                '}';
    }
}
