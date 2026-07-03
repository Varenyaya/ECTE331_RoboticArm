
public class SimulationMain {
    public static void main(String[] args) {
        MotorController sharedMotor = new MotorController();

        Thread logger = new Thread(new ArmTask("Logger (Low)", sharedMotor, 1000, true));
        Thread motionPlanner = new Thread(new ArmTask("Motion Planner (Med)", sharedMotor, 1000, false));
        Thread safetyMonitor = new Thread(new ArmTask("Safety Monitor (High)", sharedMotor, 1000, true));

        // Assign native Java thread priorities (Task 1)
        logger.setPriority(Thread.MIN_PRIORITY);      // 1
        motionPlanner.setPriority(Thread.NORM_PRIORITY); // 5
        safetyMonitor.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("--- Starting Basic Project Simulation ---");
        logger.start();
        motionPlanner.start();
        safetyMonitor.start();
    }
}