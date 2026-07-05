/**
 * Automated benchmark suite executing all three configurations sequentially
 * for a balanced, side-by-side empirical performance comparison.
 */
public class SimulationComparison {

    public static void main(String[] args) {
        System.out.println("\n========= STARTING INTEGRATED COMPARISON COMPILATION =========");

        System.out.println("\nExecuting Baseline Stream...");
        SimulationBaseline.main(null);
        
        System.out.println("\nExecuting Inheritance Stream...");
        SimulationInheritance.main(null);
        
        System.out.println("\nExecuting Ceiling Stream...");
        SimulationCeiling.main(null);

        System.out.println("\n========= FINAL STRATEGY SUMMARY COMPONENT =========");
        System.out.printf("Baseline Average Waiting    : %.2f ms | Response: %.2f ms%n", 
                SimulationBaseline.getAvgWaiting(), SimulationBaseline.getAvgResponse());
        System.out.printf("Inheritance Average Waiting : %.2f ms | Response: %.2f ms%n", 
                SimulationInheritance.getAvgWaiting(), SimulationInheritance.getAvgResponse());
        System.out.printf("Ceiling Average Waiting     : %.2f ms | Response: %.2f ms%n", 
                SimulationCeiling.getAvgWaiting(), SimulationCeiling.getAvgResponse());
        System.out.println("=====================================================");
    }
}