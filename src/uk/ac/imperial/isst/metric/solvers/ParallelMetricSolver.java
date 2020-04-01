/**
 * 
 */
package uk.ac.imperial.isst.metric.solvers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.ProblemSpecification;
import uk.ac.imperial.isst.metric.SecurityMetric;
import uk.ac.imperial.isst.metric.cnf.TseitinStructure;
import uk.ac.imperial.isst.metric.config.ToolConfig;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class ParallelMetricSolver {
	
	public ParallelMetricSolver() {		
	}
	
	public SecurityMetric solve(ProblemSpecification problemSpec, Map<String,Object> stats, TseitinStructure ts, ToolConfig config) throws InterruptedException, Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		Callable<SecurityMetric> maxSatSolver = null;
		if (CpsMetricAnalyser.USE_MAX_SAT) {
	        maxSatSolver = () -> {	            
	        	Sat4jSolver solver = new Sat4jSolver();
	        	SecurityMetric m = solver.solve(problemSpec, stats, ts);
	        	if (CpsMetricAnalyser.DEBUG) {
	        		System.out.println("[*] MAX-SAT solver finished! " + new Timestamp(System.currentTimeMillis()));
	        	}	        	
	        	return m;            
	        };
		}

        Callable<SecurityMetric> optimSolver = null;
        if (CpsMetricAnalyser.USE_OPTIM) {
	        optimSolver = () -> {	        	
	        	SecurityMetric m = new OptimSolver(config).solve(problemSpec, stats, ts);	    		
	        	if (CpsMetricAnalyser.DEBUG) {
	        		System.out.println("[*] Optim solver finished! " + new Timestamp(System.currentTimeMillis()));
	        	}	        	
	        	return m;
	        };
        }
        
        SecurityMetric metric = null;
        List<Callable<SecurityMetric>> solvers = new ArrayList<Callable<SecurityMetric>>();
        List<String> solverNames = new ArrayList<String>();
		try {
			if (CpsMetricAnalyser.USE_MAX_SAT) {
				solvers.add(maxSatSolver);
				solverNames.add("MaxSAT");
			} 
			if (CpsMetricAnalyser.USE_OPTIM) {
				solvers.add(optimSolver);
				solverNames.add("OptimLP");				
			}
			System.out.println("|+| Solvers: " + solverNames);
			if (solvers.isEmpty()) {
				throw new Exception("ERROR: no solver has been enabled");
			}
			metric = executorService.invokeAny(solvers);
			if (CpsMetricAnalyser.DEBUG) {
				System.out.println("Executor result => metric: " + metric.getSolverId() + " - " + new Timestamp(System.currentTimeMillis()));
			}
		} catch (ExecutionException e) {			
			throw e;
		} catch (Exception e) {			
			throw e;
		}        
        
		executorService.shutdown();
		try {
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {		    		    	
		    	executorService.shutdownNow();		    	
		    } 
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
        		
        return metric;
	}	
}
