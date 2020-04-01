/**
 * 
 */
package uk.ac.imperial.isst.metric.solvers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.ProblemSpecification;
import uk.ac.imperial.isst.metric.SecurityMetric;
import uk.ac.imperial.isst.metric.cnf.TseitinStructure;
import uk.ac.imperial.isst.metric.config.ConfigKeys;
import uk.ac.imperial.isst.metric.config.ToolConfig;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OptimSolver {    
    
	public static String SOLVER_ID = "OPTIM-LP-SOLVER";
    private String pythonSolverPath;   
    private String pythonPath; 
    
    public OptimSolver(ToolConfig config) {
		super();
		this.pythonSolverPath = config.getProperties().getProperty(ConfigKeys.pythonSolverPathKey);
		this.pythonPath = config.getProperties().getProperty(ConfigKeys.pythonPathKey);
	}
    
    public SecurityMetric solve(ProblemSpecification problemSpec, Map<String,Object> stats, TseitinStructure ts) throws Exception {
    	String tempFile = ".optim-input.tmp";
		FileOutputStream pOutputFileStream = new FileOutputStream(new File(tempFile));    		
		    		
		ts.toStream(pOutputFileStream);	

		OptimSolution optimSolution = this.runSolver(tempFile);
		if (CpsMetricAnalyser.DEBUG) {
			System.out.println(optimSolution);
		}
    	return this.transformOptimSolution(optimSolution, problemSpec, ts);	
    }

	private OptimSolution runSolver(String inputFile) {
		if (CpsMetricAnalyser.DEBUG) {
			System.out.println("[*] Optim solver started! " + new Timestamp(System.currentTimeMillis()));
		}
    	
		File pythonScript = new File(this.pythonSolverPath); 
		
		String s = null;
        try {
        	ProcessBuilder pb = new ProcessBuilder(pythonPath, pythonScript.getAbsolutePath(), inputFile);  
        	Process p = pb.start();
        	
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command            
            StringBuilder inBuilder = new StringBuilder();
            while ((s = stdInput.readLine()) != null) {
            	inBuilder.append(s);
                //System.out.println(s);
            }
            //System.out.println("OUT:");
            String pythonOutput = inBuilder.toString();
            //System.out.println("Python output:"); System.out.println(pythonOutput);
            
            ObjectMapper mapper = new ObjectMapper();                       			            
            OptimSolution optimSolution = mapper.readValue(pythonOutput, OptimSolution.class);	            
            //System.out.println(optimSolution);
            
            // read any errors from the attempted command            
            StringBuilder errBuilder = new StringBuilder();
            while ((s = stdError.readLine()) != null) {
            	errBuilder.append(s);
                //System.out.println(s);
            }
            //System.out.println("ERR:");
            if (!errBuilder.toString().isEmpty()) {
            	System.out.println(errBuilder.toString());
            }            
            return optimSolution;
        }
        catch (IOException e) {
            System.out.println("Exception:");
            e.printStackTrace();
            
            OptimSolution err = new OptimSolution();
            err.setStatus(e.getMessage());
            return err;
        }
    }
	
	private SecurityMetric transformOptimSolution(OptimSolution optimSolution, ProblemSpecification problemSpec, TseitinStructure ts) {		
		SecurityMetric m = new SecurityMetric(problemSpec);
		m.setSolverId(OptimSolver.SOLVER_ID);
		m.setCost(optimSolution.getCost());
			
		Map<Integer, Object> varNameMap = ts.getTseitinVisitor().getVarNameMap();
		
		Map<String,Double> optimSol = optimSolution.getSolution();
		for (Entry<String,Double> e : optimSol.entrySet()) {
			String literalIndex = e.getKey().substring(2);
			Integer index = Integer.parseInt(literalIndex);
			String nodeName = String.valueOf(varNameMap.get(index));
			//long value = new Double(e.getValue()).longValue();
			//System.out.println("index: " + index + ", nodeName=" + nodeName + ", value=" + value);
			
			AndOrNode node = problemSpec.getGraph().getNode(nodeName);
			if (node != null) {
				m.getNodes().add(node);
			} else {
				// Workaround 2019-03-12
				// The security metric should consider AndOrNodes and Measure instances separately
				Measure measure = problemSpec.getMeasureByInstanceId().get(nodeName);
				m.getNodes().add(new AndOrNode(nodeName, measure.getId(), measure.getCost()));
				m.getMeasureByInstanceIdMap().put(nodeName, measure);
			}
		}
		
		return m;
	}
}
