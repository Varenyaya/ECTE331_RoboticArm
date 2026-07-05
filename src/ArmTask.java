/**
 * Core thread worker representing a functional task within the robotic arm system.
 * Simulates either a critical hardware resource access pattern or independent, 
 * computationally heavy background work.
 */
public class ArmTask implements Runnable {

    private final String name;
    private final TaskType type;
    private final MotorController controller;
    private final int workTime;
    private final boolean needsResource;

    public ArmTask(String name,
                   TaskType type,
                   MotorController controller,
                   int workTime,
                   boolean needsResource) {

        this.name = name;
        this.type = type;
        this.controller = controller;
        this.workTime = workTime;
        this.needsResource = needsResource;
    }

    @Override
    public void run() {
        // Enforce meaningful string labels over internal JRE thread identifiers (e.g., Thread-0)
        // This ensures system log timelines map directly back to our architecture components.
        Thread.currentThread().setName(name);

        System.out.println("[" + System.currentTimeMillis()
                + "] "
                + name
                + " STARTED");

        if (needsResource) {
            // Task requires hardware synchronization. Attempting entry into the MotorController critical section.
            // FIXED: Passing the 'type' enum token to support automated high-precision baseline benchmarking.
            controller.accessResource(name, workTime, type);

        } else {
            // Task executes independent operations (e.g., Motion Planner path-finding algorithms).
            // Employs a hybrid, scheduler-aware spinning loop to accurately match real preemption jitter.
            long start = System.currentTimeMillis();
            int spin = 0;

            while (System.currentTimeMillis() - start < workTime) {
                spin++;
                // Periodically yield the CPU to simulate realistic operating system background noise
                if (spin % 10000 == 0) {
                    Thread.yield();
                }
            }

            System.out.println("[" + System.currentTimeMillis()
                    + "] "
                    + name
                    + " completed independent work.");
        }

        System.out.println("[" + System.currentTimeMillis()
                + "] "
                + name
                + " FINISHED");
    }
}