/**
 * 
 */
package uk.ac.imperial.isst.metric;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.imperial.isst.metric.cnf.TseitinStructure;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;
import uk.ac.imperial.isst.metric.solvers.Sat4jSolver;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class SecurityMetric {

	private static Double MAX_DOUBLE =  Sat4jSolver.MAX_DOUBLE;
	
	private List<AndOrNode> nodes;
	private Map<String, Measure> measureByInstanceIdMap;
	private Double cost;
	private TseitinStructure tseitinStructure;
	private Integer x;	
	private String solverId;
	
	private ProblemSpecification problemSpec;
	
	public SecurityMetric(ProblemSpecification problemSpec) {
		this.problemSpec = problemSpec;
		this.nodes = new ArrayList<AndOrNode>();
		this.cost = -1.0;
		this.tseitinStructure = null;
		this.measureByInstanceIdMap = new LinkedHashMap<String,Measure>();
	}

	public SecurityMetric(List<AndOrNode> nodes, Double cost) {
		super();
		this.nodes = nodes;
		this.cost = cost;
	}
	
	public String getSolverId() {
		return solverId;
	}

	public void setSolverId(String solverId) {
		this.solverId = solverId;
	}

	public List<AndOrNode> getNodes() {
		return nodes;
	}
	
	public Map<String, Measure> getMeasureByInstanceIdMap() {
		return measureByInstanceIdMap;
	}

	public void setNodes(List<AndOrNode> nodes) {
		this.nodes = nodes;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	public void setTseitinStructure(TseitinStructure ts) {
		this.tseitinStructure = ts;
	}
	
	@JsonIgnore
	public TseitinStructure getTseitinStructure() {
		return this.tseitinStructure;
	}

	@JsonIgnore
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	@Override
	public String toString() {
		return "SecurityMetric [nodes=" + nodes + ", cost=" + cost + "]";
	}

	public void display() {		
		System.out.println("=== Security Metric ==="); 
		if (cost != null && cost.compareTo(MAX_DOUBLE)==0) {
			System.out.println("CUT cost: INFINITE");
		} else {			
			/* ** MAX_PROBS ** */
			//DecimalFormat df = new DecimalFormat(".######");		
	 		if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
	 			//System.out.println("Joint probability of failure: " + GraphUtils.round(Math.exp(-1 * cost), CpsMetricAnalyser.DECIMAL_PLACES));
	 			System.out.println("Joint probability of failure: " + cost);
	 		} else {
	 			System.out.println("CUT cost: " + cost);
	 		}			
		}						
		
		/* ** MAX_PROBS ** */		
		int failedNodes = 0;
		if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
			System.out.print("[+] Failed nodes: ");		
			for (AndOrNode n : nodes) {				
				if (!measureByInstanceIdMap.containsKey(n.getId())) {
					Double d = Double.parseDouble(n.getValue());
					//if ("1.0".equals(n.getValue())){					
					if (d.doubleValue() == 1.0){
						failedNodes++;
						System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
					}
				}
			}
			if (failedNodes == 0) {
				System.out.print("none");
			}
		}
		System.out.println("");

		//String mscsAcronym = "MSCS";
		/* ** MAX_PROBS ** */
		if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
			if (cost == 0.0) {
				if (CpsMetricAnalyser.MSCS) {
					System.out.println("Total critical nodes: 0");
					System.out.println("[+] Minimal Stochastic Cut Set (MSCS): none");
				} else {
					System.out.println("Total critical nodes: 0");
					System.out.println("[+] Most likely critical set (MLCS): none");
				}
				return;
			}
		}
		
		
		System.out.println("Total critical nodes: " + (nodes.size() - (measureByInstanceIdMap.size() + failedNodes)));
		if (!measureByInstanceIdMap.isEmpty()) {
			System.out.println("Involved security measures: " + measureByInstanceIdMap.size());
		}
		
		/* ** MAX_PROBS ** */
		if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
			if (CpsMetricAnalyser.MSCS) {
				System.out.print("[+] Minimal Stochastic Cut Set (MSCS): ");
			} else {
				System.out.print("[+] Most likely critical set (MLCS): ");
			}
		} else {
			System.out.print("[+] Critical nodes: ");
		}
				
		for (AndOrNode n : nodes) {			
			if (!measureByInstanceIdMap.containsKey(n.getId())) {
				if (CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES) {
					if (!"1.0".equals(n.getValue())) {
						System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
					}
				} else {
					System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
				}				
			}
		}
		System.out.println("");
		
		
		
		if (!measureByInstanceIdMap.isEmpty()) {				
			System.out.println("[+] Security measure instances: ");
		
			for (AndOrNode n : nodes) {
				if (measureByInstanceIdMap.containsKey(n.getId())) {
					System.out.print("\t(" + n.getId() + "," + n.getValue() + ") -> [");
					List<AndOrNode> affectedNodes = this.problemSpec.getNodesByInstanceId().get(n.getId());
					for (int i = 0; i < affectedNodes.size()-1; i++) {
						System.out.print(affectedNodes.get(i).getId() + ",");
					}
					System.out.println(affectedNodes.get(affectedNodes.size()-1).getId() + "];");
				}
			}
			
			System.out.print("[+] Full solution: ");
			
			for (AndOrNode n : nodes) {				
				System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
			}
			System.out.println("");
			
			System.out.print("[+] Critical nodes display: ");			
			for (AndOrNode n : nodes) {
				if (!measureByInstanceIdMap.containsKey(n.getId())) {
					System.out.print("(" + n.getId() + "," + n.getLabel() + ")" + "; ");
				}
			}
			System.out.println("");
		}
		
	}
	
	public void displayOLD() {		
		System.out.println("=== Security Metric ==="); 
		if (cost != null && cost.compareTo(MAX_DOUBLE)==0) {
			System.out.println("CUT cost: INFINITE");
		} else {			
			System.out.println("CUT cost: " + cost);
		}
		System.out.println("Total critical nodes: " + (nodes.size()-measureByInstanceIdMap.size()));
		if (!measureByInstanceIdMap.isEmpty()) {
			System.out.println("Involved security measures: " + measureByInstanceIdMap.size());
		}
		System.out.print("[+] Critical nodes: ");		
		for (AndOrNode n : nodes) {			
			if (!measureByInstanceIdMap.containsKey(n.getId())) {				
				System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
			}
		}
		System.out.println("");
		
		
		if (!measureByInstanceIdMap.isEmpty()) {				
			System.out.println("[+] Security measure instances: ");
		
			for (AndOrNode n : nodes) {
				if (measureByInstanceIdMap.containsKey(n.getId())) {
					System.out.print("\t(" + n.getId() + "," + n.getValue() + ") -> [");
					List<AndOrNode> affectedNodes = this.problemSpec.getNodesByInstanceId().get(n.getId());
					for (int i = 0; i < affectedNodes.size()-1; i++) {
						System.out.print(affectedNodes.get(i).getId() + ",");
					}
					System.out.println(affectedNodes.get(affectedNodes.size()-1).getId() + "];");
				}
			}
			
			System.out.print("[+] Full solution: ");
			
			for (AndOrNode n : nodes) {				
				System.out.print("(" + n.getId() + "," + n.getValue() + ")" + "; ");
			}
			System.out.println("");
			
			System.out.print("[+] Critical nodes display: ");			
			for (AndOrNode n : nodes) {
				if (!measureByInstanceIdMap.containsKey(n.getId())) {
					System.out.print("(" + n.getId() + "," + n.getLabel() + ")" + "; ");
				}
			}
			System.out.println("");
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cost == null) ? 0 : cost.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityMetric other = (SecurityMetric) obj;
		if (cost == null) {
			if (other.cost != null)
				return false;
		} else if (!cost.equals(other.cost))
			return false;
		
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else {
			if (!nodes.containsAll(other.nodes) || !other.nodes.containsAll(nodes)){
				return false;
			}			
		}
		return true;
	}
	
	
}
