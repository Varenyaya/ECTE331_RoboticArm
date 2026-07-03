
public class SimulationMain {
    public static void main(String[] args) {
        MotorController sharedMotor = new MotorController();

        // Logger runs long (3s) and needs resource. Med runs (2s) independently. High runs quick (1s) but needs resource.
        Thread logger = new Thread(new ArmTask("Logger (Low)", sharedMotor, 3000, true));
        Thread motionPlanner = new Thread(new ArmTask("Motion Planner (Med)", sharedMotor, 2000, false));
        Thread safetyMonitor = new Thread(new ArmTask("Safety Monitor (High)", sharedMotor, 1000, true));

        logger.setPriority(Thread.MIN_PRIORITY);      // 1
        motionPlanner.setPriority(Thread.NORM_PRIORITY); // 5
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("--- Starting Task 3: Priority Inversion Demonstration ---");

        try {
            // 1. Start Low priority task to seize the resource lock
            logger.start();
            Thread.sleep(200); 

            // 2. High priority task starts, tries to get lock, and becomes BLOCKED by Low
            safetyMonitor.start();
            Thread.sleep(200); 

            // 3. Medium priority task starts. It doesn't need the resource, but pre-empts Low.
            // This leaves High waiting indefinitely for Medium to finish!
            motionPlanner.start();

        } catch (InterruptedException e) {
            System.out.println("Main simulation execution interrupted.");
        }
    }
}