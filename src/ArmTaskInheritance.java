/**
 * Task worker class configured to simulate robotic arm operations under 
 * the Priority Inheritance Protocol (PIP). Designed to test how dynamic 
 * priority boosting prevents middle-priority tasks from stalling the system.
 */
public class ArmTaskInheritance implements Runnable {

    private final String name;
    private final TaskType type;
    private final PriorityInheritanceResource resource;
    private final int workTime;
    private final boolean needsResource;

    public ArmTaskInheritance(String name,
                              TaskType type,
                              PriorityInheritanceResource resource,
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
        // Replace default JRE thread IDs with our descriptive task names.
        // Vital for reading the console logs and tracking execution order.
        Thread.currentThread().setName(name);

        System.out.println("[" + System.currentTimeMillis() + "] " + name + " STARTED");

        if (needsResource) {
            // Task needs hardware access. Requesting entry through the inheritance lock monitor.
            // If another thread blocks us here, the lock owner's priority will be boosted.
            resource.lock(type);

            try {
                // Incorporate dynamic operational jitter (±100ms) to simulate structural hardware latency variance.
                int jitter = (int)(Math.random() * 200 - 100);
                Thread.sleep(Math.max(0, workTime + jitter));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Free the resource lock and drop the thread back to its original base priority.
                resource.unlock(type);
            }

        } else {
            // Simulates independent calculations (like path-finding) outside the critical resource.
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