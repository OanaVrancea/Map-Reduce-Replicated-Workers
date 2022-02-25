import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        //read number of workers from args
        int workersNr = Integer.parseInt(args[0]);
        //read name of input file from args
        String inputFile = args[1];
        //read name of output file from args
        String outputFile = args[2];

        //start reading from input file
        Scanner scanner = new Scanner(new File(inputFile));

        //read length of a fragment
        int fragmentLength = Integer.parseInt(scanner.nextLine());

        //read number of documents
        int nrDocs = Integer.parseInt(scanner.nextLine());

        //array of the MapTasks which will be created
        ArrayList<MapTask> tasks = new ArrayList<MapTask>();

        //init an executor service with workersNr workers
        ExecutorService tpe = Executors.newFixedThreadPool(workersNr);

        //array of strings where the names of the files will be saved
        ArrayList<String> listOfFileNames = new ArrayList<>();

        /*
            the files are opened and map tasks are created
         */
        for(int i = 0; i < nrDocs; i++){
            String currentFileName = scanner.nextLine();
            listOfFileNames.add(currentFileName);
            File file = new File( currentFileName);
            long leftBytes = file.length();
            int startOffset = 0;
            while (leftBytes > fragmentLength){
                MapTask task = new MapTask(currentFileName, startOffset, fragmentLength, file.length(), tpe);
                tasks.add(task);
                leftBytes -= fragmentLength;
                startOffset += fragmentLength;
            }
            if(leftBytes > 0){
                MapTask task = new MapTask(currentFileName, startOffset, (int) leftBytes, file.length(), tpe);
                tasks.add(task);
            }
        }

        //array which contains the results of the map tasks
        ArrayList<Future<MapTaskResult>> resultsFromMapTasks= new ArrayList<>();

        //submit each task to the pool of task
        for (MapTask task : tasks) {
            Future<MapTaskResult> future = tpe.submit(task);
            resultsFromMapTasks.add(future);
        }

        //stop the executor service
        tpe.shutdown();

        //assign to each file their results after the map operation
        HashMap<String, ArrayList<MapTaskResult>> aux= new HashMap<>();

        for(int i = 0; i < listOfFileNames.size(); i++){
            String currentFileName = listOfFileNames.get(i);
            ArrayList<MapTaskResult> arrayList = new ArrayList<>();
            for(Future<MapTaskResult> r : resultsFromMapTasks){
                try {
                    if(r.get().getFilename().equals(listOfFileNames.get(i))){
                        arrayList.add(r.get());
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            aux.put(currentFileName, arrayList);
        }


        //init an executor service with workersNr workers
        ExecutorService tpeReduce = Executors.newFixedThreadPool(workersNr);

        ArrayList<ReduceTask> tasksReduce = new ArrayList<>();

        for (Map.Entry<String, ArrayList<MapTaskResult>> mapElement : aux.entrySet()) {
            //append the dictionaries and list of maximum words from the array of MapTaskResults
            ArrayList<HashMap<Integer, Integer>> appendedDictionaries = new ArrayList<>();
            ArrayList<ArrayList<String>> appendedListsOfMaxLenWords = new ArrayList<>();
            for(MapTaskResult task : mapElement.getValue()){
                appendedDictionaries.add(task.getDictionary());
            }
            for(MapTaskResult task : mapElement.getValue()){
                appendedListsOfMaxLenWords.add(task.getMaxLenWords());
            }
            ReduceTask reduceTask = new ReduceTask(mapElement.getKey(), tpeReduce, mapElement.getValue(), appendedDictionaries, appendedListsOfMaxLenWords);
            tasksReduce.add(reduceTask);
        }


        //reduce tasks are created and added to an array

        ArrayList<Future<ReduceTaskResult>> resultsFromReduceTasks= new ArrayList<>();
        ArrayList<ReduceTaskResult> output = new ArrayList<>();

        for (ReduceTask task : tasksReduce) {
            Future<ReduceTaskResult> future = tpeReduce.submit(task);
            resultsFromReduceTasks.add(future);
        }


        for(Future<ReduceTaskResult> r : resultsFromReduceTasks) {
            while (!r.isDone()) {
             //   Thread.sleep(300);
            }
            try {
                output.add(r.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        tpeReduce.shutdown();

        //sort the reduce task results after their rank
       output.sort(Comparator.comparing(ReduceTaskResult::getRank).reversed());


       //write the result in output file
        File fout = new File(outputFile);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (ReduceTaskResult r : output) {
            String name = r.getFilename().substring(12);
            bw.write(name +"," + String.format("%.2f", r.getRank()) +"," + r.getMaxLen() +"," + r.getMaxLenNr());
            bw.newLine();
        }

        bw.close();

    }
}
