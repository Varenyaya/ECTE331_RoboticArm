/**
 * Tracks and calculates real-time performance data for task execution.
 * Captures request, acquisition, and completion timestamps to calculate 
 * pure thread waiting latencies and total response intervals.
 */
public class Metrics {

    private long requestTime;
    private long acquireTime;
    private long finishTime;

    // Captures the exact moment a task attempts to request the shared resource lock.
    public void recordRequest() {
        requestTime = System.currentTimeMillis();
    }

    // Captures the exact moment the task successfully bypasses the monitor queue 
    // and secures exclusive lock control over the resource.
    public void recordAcquire() {
        acquireTime = System.currentTimeMillis();
    }

    // Captures the final timestamp when the task completes its operation 
    // and officially yields the resource lock back to the system pool.
    public void recordFinish() {
        finishTime = System.currentTimeMillis();
    }

    // Calculates the pure time spent blocked in the queue.
    // Measures the delay between requesting the resource and actually acquiring it.
    public long getWaitingTime() {
        return acquireTime - requestTime;
    }

    // Calculates the total turnaround time for the critical operation.
    // Tracks the end-to-end window from the initial lock request to final execution finish.
    public long getResponseTime() {
        return finishTime - requestTime;
    }
}