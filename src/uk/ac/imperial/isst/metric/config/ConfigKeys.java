package uk.ac.imperial.isst.metric.config;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public interface ConfigKeys {

	// Solvers
	public static final String useSat4jKey = "solvers.sat4j";
	public static final String useOptimKey = "solvers.optim";
	
	// Python environment
	public static final String pythonPathKey = "python.path";
	public static final String pythonSolverPathKey = "python.solver.path";
	
	// Output
	public static final String outputSolKey = "output.sol";
	public static final String outputWcnfKey = "output.wcnf";	
	public static final String outputTxtKey = "output.txt";

	// Output folders
	public static final String outputFolder = "folders.output";
	public static final String outputViewFolder = "folders.view";	
		
	// Debug
	public static final String basicDebugKey = "tool.debug";
	public static final String fullDebugKey = "tool.fulldebug";
	
	//Weights as probabilities
	public static final String weightsAsProbabilities = "weights.probabilities";
	
}
