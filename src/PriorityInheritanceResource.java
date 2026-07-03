public class PriorityInheritanceResource {
    private boolean isLocked = false;
    private Thread holdingThread = null;
    private int originalPriority = Thread.NORM_PRIORITY;
    private static boolean isBoosted = false; // Flag to tell Med threads to back off

    public static boolean isInheritanceActive() {
        return isBoosted;
    }

    public synchronized void lock() {
        Thread currentThread = Thread.currentThread();
        
        while (isLocked) {
            System.out.println("[" + System.currentTimeMillis() + "] " + currentThread.getName() + " is BLOCKED by " + holdingThread.getName());
            
            if (currentThread.getPriority() > holdingThread.getPriority()) {
                originalPriority = holdingThread.getPriority();
                System.out.println(">>> [INHERITANCE] Boosting " + holdingThread.getName() + " priority from " + originalPriority + " to " + currentThread.getPriority());
                holdingThread.setPriority(currentThread.getPriority());
                isBoosted = true; // Block lower priority tasks from executing
            }
            
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(currentThread.getName() + " interrupted.");
            }
        }
        
        isLocked = true;
        holdingThread = currentThread;
        System.out.println("[" + System.currentTimeMillis() + "] " + holdingThread.getName() + " ACQUIRED resource.");
    }

    public synchronized void unlock() {
        Thread currentThread = Thread.currentThread();
        if (holdingThread == currentThread) {
            System.out.println("[" + System.currentTimeMillis() + "] " + currentThread.getName() + " RELEASED resource.");
            
            if (currentThread.getPriority() != originalPriority) {
                System.out.println(">>> [RESTORE] Resetting " + currentThread.getName() + " priority back to " + originalPriority);
                currentThread.setPriority(originalPriority);
            }
            
            isBoosted = false; // Clear flag
            isLocked = false;
            holdingThread = null;
            notifyAll();
        }
    }
}