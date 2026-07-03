
public class ArmTask implements Runnable {
    private final String name;
    private final MotorController resource;
    private final int workDuration;
    private final boolean needsResource;

    public ArmTask(String name, MotorController resource, int workDuration, boolean needsResource) {
        this.name = name;
        this.resource = resource;
        this.workDuration = workDuration;
        this.needsResource = needsResource;
    }

    @Override
    public void run() {
        System.out.println("[" + System.currentTimeMillis() + "] " + name + " started execution.");
        if (needsResource) {
            resource.accessResource(name, workDuration);
        } else {
            try {
                Thread.sleep(workDuration);
            } catch (InterruptedException e) {
                System.out.println(name + " interrupted.");
            }
        }
        System.out.println("[" + System.currentTimeMillis() + "] " + name + " finished execution.");
    }
}