import java.util.HashMap;
import java.util.Map;

/**
 * Shared resource monitor implementing the Priority Ceiling Protocol (PCP).
 * Instantly escalates any task's priority to the maximum systemic limit upon 
 * lock acquisition to eliminate mid-tier preemptions entirely.
 */
public class PriorityCeilingResource {

    private boolean locked = false;
    private Thread owner = null;

    // Define the immutable priority ceiling equivalent to the highest-priority competitor (Priority 10)
    private static final int CEILING_PRIORITY = Thread.MAX_PRIORITY;

    // Stashes original base priorities so threads can be reverted cleanly back to their native levels on release
    private final Map<Thread, Integer> originalPriorities = new HashMap<>();

    private final Metrics metrics = new Metrics();

    public Metrics getMetrics() {
        return metrics;
    }

    public synchronized void lock(TaskType type) {
        Thread current = Thread.currentThread();

        // Start the benchmark stopwatch if the calling thread is the critical safety task
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordRequest();
        }

        // Standard mutual exclusion check loop. If locked, the thread waits.
        while (locked) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Claim resource ownership
        locked = true;
        owner = current;

        // Archive the thread's existing priority before overriding it
        originalPriorities.put(current, current.getPriority());

        // Proactive Step: Immediately push the thread to priority 10.
        // This ensures a low task cannot be preempted by a medium task while holding the lock.
        System.out.println(
                ">>> PRIORITY CEILING APPLIED: "
                        + current.getName()
                        + " -> "
                        + CEILING_PRIORITY);

        current.setPriority(CEILING_PRIORITY);

        // Record successful acquisition timestamp for the safety metric evaluation
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordAcquire();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + current.getName()
                + " ACQUIRED MotorController.");
    }

    public synchronized void unlock(TaskType type) {
        Thread current = Thread.currentThread();

        // Record the completion timestamp if this is the safety task releasing the hardware
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordFinish();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + current.getName()
                + " RELEASED MotorController.");

        // Pull the archived priority back from our map and drop the thread to its base level
        int oldPriority = originalPriorities.get(current);
        current.setPriority(oldPriority);

        System.out.println(
                ">>> PRIORITY RESTORED: "
                        + current.getName()
                        + " -> "
                        + oldPriority);

        // Clean up the map registry entry for this task session
        originalPriorities.remove(current);

        // Reset resource tokens and wake up all waiting threads
        owner = null;
        locked = false;

        notifyAll();
    }
}