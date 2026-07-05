/**
 * Tracks and calculates real-time performance data for task execution.
 * Captures high-precision nanosecond timestamps to evaluate pure thread 
 * waiting latencies and total response intervals with engineering-grade precision.
 */
public class Metrics {
    private long requestTime;
    private long acquireTime;
    private long finishTime;

    public void recordRequest() {
        this.requestTime = System.nanoTime();
    }

    public void recordAcquire() {
        this.acquireTime = System.nanoTime();
    }

    public void recordFinish() {
        this.finishTime = System.nanoTime();
    }

    /**
     * Calculates pure resource contention delay in milliseconds.
     * Evaluates the delta between the initial request point and the final acquisition frame.
     *
     * @return pure waiting time in milliseconds with decimal precision.
     */
    public double getWaitingTime() {
        return (acquireTime - requestTime) / 1000000.0;
    }

    /**
     * Calculates total operation turnaround frame metrics in milliseconds.
     * Evaluates the complete lifespan from initial execution request to final lock release.
     *
     * @return total response time in milliseconds with decimal precision.
     */
    public double getResponseTime() {
        return (finishTime - requestTime) / 1000000.0;
    }
}