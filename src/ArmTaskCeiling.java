/**
 * Real-time worker thread configured to simulate robotic arm operations under 
 * the Priority Ceiling Protocol (PCP). Bypasses dynamic inversion vulnerabilities 
 * by enforcing immediate priority shifts inside the shared resource layer.
 */
public class ArmTaskCeiling implements Runnable {

    private final String name;
    private final TaskType type;
    private final PriorityCeilingResource resource;
    private final int workTime;
    private final boolean needsResource;

    public ArmTaskCeiling(String name,
                          TaskType type,
                          PriorityCeilingResource resource,
                          int workTime,
                          boolean needsResource) {

        this.name = name;
        this.type = type;
        this.resource = resource;
        this.workTime = workTime;
        this.needsResource = needsResource;
    }

    @Override
    public void run() {
        // Enforce systemic naming rules over generic JRE thread IDs.
        // Crucial for trace metrics parsing and verification of protocol execution timelines.
        Thread.currentThread().setName(name);

        System.out.println("[" + System.currentTimeMillis() + "] " + name + " STARTED");

        if (needsResource) {
            // Task requires hardware resource access. Invoking the Priority Ceiling monitor lock.
            // Priority is proactively driven to the ceiling (Priority 10) inside this block to prevent preemption.
            resource.lock(type);

            try {
                // Safely simulate the duration of hardware resource usage once the ceiling is applied.
                Thread.sleep(workTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Relinquish resource control and restore the thread's native execution priority.
                resource.unlock(type);
            }

        } else {
            // Simulates localized task operations independent of the critical resource.
            // Uses a busy-waiting computation loop to ensure the thread actively claims its 
            // scheduler priority and core allocation without involuntarily yielding.
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < workTime) {
                // Simulate intensive processing workload duration by consuming CPU cycles.
            }
        }

        System.out.println("[" + System.currentTimeMillis() + "] " + name + " FINISHED");
    }
}