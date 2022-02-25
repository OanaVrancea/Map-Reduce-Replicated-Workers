public class ReduceTaskResult {

    private String filename;
    private float rank;
    private int maxLen;
    private int maxLenNr;

    public ReduceTaskResult(String filename, float rank, int maxLen, int maxLenNr) {
        this.filename = filename;
        this.rank = rank;
        this.maxLen = maxLen;
        this.maxLenNr = maxLenNr;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public int getMaxLenNr() {
        return maxLenNr;
    }

    public void setMaxLenNr(int maxLenNr) {
        this.maxLenNr = maxLenNr;
    }



    @Override
    public String toString() {
        return "ReduceTaskResult{" +
                "filename='" + filename + '\'' +
                ", rank=" + rank +
                ", maxLen=" + maxLen +
                ", maxLenNr=" + maxLenNr +
                '}';
    }
}
