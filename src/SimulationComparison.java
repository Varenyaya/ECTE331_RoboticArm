/**
 * Main automated benchmark suite for the Real-Time Robotic Arm Controller.
 * Runs the Baseline, Priority Inheritance, and Priority Ceiling simulations 
 * sequentially to generate a complete, comparative performance dataset 
 * for the final evaluation report.
 */
public class SimulationComparison {

    public static void main(String[] args) {

        System.out.println("\n========= FINAL COMPARISON =========\n");

        // Step 1: Execute the unmanaged baseline to record priority inversion behavior
        runBaseline();
        
        // Step 2: Execute the 10-run priority inheritance protocol simulation block
        runInheritance();
        
        // Step 3: Execute the 10-run priority ceiling protocol simulation block
        runCeiling();
    }

    /**
     * Executes the baseline unmanaged simulation.
     * Demonstrates the base scheduling anomaly where a mid-priority thread 
     * inadvertently delays a critical safety thread.
     */
    private static void runBaseline() {
        System.out.println("\n--- BASELINE ---");
        SimulationBaseline.main(null);
    }

    /**
     * Invokes the multi-run Priority Inheritance experiment harness.
     * Extracts dynamic priority boosting logs and compiles average task latencies.
     */
    private static void runInheritance() {
        System.out.println("\n--- INHERITANCE ---");
        SimulationInheritance.main(null);
    }

    /**
     * Invokes the multi-run Priority Ceiling experiment harness.
     * Extracts proactive priority shielding logs and compiles average task latencies.
     */
    private static void runCeiling() {
        System.out.println("\n--- CEILING ---");
        SimulationCeiling.main(null);
    }
}