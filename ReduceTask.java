import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class ReduceTask implements Callable<ReduceTaskResult> {
    private String filename;
    private ArrayList<MapTaskResult> infoList = new ArrayList<>();
    ExecutorService tpeReduce;
    private HashMap<Integer, Integer> dictionary = new HashMap<>();
    private ArrayList<String> maxLenWords = new ArrayList<>();
    private ArrayList<HashMap<Integer, Integer>> appendedDictionaries;
    private ArrayList<ArrayList<String>> appendedListsOfMaxLenWords;
    private int maxLenWord = 0;
    private float totalNumberOfWords;
    private float sum;
    private float[] fibonacciNr = new float[]{0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987};


    public ReduceTask(String filename, ExecutorService tpeReduce,  ArrayList<MapTaskResult> infoList,
                      ArrayList<HashMap<Integer, Integer>> appendedDictionaries,
                      ArrayList<ArrayList<String>> appendedListsOfMaxLenWords) {
        this.filename = filename;
        this.tpeReduce = tpeReduce;
        this.infoList = infoList;
        this.appendedDictionaries = appendedDictionaries;
        this.appendedListsOfMaxLenWords = appendedListsOfMaxLenWords;
    }

    @Override
    public ReduceTaskResult call() throws Exception {
        computeMaxLenWord();

        //first stage, the dictionary and the list of words is created
        createDictionary();
        createListOfMaxLenWords();

        //second stage, the rank is computed
        computeTotalNrOfWords();
        computeSum();
        float rank = (float)(sum / totalNumberOfWords);

        ReduceTaskResult result = new ReduceTaskResult(filename, rank, maxLenWord, maxLenWords.size() );

        return result;

    }

    public void computeSum(){
        for(Map.Entry<Integer, Integer> mapElem : dictionary.entrySet()){
            sum += fibonacciNr[mapElem.getKey() + 1]  * mapElem.getValue();
        }
    }

    public void computeTotalNrOfWords(){
        for(Map.Entry<Integer, Integer> mapElem : dictionary.entrySet()){
            totalNumberOfWords += mapElem.getValue();
        }
    }

    public void computeMaxLenWord(){
        for(MapTaskResult task : infoList){
            if(task.getMaxLen() > maxLenWord){
                maxLenWord = task.getMaxLen();
            }
        }
    }

    public void createDictionary(){
        for(HashMap<Integer, Integer> d : appendedDictionaries){
            for(Map.Entry<Integer, Integer> mapElem : d.entrySet()){
                if(!dictionary.containsKey(mapElem.getKey())){
                    dictionary.put(mapElem.getKey(), mapElem.getValue());
                } else {
                    dictionary.put(mapElem.getKey(), mapElem.getValue() + dictionary.get(mapElem.getKey()));
                }
            }
        }
    }

    public void createListOfMaxLenWords(){
        for(MapTaskResult task : infoList){
            if(task.getMaxLen() == maxLenWord){
                for(String s : task.getMaxLenWords()){
                    maxLenWords.add(s);
                }
            }
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<MapTaskResult> getInfoList() {
        return infoList;
    }

    public void setInfoList(ArrayList<MapTaskResult> infoList) {
        this.infoList = infoList;
    }

    public HashMap<Integer, Integer> getDictionary() {
        return dictionary;
    }

    public void setDictionary(HashMap<Integer, Integer> dictionary) {
        this.dictionary = dictionary;
    }

    public ArrayList<String> getMaxLenWords() {
        return maxLenWords;
    }

    public void setMaxLenWords(ArrayList<String> maxLenWords) {
        this.maxLenWords = maxLenWords;
    }

    @Override
    public String toString() {
        return "ReduceTask{" +
                "filename='" + filename + '\'' +
                ", appendedDictionaries=" + appendedDictionaries +
                ", appendedListsOfMaxLenWords=" + appendedListsOfMaxLenWords +
                '}';
    }
}
