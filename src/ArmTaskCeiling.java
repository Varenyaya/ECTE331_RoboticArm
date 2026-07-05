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
                // Incorporate dynamic operational jitter (±100ms) to simulate structural hardware latency variance.
                int jitter = (int)(Math.random() * 200 - 100);
                Thread.sleep(Math.max(0, workTime + jitter));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Relinquish resource control and restore the thread's native execution priority.
                resource.unlock(type);
            }

        } else {
            // Simulates localized task operations independent of the critical resource.
            // Employs a hybrid spinning loop to accurately reflect real operating system background preemption.
            long start = System.currentTimeMillis();
            int spin = 0;

            while (System.currentTimeMillis() - start < workTime) {
                spin++;
                // Periodically yield execution control slices to match baseline preemption jitter
                if (spin % 10000 == 0) {
                    Thread.yield();
                }
            }
            
            System.out.println("[" + System.currentTimeMillis() + "] " + name + " completed independent work.");
        }

        System.out.println("[" + System.currentTimeMillis() + "] " + name + " FINISHED");
    }
}