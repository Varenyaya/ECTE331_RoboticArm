/**
 * Test harness for evaluating the Priority Ceiling Protocol (Task 5 & 6).
 * Executes 10 sequential iterations under identical background preemption stress.
 */
public class SimulationCeiling {

    private static final int RUNS = 10;
    private static double totalWaiting = 0;
    private static double totalResponse = 0;

    public static void main(String[] args) {
        System.out.println("\n--- PRIORITY CEILING EXPERIMENT ---\n");

        for (int i = 0; i < RUNS; i++) {
            startBackgroundNoise();

            PriorityCeilingResource resource = new PriorityCeilingResource();

            Thread logger = new Thread(
                    new ArmTaskCeiling("Logger (Low)", TaskType.LOGGER, resource, 3000, true));

            Thread motion = new Thread(
                    new ArmTaskCeiling("Motion Planner (Med)", TaskType.MOTION_PLANNER, resource, 2000, false));

            Thread safety = new Thread(
                    new ArmTaskCeiling("Safety Monitor (High)", TaskType.SAFETY_MONITOR, resource, 1000, true));

            logger.setPriority(Thread.MIN_PRIORITY);
            motion.setPriority(Thread.NORM_PRIORITY);
            safety.setPriority(Thread.MAX_PRIORITY);

            logger.start();
            sleep(500); 
            
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

            Metrics m = resource.getMetrics();
            double wait = m.getWaitingTime();
            double response = m.getResponseTime();

            totalWaiting += wait;
            totalResponse += response;

            System.out.printf("Ceiling Run %d | Waiting: %.2f ms | Response: %.2f ms%n", (i + 1), wait, response);
        }

        System.out.println("\n===== CEILING AVERAGES =====");
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
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 7000) {
                double volatileVar = Math.sin(Math.random()) * Math.cos(Math.random());
            }
        });
        noise.setPriority(Thread.NORM_PRIORITY); 
        noise.setDaemon(true);
        noise.start();
    }
}