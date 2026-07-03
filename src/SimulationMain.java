public class SimulationMain {
    public static void main(String[] args) {
        MotorController sharedMotor = new MotorController();

        Thread logger = new Thread(new ArmTask("Logger (Low)", sharedMotor, 3000, true));
        Thread motionPlanner = new Thread(new ArmTask("Motion Planner (Med)", sharedMotor, 2000, false));
        Thread safetyMonitor = new Thread(new ArmTask("Safety Monitor (High)", sharedMotor, 1000, true));

        logger.setPriority(Thread.MIN_PRIORITY);      // 1
        motionPlanner.setPriority(Thread.NORM_PRIORITY); // 5
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("--- Starting Task 3: Priority Inversion Demonstration ---");

        // Track exactly when the High Priority task is spawned and starts waiting
        long highStartTime = System.currentTimeMillis();

        try {
            // 1. Start Low priority task to seize the resource lock
            logger.start();
            Thread.sleep(200); 

            // 2. High priority task starts, tries to get lock, and becomes BLOCKED by Low
            safetyMonitor.start();
            Thread.sleep(200); 

            // 3. Medium priority task starts. Preempts Low.
            motionPlanner.start();

            // --- NEW METRIC TRACKING SECTION ---
            // Wait for the threads to completely finish running before printing metrics
            safetyMonitor.join();
            logger.join();
            motionPlanner.join();
            
            // Calculate total time elapsed since High thread started until it completed
            long highEndTime = System.currentTimeMillis();
            long totalLatency = highEndTime - highStartTime;
            
            // Note: The execution time of High is 1000ms. The rest is the time it spent WAITING.
            long waitingTime = totalLatency - 1000; 

            System.out.println("\n==================================================");
            System.out.println(">>> [METRIC] Safety Monitor (High) Total Execution Latency: " + totalLatency + " ms");
            System.out.println(">>> [METRIC] Safety Monitor (High) Pure Waiting Time: " + waitingTime + " ms");
            System.out.println("==================================================\n");

        } catch (InterruptedException e) {
            System.out.println("Main simulation execution interrupted.");
        }
    }
}