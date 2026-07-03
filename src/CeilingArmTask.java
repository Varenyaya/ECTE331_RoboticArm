public class CeilingArmTask implements Runnable {
    private final String name;
    private final PriorityCeilingResource resource;
    private final int workDuration;
    private final boolean needsResource;

    public CeilingArmTask(String name, PriorityCeilingResource resource, int workDuration, boolean needsResource) {
        this.name = name;
        this.resource = resource;
        this.workDuration = workDuration;
        this.needsResource = needsResource;
    }

    @Override
    public void run() {
        System.out.println("[" + System.currentTimeMillis() + "] " + name + " started execution.");
        
        if (needsResource) {
            resource.lock();
            try {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < workDuration) {
                    // Burn CPU cycles to simulate work
                }
            } finally {
                resource.unlock();
            }
        } else {
            // Emulate real-time kernel priority ceiling preemption check
            while (PriorityCeilingResource.isCeilingProtocolRunning()) {
                try {
                    Thread.sleep(50); // Pause to let the ceiling task finish execution
                } catch (InterruptedException e) {
                    System.out.println(name + " interrupted.");
                }
            }
            
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < workDuration) {
                // Burn CPU cycles to simulate work
            }
        }
        
        System.out.println("[" + System.currentTimeMillis() + "] " + name + " finished execution.");
    }
}