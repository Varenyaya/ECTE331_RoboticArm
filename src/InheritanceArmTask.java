public class InheritanceArmTask implements Runnable {
    private final String name;
    private final PriorityInheritanceResource resource;
    private final int workDuration;
    private final boolean needsResource;

    public InheritanceArmTask(String name, PriorityInheritanceResource resource, int workDuration, boolean needsResource) {
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
            // Simulated Real-Time Kernel Preemption Check for Medium Task
            while (PriorityInheritanceResource.isInheritanceActive()) {
                try {
                    // Force the medium task to wait for the high-priority sequence to clear
                    Thread.sleep(50); 
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