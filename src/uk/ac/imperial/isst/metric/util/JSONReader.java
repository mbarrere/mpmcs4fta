/**
 * 
 */
package uk.ac.imperial.isst.metric.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.ProblemSpecification;
import uk.ac.imperial.isst.metric.model.AndOrEdge;
import uk.ac.imperial.isst.metric.model.AndOrGraph;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class JSONReader {

	public static Double MAX_DOUBLE =  Double.MAX_VALUE;
	
	public JSONReader() {		
	}				
	
	public ProblemSpecification loadProblemSpecification(String filename) throws Exception {		
	 	ObjectMapper mapper = new ObjectMapper();
	    File inputJSONFile = new File(filename);
	    
	    String artificialSource = GraphUtils.ARTIFICIAL_SOURCE;
	    
	    if (!inputJSONFile.exists()) {
	    	throw new IllegalSpecificationException("The specified JSON file does not exists: " + filename);
	    }
	    	 	
	 	JsonNode rootNode = mapper.readTree(inputJSONFile);
	 	
	 	//System.out.println("Content: ");	 	
	 	//String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode); //rootNode.asText();
	 	//System.out.println(jsonString);
	 		 		 	
	 	AndOrGraph graph = new AndOrGraph();
	 	
	 	String problemSource = null;
	 	String problemTarget = null;
	 	
	 	JsonNode graphNode = rootNode.path("graph");
	 	JsonNode sourceNode = graphNode.path("source");
	 	if (sourceNode.isMissingNode()) {	 		
	 		if (CpsMetricAnalyser.DEBUG) {
	 			System.out.println("[INFO] Problem source not specified. Analysing whether artificial source must be added.");
	 		}
	 		problemSource = artificialSource;
	 	} else {
	 		problemSource = sourceNode.textValue();
	 	}
	 	
	 	JsonNode targetNode = graphNode.path("target");
	 	if (targetNode.isMissingNode()) {
	 		throw new IllegalSpecificationException("[ERROR] Target node of the problem not specified");
	 	}else {
	 		problemTarget = targetNode.textValue();
	 	}	 	
	 		 	
	 	//Measures addition - 2019-03-12
	 	List<Measure> problemMeasures = new ArrayList<Measure>();	 	
	 	try {
 		 	
	 		JsonNode measuresNode = graphNode.path("measures");
		 	Iterator<JsonNode> measuresIter = measuresNode.iterator();
		
		 	while (measuresIter.hasNext()) {
		 		JsonNode n = measuresIter.next();
		 		
		 		Measure m = mapper.treeToValue(n, Measure.class);		 	
		 		problemMeasures.add(m);				
				if (CpsMetricAnalyser.DEBUG) {
					System.out.println("Adding measure " + m);		 		
				}
		 	}		 			 		 			 	
	 	} catch (Exception e) {				
			throw new IllegalSpecificationException(e.getMessage());
		}	 	
	 	
	 	try {
		 		 	
		 	JsonNode nodesNode = graphNode.path("nodes");
		 	Iterator<JsonNode> nodesIter = nodesNode.iterator();
		
		 	while (nodesIter.hasNext()) {
		 		JsonNode n = nodesIter.next();
		 		
		 		AndOrNode node = mapper.treeToValue(n, AndOrNode.class); 	
		 		
		 		/* ** MAX_PROBS ** */
		 		if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES && node.isAtomicType()) {
		 			node.setOriginalValue(node.getValue());
		 			String value = node.getValue();
		 			Double d = 0.0;
		 			if ("inf".equalsIgnoreCase(value)) {
						//d = MAX_DOUBLE;
		 				d = 0.0;
					} else {					
						d = Double.parseDouble(value);
					}
		 			
		 			if (CpsMetricAnalyser.DEBUG) {
		 				System.out.println("Original value for node " + node.getId() + ": "  + d);
		 			}			 			
		 			
		 			Double logValue = -1 * Math.log(d);
		 			
		 			if (logValue.isInfinite()) {
		 				node.setValue("inf");
		 			} else {
		 				DecimalFormat df = new DecimalFormat(".######");		 			
			 			//node.setValue(logValue.toString());
			 			node.setValue(df.format(logValue));
		 			}		 			
		 			if (CpsMetricAnalyser.DEBUG) {
		 				System.out.println("New value (-log(v)): "  + node.getValue());
		 			}
		 		}
		 		// UP TO THIS POINT /* ** MAX_PROBS ** */
		 		
		 		// MSCS4FTA
		 		if (CpsMetricAnalyser.MSCS) {
		 			//System.out.println("Node (PRE): " + node);
		 			if (node.isAndType()){		 		
		 				node.setType("or");
		 			} else {
			 			if (node.isOrType()){		 		
			 				node.setType("and");
			 			}
		 			}
		 			//System.out.println("Node (POST): " + node);
		 		}
		 		// UNTIL HERE (MSCS4FTA)
				graph.addNode(node);				
				if (CpsMetricAnalyser.DEBUG) {
					//System.out.println(node);		 		
				}
		 	}
		 	
		 	JsonNode edgesNode = graphNode.path("edges");
		 	Iterator<JsonNode> edgesIter = edgesNode.iterator();
		
		 	while (edgesIter.hasNext()) {
		 		JsonNode e = edgesIter.next();
		 		
		 		AndOrEdge edge = mapper.treeToValue(e, AndOrEdge.class);		 	
				graph.addEdge(edge);	
				if (CpsMetricAnalyser.DEBUG) {
					System.out.println(edge);
				}
		 	}
		 			 	
	 	} catch (Exception e) {				
			throw new IllegalSpecificationException(e.getMessage());
		}
	 	
	 	GraphUtils utils = new GraphUtils();
	 	problemSource = utils.unifySources(graph, artificialSource);
	 	utils.verifyInputGraph(graph);
	 	
	 	if (!graph.containsNode(problemSource)) {
	 		throw new IllegalSpecificationException("[ERROR] The source node of the problem '" + problemSource + "' does not appear in the graph specification");
	 	}
	 	
	 	if (!graph.containsNode(problemTarget)) {
	 		throw new IllegalSpecificationException("[ERROR] The target node of the problem '" + problemTarget + "' does not appear in the graph specification");
	 	}
		
	 	graph.setSource(problemSource);
	 	graph.setTarget(problemTarget);
	 	
	 	return new ProblemSpecification(graph, problemSource, problemTarget, problemMeasures);
	 		 	
    }

}
