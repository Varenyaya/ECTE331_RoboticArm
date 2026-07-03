public class SimulationInheritance {
    public static void main(String[] args) {
        PriorityInheritanceResource sharedResource = new PriorityInheritanceResource();

        Thread logger = new Thread(new InheritanceArmTask("Logger (Low)", sharedResource, 3000, true));
        Thread motionPlanner = new Thread(new InheritanceArmTask("Motion Planner (Med)", sharedResource, 2000, false));
        Thread safetyMonitor = new Thread(new InheritanceArmTask("Safety Monitor (High)", sharedResource, 1000, true));

        logger.setPriority(Thread.MIN_PRIORITY);      // 1
        motionPlanner.setPriority(Thread.NORM_PRIORITY); // 5
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("--- Starting Task 4: Priority Inheritance Protocol ---");

        try {
            logger.start();
            Thread.sleep(200); // Give Low time to grab lock

            safetyMonitor.start();
            Thread.sleep(200); // Give High time to block and trigger priority boost

            motionPlanner.start(); // Medium starts, but cannot pre-empt Low anymore!

        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted.");
        }
    }
}