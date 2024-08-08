import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int numThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(() -> {
                String route = generateRoute("RLRFR", 100);
                int rCount = countOccurrences(route, 'R');
                updateFrequencyMap(rCount);
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();

        printResults();

    }

    public static void printResults() {
        int maxFrequency = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
        int maxCount = sizeToFreq.get(maxFrequency);

        System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + maxCount + " раз)");
        System.out.println("Другие размеры:");

        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    if (entry.getKey() != maxFrequency) {
                        System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
                    }
                });
    }


    public static synchronized void updateFrequencyMap(int count) {
        sizeToFreq.merge(count, 1, Integer::sum);
    }


    public static int countOccurrences(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}