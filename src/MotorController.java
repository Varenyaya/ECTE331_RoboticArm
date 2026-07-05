/**
 * Shared hardware resource interface representing the robotic arm motor registers.
 * Acts as the baseline configuration to demonstrate basic mutual exclusion 
 * and unmanaged priority inversion.
 */
public class MotorController {

    // Enforces a binary monitor lock. Tracks timestamps relative to the external trigger bounds.
    public synchronized void accessResource(String taskName, int workTime, TaskType type) {
        
        // Record the actual acquisition timestamp the moment the thread clears the synchronization pool
        if (type == TaskType.SAFETY_MONITOR) {
            SimulationBaseline.getMetrics().recordAcquire();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + taskName + " ACQUIRED MotorController.");

        try {
            // Inject dynamic operational jitter (±100ms) to simulate structural hardware variance
            int jitter = (int)(Math.random() * 200 - 100);
            Thread.sleep(Math.max(0, workTime + jitter));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + taskName + " RELEASED MotorController.");

        // Record the final completion timestamp when the safety task exits the critical block completely
        if (type == TaskType.SAFETY_MONITOR) {
            SimulationBaseline.getMetrics().recordFinish();
        }
    }
}