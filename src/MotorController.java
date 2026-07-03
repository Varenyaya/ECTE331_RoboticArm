/**
 * Shared hardware resource interface representing the robotic arm motor registers.
 * Acts as the baseline configuration to demonstrate basic mutual exclusion 
 * and unmanaged priority inversion.
 */
public class MotorController {

    // The 'synchronized' modifier enforces a binary monitor lock on this method.
    // It guarantees mutual exclusion, meaning only one thread can execute inside 
    // this critical section at any given moment to prevent data race conditions.
    public synchronized void accessResource(String taskName, int workTime) {

        System.out.println("[" + System.currentTimeMillis() + "] "
                + taskName + " ACQUIRED MotorController.");

        try {
            // Emulate the hardware operational window by suspending the thread
            // inside the critical section for the specified execution duration.
            Thread.sleep(workTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + taskName + " RELEASED MotorController.");
    }
}