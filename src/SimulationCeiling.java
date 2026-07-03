/**
 * Test harness for evaluating the Priority Ceiling Protocol (Task 5 & 6).
 * Executes 10 sequential iterations of the simulation configuration to eliminate 
 * random operating system scheduling noise and calculate stable performance averages.
 */
public class SimulationCeiling {

    // Number of experimental loops required for compiling statistical averages
    private static final int RUNS = 10;

    public static void main(String[] args) {

        long totalWaiting = 0;
        long totalResponse = 0;

        System.out.println("\n--- PRIORITY CEILING EXPERIMENT ---\n");

        // Loop harness to gather multiple data points for the evaluation report
        for (int i = 0; i < RUNS; i++) {

            // Create a fresh instance of the protocol monitor for this test iteration
            PriorityCeilingResource resource = new PriorityCeilingResource();

            // Instantiate tasks matching the core embedded application specifications
            Thread logger = new Thread(
                    new ArmTaskCeiling("Logger (Low)", TaskType.LOGGER, resource, 3000, true));

            Thread motion = new Thread(
                    new ArmTaskCeiling("Motion Planner (Med)", TaskType.MOTION_PLANNER, resource, 2000, false));

            Thread safety = new Thread(
                    new ArmTaskCeiling("Safety Monitor (High)", TaskType.SAFETY_MONITOR, resource, 1000, true));

            // Enforce system priority assignments (1, 5, and 10)
            logger.setPriority(Thread.MIN_PRIORITY);
            motion.setPriority(Thread.NORM_PRIORITY);
            safety.setPriority(Thread.MAX_PRIORITY);

            // Stagger task execution paths using the defined milestone intervals
            logger.start();
            sleep(200); // Allow Low to secure the resource and immediately scale to ceiling priority
            
            safety.start();
            sleep(200); // Stagger interval before activating independent workload
            
            motion.start(); // Medium begins but cannot interfere due to active ceiling protection

            try {
                // Suspend loop execution until current thread run completes entirely
                logger.join();
                safety.join();
                motion.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Extract high-precision temporal metrics recorded inside the resource monitor
            Metrics m = resource.getMetrics();

            long wait = m.getWaitingTime();
            long response = m.getResponseTime();

            // Accumulate metrics for computing systemic mean values
            totalWaiting += wait;
            totalResponse += response;

            System.out.println("Run " + (i + 1) +
                    " | Waiting: " + wait +
                    " ms | Response: " + response + " ms");
        }

        // Output finalized mathematical averages for the performance data section
        System.out.println("\n===== CEILING AVERAGES =====");
        System.out.println("Avg Waiting Time: " + (totalWaiting / RUNS) + " ms");
        System.out.println("Avg Response Time: " + (totalResponse / RUNS) + " ms");
    }

    /**
     * Staging utility function used to precisely sequence the initial arrival times 
     * of the task threads.
     */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}