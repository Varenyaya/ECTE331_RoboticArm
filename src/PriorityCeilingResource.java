public class PriorityCeilingResource {
    private boolean isLocked = false;
    private Thread holdingThread = null;
    private final int ceilingPriority = Thread.MAX_PRIORITY; // Priority 10
    private int originalPriority = Thread.NORM_PRIORITY;
    private static boolean isCeilingActive = false; // Flag for real-time emulation

    public static boolean isCeilingProtocolRunning() {
        return isCeilingActive;
    }

    public synchronized void lock() {
        Thread currentThread = Thread.currentThread();
        
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(currentThread.getName() + " interrupted.");
            }
        }
        
        isLocked = true;
        holdingThread = currentThread;
        originalPriority = currentThread.getPriority();
        isCeilingActive = true; // Raise the global ceiling flag
        
        System.out.println(">>> [CEILING] Immediately boosting " + holdingThread.getName() + " to Ceiling Priority: " + ceilingPriority);
        holdingThread.setPriority(ceilingPriority);
        
        System.out.println("[" + System.currentTimeMillis() + "] " + holdingThread.getName() + " ACQUIRED resource under Ceiling.");
    }

    public synchronized void unlock() {
        Thread currentThread = Thread.currentThread();
        if (holdingThread == currentThread) {
            System.out.println("[" + System.currentTimeMillis() + "] " + currentThread.getName() + " RELEASED resource.");
            
            System.out.println(">>> [RESTORE] Resetting " + currentThread.getName() + " priority back to original: " + originalPriority);
            currentThread.setPriority(originalPriority);
            
            isCeilingActive = false; // Lower the global ceiling flag
            isLocked = false;
            holdingThread = null;
            notifyAll();
        }
    }
}