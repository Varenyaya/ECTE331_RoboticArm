public class SimulationCeiling {
    public static void main(String[] args) {
        PriorityCeilingResource sharedResource = new PriorityCeilingResource();

        Thread logger = new Thread(new CeilingArmTask("Logger (Low)", sharedResource, 3000, true));
        Thread motionPlanner = new Thread(new CeilingArmTask("Motion Planner (Med)", sharedResource, 2000, false));
        Thread safetyMonitor = new Thread(new CeilingArmTask("Safety Monitor (High)", sharedResource, 1000, true));

        logger.setPriority(Thread.MIN_PRIORITY);      // 1
        motionPlanner.setPriority(Thread.NORM_PRIORITY); // 5
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("--- Starting Task 5: Priority Ceiling Protocol ---");

        // Track when the High Priority task is spawned
        long highStartTime = System.currentTimeMillis();

        try {
            logger.start();
            Thread.sleep(200); 

            safetyMonitor.start();
            Thread.sleep(200);

            motionPlanner.start();

            // Wait for all threads to finish completely
            safetyMonitor.join();
            logger.join();
            motionPlanner.join();
            
            // Metrics Calculations
            long highEndTime = System.currentTimeMillis();
            long totalLatency = highEndTime - highStartTime;
            long waitingTime = totalLatency - 1000; 

            System.out.println("\n==================================================");
            System.out.println(">>> [METRIC] Safety Monitor (High) Total Execution Latency: " + totalLatency + " ms");
            System.out.println(">>> [METRIC] Safety Monitor (High) Pure Waiting Time: " + waitingTime + " ms");
            System.out.println("==================================================\n");

        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }
    }
}