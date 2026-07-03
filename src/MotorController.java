
public class MotorController {
    // The synchronized keyword ensures only one thread can access this at a time (Task 2)
    public synchronized void accessResource(String threadName, int holdTimeMs) {
        System.out.println("[" + System.currentTimeMillis() + "] " + threadName + " ACQUIRED MotorController.");
        try {
            Thread.sleep(holdTimeMs);
        } catch (InterruptedException e) {
            System.out.println(threadName + " was interrupted.");
        }
        System.out.println("[" + System.currentTimeMillis() + "] " + threadName + " RELEASED MotorController.");
    }
}