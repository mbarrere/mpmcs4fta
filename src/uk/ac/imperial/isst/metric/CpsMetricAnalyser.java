/**
 *
 */
package uk.ac.imperial.isst.metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.ac.imperial.isst.metric.cnf.TseitinStructure;
import uk.ac.imperial.isst.metric.config.ConfigKeys;
import uk.ac.imperial.isst.metric.config.ToolConfig;
import uk.ac.imperial.isst.metric.model.AndOrGraph;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;
import uk.ac.imperial.isst.metric.solvers.ParallelMetricSolver;
import uk.ac.imperial.isst.metric.util.GraphUtils;
import uk.ac.imperial.isst.metric.util.JSONReader;
import uk.ac.imperial.isst.metric.util.JSONWriter;


/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class CpsMetricAnalyser {
	
	public static boolean USE_MAX_SAT = true;
	public static boolean USE_OPTIM = false;		
	public static boolean DEBUG = false;
	public static boolean FULL_DEBUG = false;
	public static boolean OUTPUT_WCNF = false;
	public static boolean OUTPUT_SOL = true;	
	public static boolean OUTPUT_TXT = false;
	public static boolean WEIGHTS_AS_PROBABILITIES = true;
	public static boolean MSCS = true;
	public static final int DECIMAL_PLACES = 7;
	
	public static String OUTPUT_FOLDER = "output";
	public static String VIEW_FOLDER = "view";	
	
	
	public static final int NANOS_IN_MS = 1000000;	
	public static final String TOOL_NAME = "MPMCS4FTA";
	public static final String CONFIG_FILE = "mpmcs4fta.conf";	
	public static boolean ASSIGNMENT_DEBUG = false; //internal - testing
	
	public static String helpMessage() {	
		return "Usage: java -jar " + TOOL_NAME.toLowerCase() + ".jar inputFile.json [-c configFile]";
	}
	
	public static void main(String[] args) {		
		String version = "v0.76.1";
		try {
			System.out.println("== " + TOOL_NAME + " " + version + " ==");		
			System.out.println("== Started at " + new Timestamp(System.currentTimeMillis()) + " ==");
			System.out.println("");
			
			String filename = null;
						
			if (args.length == 0) {
				System.err.println("[ERROR] Please indicate the JSON specification file");
				System.err.println(helpMessage());
			} else {
				filename = args[0];
				
				ToolConfig config = new ToolConfig(); 
				config.loadDefaultSetings();  
				config.tryLoadDefaultFromFile(CONFIG_FILE);
				//config.printProperties();				
				setupTool(config);				
				
				if (args.length == 3) {
					if ("-c".equals(args[1])) {
						String configFilePath = args[2];										
						System.out.print("=> Loading custom configuration file '" + configFilePath + "'... ");
						try { 
							config.loadConfigFromFile(configFilePath);
							//config.printProperties();
							setupTool(config);
							System.out.println("done.");
						} catch (IOException e) {
							System.err.println("\n[WARN] Configuration file '" + configFilePath + "' not found => using default settings.");
						}						
					} else {
						throw new Exception("[ERROR] Unkown parameter: '" + args[1] + "'\n" + helpMessage());
					}
				} else {
					if (args.length > 1) {
						throw new Exception("[ERROR] Wrong combination of parameters\n" + helpMessage());
					}
				}
								
				System.out.print("=> Loading problem specification... ");
				
				long start = System.currentTimeMillis(); 				
				ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);
				long loadTime = System.currentTimeMillis() - start;
				System.out.println(" done in " + loadTime + " ms (" + ((loadTime+500)/1000) + " seconds).");
								
				//analyseGraph(problem.getGraph());				
				solveWithTseitinAndDisplaySolution(problem, config);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("== " + TOOL_NAME + " ended at " + new Timestamp(System.currentTimeMillis()) + " ==");
		System.exit(0);
	}
	
	public static void setupTool(ToolConfig config) {
		try {			
			CpsMetricAnalyser.USE_MAX_SAT = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.useSat4jKey));
			CpsMetricAnalyser.USE_OPTIM = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.useOptimKey));
			CpsMetricAnalyser.DEBUG = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.basicDebugKey));
			CpsMetricAnalyser.FULL_DEBUG = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.fullDebugKey));
			CpsMetricAnalyser.OUTPUT_WCNF = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.outputWcnfKey));
			CpsMetricAnalyser.OUTPUT_SOL = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.outputSolKey));
			CpsMetricAnalyser.OUTPUT_TXT = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.outputTxtKey));
			
			CpsMetricAnalyser.OUTPUT_FOLDER = config.getProperties().getProperty(ConfigKeys.outputFolder);
			CpsMetricAnalyser.VIEW_FOLDER = config.getProperties().getProperty(ConfigKeys.outputViewFolder);			
			
			CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES = Boolean.valueOf(config.getProperties().getProperty(ConfigKeys.weightsAsProbabilities));
			
		} catch (Exception e) {
			System.err.println("Error in configuration file => ending " + TOOL_NAME + " now.");
			System.exit(0);
		}				
	}
	
	public static ProblemSolution solveWithTseitinAndDisplaySolution(ProblemSpecification problem, ToolConfig config) throws Exception {

		Map<String,Object> stats = analyseGraph(problem.getGraph());

		System.out.println("----------------------------------");
		System.out.println("Problem source: " + problem.getSource());
		System.out.println("Problem target: " + problem.getTarget());
		if (problem.getMeasures() != null && !problem.getMeasures().isEmpty()) {
			System.out.println("Available measures: ");
			for (Measure measure : problem.getMeasures()) {
				System.out.println("\t" + measure);
			}
		}				
		System.out.println("----------------------------------");

		System.out.print("=> Performing Tseitin transformation... ");
		long start = System.currentTimeMillis();
		TseitinStructure ts = TseitinTransformer.transformGraphWithTseitin(problem, stats);
		long tsTime = System.currentTimeMillis() - start;
		System.out.println(" done in " + tsTime + " ms (" + ((tsTime+500)/1000) + " seconds).");
		
		long metricStart = System.currentTimeMillis();
		SecurityMetric m = null;
		try {
			// Optim solver requires further work when dealing with multiple measures
			if (problem.involvesMultipleMeasures()) {
				CpsMetricAnalyser.USE_OPTIM = false;
			}
			m = new ParallelMetricSolver().solve(problem, stats, ts, config);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("== " + TOOL_NAME + " ended at " + new Timestamp(System.currentTimeMillis()) + " ==");
			System.exit(0);
		}
		long metricTime = System.currentTimeMillis() - metricStart;

		System.out.println("\n==================================");		
		System.out.println("=> BEST solution found by " + m.getSolverId() + " for:");
		System.out.println("Source: " + problem.getSource());
		System.out.println("Target: " + problem.getTarget());
		
		//updateNodeLabelsWithMeasureValues(m, problem);
		
		/* ** MAX_PROBS ** */
 		//if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
		if (CpsMetricAnalyser.MSCS) {
 			updateProblemSpecAndMetric(m,problem);
 			//System.out.println("Joint probability of failure: " + GraphUtils.round(Math.exp(-1 * cost), CpsMetricAnalyser.DECIMAL_PLACES));
 			//System.out.println("CUT cost: " + cost);
 		}
 		
		m.display();
			
		ts.setSecurityMetric(m);
		ts.setExecutionTime(metricTime);
		
		System.out.println("[*] Metric computation time: " + metricTime + " ms (" + ((metricTime+500)/1000) + " seconds).");
		System.out.println("==================================");
		
		ProblemSolution sol = new ProblemSolution(problem, m);
		
		if (OUTPUT_SOL) {				
			outputSolution(ts, problem, m, CpsMetricAnalyser.VIEW_FOLDER, sol);
		}
		
		if (OUTPUT_WCNF) {		
			outputWCNF(ts, problem, m);
		}
		
		if (OUTPUT_TXT) {							
			outputTxt(ts, problem, m);			
		}
		return sol;
	}
	
	public static void updateProblemSpecAndMetric(SecurityMetric metric, ProblemSpecification problem) {
		/* ** MSCS ** */		
		metric.setCost(GraphUtils.round(Math.exp(-1 * metric.getCost()), CpsMetricAnalyser.DECIMAL_PLACES));
 		
		for (AndOrNode node : problem.getGraph().getNodes()) {
			if (node.isAtomicType()) {	 			
				if (node.isGoalOrFunction() || node.isInitType()) { 					
 					node.setValue("none"); 					
 				} else {
 					node.setValue(node.getOriginalValue());
 				}
	 		} else {
	 			if (node.isAndType()){		 		
	 				node.setType("or");
	 			} else {
		 			if (node.isOrType()){		 		
		 				node.setType("and");
		 			}
	 			}
	 		}
			
		}
		
	}
	
	public static void updateProblemSpecAndMetricOld(SecurityMetric metric, ProblemSpecification problem) {
		/* ** MAX_PROBS ** */		
		metric.setCost(GraphUtils.round(Math.exp(-1 * metric.getCost()), CpsMetricAnalyser.DECIMAL_PLACES));
 		
		for (AndOrNode node : problem.getGraph().getNodes()) {
			if (node.isAtomicType()) {
	 			String value = node.getValue();
	 			Double d = 0.0;
	 			if ("inf".equalsIgnoreCase(value)) {
					d =  Double.MAX_VALUE;
	 				//d = 0.0;
				} else {					
					d =  Double.parseDouble(value);					
				}
	 			if (CpsMetricAnalyser.DEBUG) {
	 				System.out.println("Original value for node " + node.getId() + ": "  + d);
	 			}			 				 				 			
	 			//System.out.println("Original value for node " + node.getId() + ": "  + d);
	 									
				Double probValue = GraphUtils.round(Math.exp(-1 * d), CpsMetricAnalyser.DECIMAL_PLACES); 
	 			
	 			if (probValue.isInfinite()) {
	 				if (node.isGoalOrFunction() || node.isInitType()) {
	 					//node.setValue("0");
	 					node.setValue("none");
	 					//node.setLabel("+");
	 					//node.setLabel("none");
	 				}
	 				else {
	 					node.setValue("0");
	 					//node.setLabel("0");
	 					//node.setLabel("none");
	 				}
	 				
	 			} else {
	 				if (node.isGoalOrFunction() || node.isInitType()) {
	 					//node.setValue("0");
	 					node.setValue("none");
	 					//node.setLabel("-");
	 					//node.setLabel("none");
	 				}
	 				else {
		 				//DecimalFormat df = new DecimalFormat("#.######");		 						 			
			 			//node.setValue(df.format(probValue));
			 			//node.setLabel(df.format(probValue));
			 			node.setValue(probValue.toString());
			 			//node.setLabel(probValue.toString());
	 				}
	 			}		 			
	 			if (CpsMetricAnalyser.DEBUG) {
	 				System.out.println("New value (-log(v)): "  + node.getValue());
	 			}	 				 			
	 		} else {
	 			if (node.isAndType()){		 		
	 				node.setType("or");
	 			} else {
		 			if (node.isOrType()){		 		
		 				node.setType("and");
		 			}
	 			}
	 		}
			
		}
		
	}
	
	public static void updateNodeLabelsWithMeasureValues(SecurityMetric metric, ProblemSpecification problem) {
		Map<String, List<AndOrNode>> nodesByInstanceId = problem.getNodesByInstanceId();
		Map<AndOrNode, String> labels = new LinkedHashMap<AndOrNode,String>();
		
		if (nodesByInstanceId != null && !nodesByInstanceId.isEmpty()) {
			Map<String, Measure> measureByInstanceId = problem.getMeasureByInstanceId(); 
			
			for (Map.Entry<String, List<AndOrNode>> entry : nodesByInstanceId.entrySet()) {
				Measure m = measureByInstanceId.get(entry.getKey());
				//System.out.println("Measure " + m.getId() + ", cost=" + m.getCost());				
				double measureCost = Double.valueOf(m.getCost());
				
				for (AndOrNode node : entry.getValue()) {
					if (!"inf".equalsIgnoreCase(node.getValue())) {
						String label = labels.get(node);
						if (label == null) {
							label = node.getValue();
							labels.put(node, label);
						}
						
						Double currentNodeCost = Double.valueOf(label) + measureCost;
						String newValue = "";
						if (currentNodeCost % 1 == 0) {
							newValue = String.valueOf(currentNodeCost.longValue());							
						} else {
							newValue = String.valueOf(currentNodeCost);							
						}
						labels.put(node, newValue);
						//System.out.println("Executing");
						node.setLabel(newValue);
					} else {
						node.setLabel("inf");
					}
				}
			}								
		} else {
			//No measures available			
			List<AndOrNode> nodes = problem.getGraph().getNodes();
			for (AndOrNode node : nodes) {
				if (node.isAtomicType()) {
					node.setLabel(node.getValue());					
				}
			}			
		}				
	}
	
	public static void outputSolution(TseitinStructure ts, ProblemSpecification problem, SecurityMetric metric, String path, ProblemSolution sol) {		
		try {
			File outputDirectory = new File(path);
			if (!outputDirectory.exists()) {
	            System.out.println("Creating view directory: " + path);
	            outputDirectory.mkdir();
	        }			
			String outputFilepath = "./" + path + "/sol.json";
			
			new JSONWriter().write(sol, outputFilepath);
			System.out.println("Solution saved in: " + outputFilepath);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void outputTxt(TseitinStructure ts, ProblemSpecification problem, SecurityMetric metric) {
		try {
			String path = CpsMetricAnalyser.OUTPUT_FOLDER;
			File outputDirectory = new File(path);
			if (!outputDirectory.exists()) {
	            System.out.println("Creating output directory: " + path);
	            outputDirectory.mkdir();
	        }
			
			String outputFilepath = "./" + path + "/cps-metric-out.txt";
			System.out.println("- Exporting TXT specification to: " + outputFilepath);
			FileOutputStream pOutputFileStream = new FileOutputStream(new File(outputFilepath));					
			ts.toStream(pOutputFileStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void outputWCNF(TseitinStructure ts, ProblemSpecification problem, SecurityMetric metric) {
		try {
			String path = CpsMetricAnalyser.OUTPUT_FOLDER;
			File outputDirectory = new File(path);
			if (!outputDirectory.exists()) {
	            System.out.println("Creating output directory: " + path);
	            outputDirectory.mkdir();
	        }
			
			String outputFilepath = "./" + path + "/cps-metric-out.wcnf";
			
			System.out.println("- Exporting WCNF specification to: " + outputFilepath);
			FileOutputStream wcnfOutputFileStream = new FileOutputStream(new File(outputFilepath));
			ts.toWCNF(wcnfOutputFileStream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void outputJson(TseitinStructure ts, ProblemSpecification problem, SecurityMetric metric) {
		String path = CpsMetricAnalyser.OUTPUT_FOLDER;
		File outputDirectory = new File(path);
		if (!outputDirectory.exists()) {
            System.out.println("Creating output directory: " + path);
            outputDirectory.mkdir();
        }
		
		String outputFilepath = "./" + path + "/cps-metric-out.json";
		ProblemSolution sol = new ProblemSolution(problem, metric);
		try {
			System.out.println("- Exporting JSON specification to: " + outputFilepath);
			new JSONWriter().write(sol, outputFilepath);
		} catch (JsonProcessingException | FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static Map<String,Object> analyseGraph(AndOrGraph g) {
		Map<String,Object> stats = new LinkedHashMap<String,Object>();

		List<AndOrNode> nodes = g.getNodes();
		stats.put("#nodes", nodes.size());
		stats.put("#edges", g.getEdges().size());

		int atomic = 0;
		int and = 0;
		int or = 0;
		int init = 0;

		for (AndOrNode n : nodes) {
			if (n.isInitType()) {
				init++;
			} else {
				if (n.isAtomicType()) {
					atomic++;
				}
				if (n.isOrType()) {
					or++;
				}
				if (n.isAndType()) {
					and++;
				}
			}
		}
		DecimalFormat df = new DecimalFormat(".##");

		if (CpsMetricAnalyser.DEBUG) {
			System.out.println("");
			System.out.println("Input graph stats: ");

			System.out.println("  #nodes: " + nodes.size());
			System.out.println("  #edges: " + g.getEdges().size());

			System.out.println("  #init: " + (init + "/" + nodes.size()));
			System.out.println("  #atomic: " + atomic + "/" + nodes.size() + " (" + df.format((atomic * 100.0) / nodes.size()) + "%)");
			System.out.println("  #and: " + and + "/" + nodes.size() + " (" + df.format((and * 100.0) / nodes.size()) + "%)");
			System.out.println("  #or: " + or + "/" + nodes.size()  + " (" + df.format((or * 100.0) / nodes.size()) + "%)");
			System.out.println("  #total: " + (init+atomic+and+or) + "/" + nodes.size() + " (" + df.format(((init+atomic+and+or) * 100.0) / nodes.size()) + "%)");
		}
		stats.put("#init", init);
		stats.put("#atomic", atomic);
		stats.put("#and", and);
		stats.put("#or", or);
		stats.put("#total", (init+atomic+and+or));

		return stats;
	}

}
