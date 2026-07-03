import java.util.HashMap;
import java.util.Map;

/**
 * Shared resource monitor implementing the Priority Inheritance Protocol (PIP).
 * Dynamically boosts a lower-priority task's execution priority if a higher-priority
 * task gets blocked trying to acquire the same resource.
 */
public class PriorityInheritanceResource {

    private boolean locked = false;
    private Thread owner = null;

    // Stores the baseline priority of threads before they get boosted, allowing a clean reset
    private final Map<Thread, Integer> originalPriorities = new HashMap<>();

    private final Metrics metrics = new Metrics();

    public Metrics getMetrics() {
        return metrics;
    }

    public synchronized void lock(TaskType type) {
        Thread current = Thread.currentThread();

        // Start tracking the wait interval if this is the critical safety task
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordRequest();
        }

        // Check if resource is available. If occupied, handle potential inversion loops.
        while (locked) {

            System.out.println("[" + System.currentTimeMillis() + "] "
                    + current.getName()
                    + " BLOCKED by "
                    + owner.getName());

            // Protocol Check: If our priority is higher than the thread currently holding the lock,
            // we must pass our priority level down to that holder so it runs faster.
            if (current.getPriority() > owner.getPriority()) {

                // Backup the current holder's original priority if we haven't already
                if (!originalPriorities.containsKey(owner)) {
                    originalPriorities.put(owner, owner.getPriority());
                }

                // Dynamic Boost: Raise lock owner to the higher competing task's priority level.
                // This prevents middle-priority tasks from preempting it on the core.
                System.out.println(
                        ">>> PRIORITY INHERITANCE: "
                                + owner.getName()
                                + " boosted from "
                                + owner.getPriority()
                                + " to "
                                + current.getPriority());

                owner.setPriority(current.getPriority());
            }

            try {
                // Suspend current thread and wait for a release signal
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Successfully cleared the block loop; claim resource control
        locked = true;
        owner = current;

        // Log the exact moment the safety monitor takes control to stop the benchmark timer
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordAcquire();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + current.getName()
                + " ACQUIRED MotorController.");
    }

    public synchronized void unlock(TaskType type) {
        Thread current = Thread.currentThread();

        // Log the final turnaround point if this is the safety task yielding the hardware
        if (type == TaskType.SAFETY_MONITOR) {
            metrics.recordFinish();
        }

        System.out.println("[" + System.currentTimeMillis() + "] "
                + current.getName()
                + " RELEASED MotorController.");

        // Priority Restoration Path: Revert the releasing thread if it was boosted during this lock cycle
        if (originalPriorities.containsKey(current)) {

            int oldPriority = originalPriorities.get(current);
            current.setPriority(oldPriority);

            System.out.println(
                    ">>> PRIORITY RESTORED: "
                            + current.getName()
                            + " -> "
                            + oldPriority);

            // Evict thread from our active registry tracking map
            originalPriorities.remove(current);
        }

        // Reset resource variables and wake up any threads waiting on this resource monitor
        owner = null;
        locked = false;

        notifyAll();
    }
}