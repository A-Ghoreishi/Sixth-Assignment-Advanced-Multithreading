package sbu.cs.CalculatePi;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class PiCalculator implements Runnable{
    /**
     * Calculate pi and represent it as a BigDecimal object with the given floating point number (digits after . )
     * There are several algorithms designed for calculating pi, it's up to you to decide which one to implement.
     Experiment with different algorithms to find accurate results.

     * You must design a multithreaded program to calculate pi. Creating a thread pool is recommended.
     * Create as many classes and threads as you need.
     * Your code must pass all of the test cases provided in the test folder.

     * @param floatingPoint the exact number of digits after the floating point
     * @return pi in string format (the string representation of the BigDecimal object)
     */

    // Set the precision
    private static final MathContext mc = new MathContext(1050); // Use 1050 precision to ensure accuracy

    // Constants for the calculation
    private static final int ITERATIONS = 1002; // Total number of iterations
    private static final int NUM_THREADS = 4; // Number of threads

    // Starting value
    private static BigDecimal Pi = BigDecimal.ZERO;
    private static final Object lock = new Object();

    // Fields for thread range
    private int start;
    private int end;

    public PiCalculator() {
        // Default constructor
    }

    // Method to set range for each thread
    public void setRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    //* the algorithm of BBP formula in run method*/
    @Override
    public void run() {
        try {
            BigDecimal pi = BigDecimal.ZERO;
            for (int k = start; k < end + 2; k++) {
                BigDecimal a = BigDecimal.ONE.divide(BigDecimal.valueOf(16).pow(k), mc);
                BigDecimal b = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8 * k + 1), mc);
                BigDecimal c = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8 * k + 4), mc);
                BigDecimal d = BigDecimal.ONE.divide(BigDecimal.valueOf(8 * k + 5), mc);
                BigDecimal e = BigDecimal.ONE.divide(BigDecimal.valueOf(8 * k + 6), mc);

                // Update the local estimate
                pi = pi.add(a.multiply(b.subtract(c).subtract(d).subtract(e)));
            }

            // Synchronize the update to the shared Pi variable
            synchronized (lock) {
                Pi = Pi.add(pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*calculating Pi value using threads*/
    public String calculate(int floatingPoint) {
        ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);
        int chunkSize = ITERATIONS / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            PiCalculator calculator = new PiCalculator();
            int start = i * chunkSize;
            int end = (i == NUM_THREADS -1) ? ITERATIONS : start + chunkSize;
            calculator.setRange(start, end);
            threadPool.execute(calculator);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String PI = Pi.toString();

        // Return the calculated value of Pi as a string with the desired precision
        return PI.substring(0, Math.min(floatingPoint + 2, PI.length()));
    }

    public static void main(String[] args) {
        // Create an instance of PiCalculator
        PiCalculator piCalculator = new PiCalculator();

        String piValue = piCalculator.calculate(1000); // Calculate pi to specified decimal places
        System.out.println(piValue);
        // Print 1000 digits plus "3."
    }


}