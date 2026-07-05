/**
 * Entry point for demonstrating unmanaged Priority Inversion (Tasks 1-3).
 * Executes 10 sequential iterations under dynamic scheduler pressure to establish
 * a statistically sound baseline average for performance comparison.
 */
public class SimulationBaseline {

    private static final int RUNS = 10;
    private static double totalWaiting = 0;
    private static double totalResponse = 0;
    
    // This is our high-precision metrics engine for the current run cycle
    private static Metrics currentMetrics;

    public static Metrics getMetrics() {
        return currentMetrics;
    }

    public static void main(String[] args) {
        System.out.println("\n--- BASELINE: PRIORITY INVERSION EXPERIMENT ---\n");

        for (int i = 0; i < RUNS; i++) {
            // Instantiate a fresh metrics tracker for this iteration block
            currentMetrics = new Metrics();

            // Deploy identical background contention noise thread for this run iteration
            startBackgroundNoise();

            MotorController controller = new MotorController();

            Thread logger = new Thread(
                    new ArmTask("Logger (Low)", TaskType.LOGGER, controller, 3000, true));

            Thread motion = new Thread(
                    new ArmTask("Motion Planner (Med)", TaskType.MOTION_PLANNER, controller, 2000, false));

            Thread safety = new Thread(
                    new ArmTask("Safety Monitor (High)", TaskType.SAFETY_MONITOR, controller, 1000, true));

            logger.setPriority(Thread.MIN_PRIORITY);   // Priority 1
            motion.setPriority(Thread.NORM_PRIORITY);  // Priority 5
            safety.setPriority(Thread.MAX_PRIORITY);   // Priority 10

            logger.start();
            sleep(500); // Head start to ensure initial lock acquisition completes cleanly
            
            // FIXED: Using our static benchmark pipeline cleanly without calling the controller
            currentMetrics.recordRequest();
            safety.start();
            sleep(100);
            motion.start();

            try {
                logger.join();
                safety.join();
                motion.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            double wait = currentMetrics.getWaitingTime();
            double response = currentMetrics.getResponseTime();

            totalWaiting += wait;
            totalResponse += response;

            System.out.printf("Baseline Run %d | Waiting: %.2f ms | Response: %.2f ms%n", (i + 1), wait, response);
        }

        System.out.println("\n===== BASELINE AVERAGES =====");
        System.out.printf("Avg Waiting Time: %.2f ms%n", (totalWaiting / RUNS));
        System.out.printf("Avg Response Time: %.2f ms%n", (totalResponse / RUNS));
    }

    public static double getAvgWaiting() {
        return totalWaiting / RUNS;
    }

    public static double getAvgResponse() {
        return totalResponse / RUNS;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startBackgroundNoise() {
        Thread noise = new Thread(() -> {
            long threadStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - threadStart < 7000) {
                double volatileVar = Math.sin(Math.random()) * Math.cos(Math.random());
            }
        });
        noise.setPriority(Thread.NORM_PRIORITY); // Priority 5
        noise.setDaemon(true); 
        noise.start();
    }
}