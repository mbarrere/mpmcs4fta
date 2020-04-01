/**
 * 
 */
package uk.ac.imperial.isst.metric.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.model.AndOrEdge;
import uk.ac.imperial.isst.metric.model.AndOrGraph;
import uk.ac.imperial.isst.metric.model.AndOrNode;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class GraphUtils {

	public static final String ARTIFICIAL_SOURCE = "_s_";
	
	public GraphUtils() {	
	}
	
	public String unifySources(AndOrGraph graph, String artificialSource) throws Exception {
		List<AndOrNode> nodes = graph.getNodes();		
		List<AndOrNode> sources = new ArrayList<AndOrNode>();
		
		for (AndOrNode node : nodes) {	
			int inCount = graph.getIncomingEdges(node.getId()).size();
			if (inCount == 0) {
				sources.add(node);
			}			
		}
		int count = sources.size();
		if (CpsMetricAnalyser.DEBUG) {
			System.out.println("Count sources: " + count);
		}
		if (count > 1 || (count==1 && !sources.get(0).isInitType())) {			
			AndOrNode init = new AndOrNode(artificialSource, "init", "inf", "init");
			graph.addNode(init);			
			if (CpsMetricAnalyser.DEBUG) {
				System.out.println("\n[INFO] " + count + " sources detected => unification in process. ");
			}
			for (int i = 0; i < count; i++) {
				AndOrNode target = sources.get(i);
				AndOrEdge e = new AndOrEdge(init.getId(), target.getId(), "inf", "init-" + target.getType());
				graph.addEdge(e);
			}
			if (CpsMetricAnalyser.DEBUG) {
				System.out.println("[INFO] New artificial source: " + artificialSource);
			}
			return artificialSource;
		} else {
			return sources.get(0).getId();						
		}
	}
	
	public void verifyInputGraph(AndOrGraph graph) throws IllegalSpecificationException {
		List<AndOrNode> nodes = graph.getNodes();		
		
		for (AndOrNode node : nodes) {	
			int inCount = graph.getIncomingEdges(node.getId()).size();
			if (node.isAtomicType()) {
				if (inCount != 1 && !node.isInitType()) {
					throw new IllegalSpecificationException("[Specification error] Atomic-type node '" + node.getId() + "' has " + inCount + " incoming edges while it should have just 1.");
				}
				if (inCount > 0 && node.isInitType()) {
					throw new IllegalSpecificationException("[Specification error] Init-type node '" + node.getId() + "' has " + inCount + " incoming edges while it should have none.");
				}
			}
			
			if (node.isAndType()) {
				if (inCount < 2 ) {
					throw new IllegalSpecificationException("[Specification error] And-type node '" + node.getId() + "' has " + inCount + " incoming edges while it should have at least 2.");				
				} 
			}

			if (node.isOrType()) {
				if (inCount < 2 ) {
					throw new IllegalSpecificationException("[Specification error] Or-type node '" + node.getId() + "' has " + inCount + " incoming edges while it should have at least 2.");				
				} 
			}						
		}
	}
	
	public void showNodeDistribution(AndOrGraph graph) throws IllegalSpecificationException {
		List<AndOrNode> nodes = graph.getNodes();		
		
		int total = 0;
		int atomic = 0;
		int ands = 0;
		int ors = 0;
		
		for (AndOrNode node : nodes) {	
			total++;
			
			if (node.isAtomicType()) {
				atomic++;				
			}
			
			if (node.isAndType()) {
				ands++;
			}

			if (node.isOrType()) {
				ors++; 
			}						
		}
		
		if (CpsMetricAnalyser.DEBUG) {
			System.out.println("Graph distribution #nodes: " + total);
		 	System.out.println("Graph distribution #atomic: " + atomic);
		 	System.out.println("Graph distribution #ands: " + ands);
		 	System.out.println("Graph distribution #ors: " + ors);
		 	//System.out.println("Graph distribution #edges: " + graph.getEdges().size());
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}


}
