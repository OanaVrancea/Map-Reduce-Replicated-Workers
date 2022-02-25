import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class MapTask implements Callable<MapTaskResult> {

    private String filename;
    private int offset;
    private int newOffset;
    private int size;
    private int leftSize;
    private long sizeOfFile;
    private ExecutorService tpe;
    private String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
    private HashMap<Integer, Integer> dictionary = new HashMap<>();
    private ArrayList<String> maxLenWords = new ArrayList<>();

    public MapTask(String filename, int offset, int size, long sizeOfFile, ExecutorService tpe) {
        this.filename = filename;
        this.offset = offset;
        this.size = size;
        this.sizeOfFile = sizeOfFile;
        this.tpe = tpe;
    }

    @Override
    public MapTaskResult call() throws Exception {

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //this function will return result
        MapTaskResult result = new MapTaskResult(filename);

        byte[] fragmentStartByte = new byte[1];
        byte[] fragmentEndByte = new byte[1];
        byte[] beforeFragmentByte = new byte[1];
        byte[] afterFragmentByte = new byte[1];


        this.newOffset = this.offset;

        //check if at given offset it's a delimiter or not
        assert file != null;
        try {
            file.seek(this.offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.read(fragmentStartByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String stringFragmentStartByte = new String(fragmentStartByte, StandardCharsets.UTF_8);
        //if character at offset is not a delimiter, check if the characer before is a
        //delimiter or not
        if(!separators.contains(stringFragmentStartByte) && this.offset >= 1){
            try {
                file.seek(this.offset - 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                file.read(beforeFragmentByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String stringBeforeFragmentByte = new String(beforeFragmentByte, StandardCharsets.UTF_8);
            //if the characters at offset-1 and offset are not delimiters, ignore characters until
            //a delimiter is reached
            if(!separators.contains(stringBeforeFragmentByte) && this.newOffset < this.sizeOfFile ){
                this.newOffset++;
                try {
                    file.seek(this.newOffset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    file.read(fragmentStartByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stringFragmentStartByte = new String(fragmentStartByte, StandardCharsets.UTF_8);
                while(!separators.contains(stringFragmentStartByte) && this.newOffset < this.sizeOfFile){
                    this.newOffset++;
                    try {
                        file.seek(this.newOffset);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        file.read(fragmentStartByte);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringFragmentStartByte = new String(fragmentStartByte, StandardCharsets.UTF_8);
                }
            }
        }

        //read all the remaining bytes
        this.leftSize = this.size - (this.newOffset - this.offset);
        byte[] fragment = new byte[this.leftSize];
        try {
            file.seek(this.newOffset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.read(fragment);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String stringFragment = new String(fragment, StandardCharsets.UTF_8);

        String toAppend = "";

        //check if the fragment ends in the middle of a word
        //check if the last character is not a delimiter
        try {
            file.seek(this.newOffset + this.leftSize - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file.read(fragmentEndByte);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String stringFragmentEndByte = new String(fragmentEndByte, StandardCharsets.UTF_8);
        //if the following character is not a delimiter, keep reading until a delimiter is reached
        if(!separators.contains(stringFragmentEndByte) && (this.newOffset + this.leftSize < this.sizeOfFile)){
            try {
                file.seek(this.newOffset + this.leftSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                file.read(afterFragmentByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String stringAfterFragmentByte = new String(afterFragmentByte, StandardCharsets.UTF_8);
            if(!separators.contains(stringAfterFragmentByte) && this.newOffset + this.leftSize < this.sizeOfFile ){
                toAppend += stringAfterFragmentByte;
                this.leftSize++;
                try {
                    file.seek(this.newOffset + this.leftSize);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    file.read(fragmentEndByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stringFragmentEndByte = new String(fragmentEndByte, StandardCharsets.UTF_8);
                while(!separators.contains(stringFragmentEndByte) && this.newOffset + this.leftSize < this.sizeOfFile){
                    toAppend +=  stringFragmentEndByte;
                    this.leftSize++;
                    try {
                        file.seek(this.newOffset + this.leftSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        file.read(fragmentEndByte);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringFragmentEndByte = new String(fragmentEndByte, StandardCharsets.UTF_8);

                }
            }
        }

        //stringFragment contains the final fragment
        stringFragment += toAppend;


        String[] tokens = stringFragment.split("\\W+");

        HashMap<Integer, Integer> map = new HashMap<>();
        ArrayList<String> listOfWords = new ArrayList<>();

        result.setMaxLen(0);

        //create the dictionary and the list of words of maximum size
        for(int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                int currentLen = tokens[i].length();

                if (currentLen == result.getMaxLen()) {
                    listOfWords.add(tokens[i]);
                } else if (currentLen > result.getMaxLen()) {
                    result.setMaxLen(currentLen);
                    listOfWords.removeAll(listOfWords);
                    listOfWords.add(tokens[i]);
                }

                if (map.containsKey(currentLen)) {
                    map.put(currentLen, map.get(currentLen) + 1);
                } else {
                    map.put(currentLen, 1);
                }

            }
        }

        //set result parameters and return it

        result.setDictionary(map);
        result.setMaxLenWords(listOfWords);

        return result;

    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        return "MapTask{" +
                "filename='" + filename + '\'' +
                ", dictionary=" + dictionary +
                ", maxLenWords=" + maxLenWords +
                '}';
    }
}
