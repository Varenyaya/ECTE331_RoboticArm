/**
 * Test harness for validating the Priority Inheritance Protocol (Task 4 & 6).
 * Runs 10 consecutive loops of the simulation scenario to capture stable latency 
 * metrics and calculate averages, screening out background operating system noise.
 */
public class SimulationInheritance {

    // Target iteration threshold required to build an academically sound performance average
    private static final int RUNS = 10;

    public static void main(String[] args) {

        long totalWaiting = 0;
        long totalResponse = 0;

        System.out.println("\n--- PRIORITY INHERITANCE EXPERIMENT ---\n");

        // Main iteration loop for gathering multiple runtime data samples
        for (int i = 0; i < RUNS; i++) {

            // Provide a isolated inheritance lock monitor instance for this execution run
            PriorityInheritanceResource resource = new PriorityInheritanceResource();

            // Setup the system task threads using our customized inheritance workers
            Thread logger = new Thread(
                    new ArmTaskInheritance("Logger (Low)", TaskType.LOGGER, resource, 3000, true));

            Thread motion = new Thread(
                    new ArmTaskInheritance("Motion Planner (Med)", TaskType.MOTION_PLANNER, resource, 2000, false));

            Thread safety = new Thread(
                    new ArmTaskInheritance("Safety Monitor (High)", TaskType.SAFETY_MONITOR, resource, 1000, true));

            // Enforce thread priorities relative to system architecture constraints (1, 5, 10)
            logger.setPriority(Thread.MIN_PRIORITY);
            motion.setPriority(Thread.NORM_PRIORITY);
            safety.setPriority(Thread.MAX_PRIORITY);

            // Step 1: Launch Logger (Low) to seize the lock monitor first
            logger.start();
            sleep(200); // Wait for the lock acquisition sequence to settle
            
            // Step 2: Launch Safety Monitor (High). This will instantly trigger the priority boost.
            safety.start();
            sleep(200); // Maintain a clean offset before bringing in the unblocked task
            
            // Step 3: Launch Motion Planner (Med). It tries to run but gets blocked out by 
            // the Logger's newly inherited high priority.
            motion.start();

            try {
                // Block loop progression until the concurrent threads finish completely
                logger.join();
                safety.join();
                motion.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Grab the timing values captured from the safety monitor's request session
            Metrics m = resource.getMetrics();

            long wait = m.getWaitingTime();
            long response = m.getResponseTime();

            // Sum up the metrics for our final performance summary math
            totalWaiting += wait;
            totalResponse += response;

            System.out.println("Run " + (i + 1) +
                    " | Waiting: " + wait +
                    " ms | Response: " + response + " ms");
        }

        // Print final parsed average outcomes to copy directly into your evaluation report
        System.out.println("\n===== INHERITANCE AVERAGES =====");
        System.out.println("Avg Waiting Time: " + (totalWaiting / RUNS) + " ms");
        System.out.println("Avg Response Time: " + (totalResponse / RUNS) + " ms");
    }

    /**
     * Staging delay function used to cleanly separate the initial arrival times 
     * of the competing tasks.
     */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}