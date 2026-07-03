/**
 * Entry point for demonstrating unmanaged Priority Inversion (Tasks 1-3).
 * Staggers thread startup sequences to force a low-priority thread to lock a shared resource 
 * right before a high-priority thread requests it, allowing a mid-priority thread to 
 * preempt execution on the CPU core.
 */
public class SimulationBaseline {

    public static void main(String[] args) {

        // Initialize the basic synchronized hardware controller proxy
        MotorController controller = new MotorController();

        // Instantiate tasks with explicit priority groupings and resource dependency configurations
        Thread logger = new Thread(
                new ArmTask("Logger (Low)", TaskType.LOGGER, controller, 3000, true));

        Thread motion = new Thread(
                new ArmTask("Motion Planner (Med)", TaskType.MOTION_PLANNER, controller, 2000, false));

        Thread safety = new Thread(
                new ArmTask("Safety Monitor (High)", TaskType.SAFETY_MONITOR, controller, 1000, true));

        // Assign native runtime priority levels to guide JRE/OS scheduling decisions
        logger.setPriority(Thread.MIN_PRIORITY);   // Priority 1
        motion.setPriority(Thread.NORM_PRIORITY);  // Priority 5
        safety.setPriority(Thread.MAX_PRIORITY);   // Priority 10

        System.out.println("\n--- BASELINE: PRIORITY INVERSION DEMO ---\n");

        long start = System.currentTimeMillis();

        // Step 1: Start Logger (Low) first to let it grab the exclusive lock on the resource
        logger.start();
        sleep(200); // 200ms delay window to ensure lock acquisition completes cleanly
        
        // Step 2: Start Safety Monitor (High). It will attempt resource entry and instantly block.
        safety.start();
        sleep(200); // Stagger interval before launching independent task workload
        
        // Step 3: Start Motion Planner (Med). It bypasses the lock entirely, but since its
        // priority (5) is higher than the Logger's priority (1), it preempts the Logger on the core.
        motion.start();

        try {
            // Block the main thread execution context until all concurrent workers complete their runs
            logger.join();
            safety.join();
            motion.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        // Captures end-to-end runtime, showing how the unmanaged baseline stretches the timeline out
        System.out.println("\nTOTAL SIMULATION TIME: " + (end - start) + " ms");
    }

    /**
     * Internal structural utility helper to handle local thread pausing.
     * Simplifies sequential execution staging without cluttering the main logic flow with try-catch blocks.
     */
    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}