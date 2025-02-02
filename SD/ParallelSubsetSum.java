import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelSubsetSum {
    private static final long TIMEOUT_SECONDS = 3600;
    
    public static int[] readNumbersFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                return Arrays.stream(line.trim().split("\\s+"))
                           .mapToInt(Integer::parseInt)
                           .toArray();
            }
            return new int[0];
        }
    }
    
    static class SubsetCounter implements Callable<Integer> {
        private final int[] numbers;
        private final int target;
        private final int start;
        private final int end;
        
        public SubsetCounter(int[] numbers, int target, int start, int end) {
            this.numbers = numbers;
            this.target = target;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public Integer call() {
            return countSubsets(numbers, target, start, end, new ArrayList<>());
        }
        
        private int countSubsets(int[] arr, int target, int start, int end, List<Integer> current) {
            if (target == 0) {
                return 1;
            }
            
            if (start > end) {
                return 0;
            }
            
            int count = 0;
            for (int i = start; i <= end; i++) {
                if (target - arr[i] >= 0) {
                    current.add(arr[i]);
                    count += countSubsets(arr, target - arr[i], i + 1, end, current);
                    current.remove(current.size() - 1);
                }
            }
            return count;
        }
    }
    
    public static int findSubsetCount(int[] numbers, int target, int numThreads) {
        if (numbers == null || numbers.length == 0) {
            return 0;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();
        AtomicInteger totalCount = new AtomicInteger(0);
        
        int segmentSize = numbers.length / numThreads;
        int remainder = numbers.length % numThreads;
        
        int startIndex = 0;
        
        for (int i = 0; i < numThreads; i++) {
            int threadSegmentSize = segmentSize + (i < remainder ? 1 : 0);
            int endIndex = startIndex + threadSegmentSize - 1;
            
            if (endIndex >= numbers.length) {
                endIndex = numbers.length - 1;
            }
            
            SubsetCounter counter = new SubsetCounter(numbers, target, startIndex, endIndex);
            futures.add(executor.submit(counter));
            
            startIndex = endIndex + 1;
        }
        
        try {
            long timeoutTime = System.currentTimeMillis() + (TIMEOUT_SECONDS * 1000);
            
            for (Future<Integer> future : futures) {
                try {
                    long remainingTime = timeoutTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        throw new TimeoutException("Computation timed out");
                    }
                    
                    int count = future.get(remainingTime, TimeUnit.MILLISECONDS);
                    totalCount.addAndGet(count);
                } catch (TimeoutException e) {
                    System.out.println("Computation timed out after " + TIMEOUT_SECONDS + " seconds");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
        
        return totalCount.get();
    }
    
    public static void main(String[] args) {
        try {
            int[] numbers = readNumbersFromFile("inst1000a.txt");
            System.out.println("Successfully read " + numbers.length + " numbers from file");
            
            int target = 2000;
            System.out.println("Targeting: " + target);
            
            System.out.println("Testing with different thread counts:");
            for (int threads = 1; threads <= 4; threads++) {
                long startTime = System.currentTimeMillis();
                int count = findSubsetCount(numbers, target, threads);
                long endTime = System.currentTimeMillis();
                
                System.out.printf("Threads: %d, Subsets found: %d, Time taken: %d ms%n",
                                threads, count, (endTime - startTime));
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
