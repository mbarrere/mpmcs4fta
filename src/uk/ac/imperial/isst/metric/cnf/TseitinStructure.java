/**
 * 
 */
package uk.ac.imperial.isst.metric.cnf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.ProblemSpecification;
import uk.ac.imperial.isst.metric.SecurityMetric;
import uk.ac.imperial.isst.metric.model.AndOrGraph;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;
import uk.ac.imperial.isst.metric.solvers.Sat4jSolver;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class TseitinStructure {

	private static Double MAX_DOUBLE =  Sat4jSolver.MAX_DOUBLE;
	
	private TseitinVisitor tseitinVisitor;			
	private List<Integer> nodes;
	private List<List<Integer>> hardClauses;
	private Map<Integer,Double> weights;
	private Long executionTime;
	private SecurityMetric securityMetric;
	private Integer x;
	private Formula formula;
	
	public TseitinStructure(TseitinVisitor tseitinVisitor, Integer x, Formula formula) {
		super();
		this.tseitinVisitor = tseitinVisitor;
		this.nodes = null;
		this.hardClauses = null;
		this.weights = new LinkedHashMap<Integer,Double>();
		this.executionTime = Long.valueOf(0);
		this.securityMetric = null;
		this.x = x;
		this.formula = formula;
	}

	public void setup(ProblemSpecification problemSpec, Long time, SecurityMetric metric) throws Exception {
		
		if (CpsMetricAnalyser.DEBUG) {			
			System.out.println("Setting up Tseitin structure...");
		}
		this.tseitinVisitor.buildTseitinStructure(this);
		if (CpsMetricAnalyser.DEBUG) {			
			System.out.println("Setting up Tseitin weights...");
		}
		this.addWeights(tseitinVisitor.getVarNameMap(), problemSpec);
		this.executionTime = time;
		this.securityMetric = metric;
	}
	
	
	
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}
	
	public TseitinVisitor getTseitinVisitor() {
		return tseitinVisitor;
	}

	public void setTseitinVisitor(TseitinVisitor tseitinVisitor) {
		this.tseitinVisitor = tseitinVisitor;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public SecurityMetric getSecurityMetric() {
		return securityMetric;
	}

	public void setSecurityMetric(SecurityMetric securityMetric) {
		this.securityMetric = securityMetric;
	}

	public List<Integer> getNodes() {
		return nodes;
	}

	public void setNodes(List<Integer> nodes) {
		this.nodes = nodes;
	}

	public List<List<Integer>> getHardClauses() {
		return hardClauses;
	}

	public void setHardClauses(List<List<Integer>> hardClauses) {
		this.hardClauses = hardClauses;
	}
	
	public void addWeights(Map<Integer, Object> varNameMap, ProblemSpecification problemSpec) {
		AndOrGraph graph = problemSpec.getGraph();
		Map<String, Measure> measureByInstanceId = problemSpec.getMeasureByInstanceId();
		
		for (Integer i : nodes) {
			Object nodeId = varNameMap.get(i);
			if (nodeId != null) {
				if (CpsMetricAnalyser.DEBUG) {			
					System.out.println("Analysing node: " + nodeId);
				}	
				AndOrNode node = graph.getNode((String)nodeId);
				if (node != null) {
					if ("inf".equalsIgnoreCase(node.getValue())) {
						weights.put(i, MAX_DOUBLE);
					} else {
						weights.put(i, Double.valueOf(node.getValue()));
					}
				} else {
					// NEW for measures 2019-03-12
					if (CpsMetricAnalyser.DEBUG) {			
						System.out.println("Measure found in formula... analysing: " + nodeId);
					}
					if (measureByInstanceId !=null && !measureByInstanceId.isEmpty()) {
						Measure measure = measureByInstanceId.get(nodeId);
						if (measure != null) {
							String cost = measure.getCost();
							if ("inf".equalsIgnoreCase(cost)) {
								weights.put(i, MAX_DOUBLE);
							} else {
								weights.put(i, Double.valueOf(cost));
							}
						} else {							
							System.out.println("[Warning] Measure " + nodeId + " not found in measures by instanceId");
							weights.put(i, 0.0);
						}
					} 					
				}
			} else {
				//weights.put(i, MAX_DOUBLE);
				weights.put(i, 0.0);
			}
		}
	}
	
	public List<Integer> getTseitinLiterals() {
		
		Map<Integer, Object> varNameMap = this.tseitinVisitor.getVarNameMap();
		List<Integer> literals = new ArrayList<Integer>();
		
		for (Integer i : nodes) {
			Object nodeId = varNameMap.get(i);
			if (nodeId == null) {
				literals.add(i);				
			} 
		}
		
		return literals;
	}
	
	public Map<Integer, Double> getWeights() {
		return weights;
	}

	public void setWeights(Map<Integer, Double> weights) {
		this.weights = weights;
	}

	@Override
	public String toString() {
		return "TseitinStructure [nodes=" + nodes + ", hardClauses="
				+ hardClauses + "]";
	}
	
	public void toStream(OutputStream os) throws IOException {
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os));
	    //System.out.println("NODES: " + nodes);
	    w.write("nodes=[");
	    for (int i = 0; i < nodes.size(); i++) {
	    	w.write(nodes.get(i) + ((i==nodes.size()-1)?"]\n":","));
	    }	    	   
	    
	    w.write("clauses=[");
	    for (int i = 0; i < hardClauses.size(); i++) {	    
	    	List<Integer> clause = hardClauses.get(i);
	    	
	    	w.write("[");
	    	for (int j = 0; j < clause.size(); j++) {
		    	w.write(clause.get(j) + ((j==clause.size()-1)?"]":","));
		    }
		    
		    w.write((i==hardClauses.size()-1)?"]\n":",");
	    }    
	    
	    Map<Integer, Object> varNameMap = tseitinVisitor.getVarNameMap();
	    
	    
	    w.write("map={");	    
	    int mapLitCount = 0;
	    for (Integer i : varNameMap.keySet()) {
	    	mapLitCount++;
	    	w.write(i + ":" + varNameMap.get(i)  + ((mapLitCount==varNameMap.keySet().size())?"}\n":","));
	    }
	    
	    
	    w.write("graph_literals=[");	     
	    int gLitCount = 0;
	    for (Integer i : varNameMap.keySet()) {
	    	gLitCount++;
	    	w.write(i + ((gLitCount==varNameMap.keySet().size())?"]\n":","));
	    }
	    
	    /*
	    w.write("tseitin_literals=[");
	    List<Integer> tLit= this.getTseitinLiterals(); 
	    int tLitCount = 0;
	    for (Integer i : tLit) {
	    	tLitCount++;
	    	w.write(i + ((tLitCount==tLit.size())?"]\n":","));
	    }
		*/
	    
	    w.write("costs={");
	    int count = 0;
	    Double maxValue = MAX_DOUBLE;
	    //for (int i = 0; i < weights.size(); i++) {
	    for (Map.Entry<Integer,Double> e : weights.entrySet()) {
	    	count++;
	    	//w.write(e.getKey() + ":" + e.getValue() + ((count==weights.size())?"]\n":","));
	    	w.write(e.getKey() + ":" + (maxValue.equals(e.getValue())?"'inf'":e.getValue()) + ((count==weights.size())?"}\n":","));
	    }	
	    
	    if (securityMetric != null) {
		    w.write("time_ms=[" + executionTime + "]\n");
		    	    
			if (securityMetric.getCost() != null && securityMetric.getCost().compareTo(MAX_DOUBLE)==0) {
				w.write("total_cost=[inf]\n");
			} else {		
				w.write("total_cost=[" + securityMetric.getCost() + "]\n");
			}
			
			//System.out.println("Total nodes: " + nodes.size());
			//System.out.print("CUT solution: ");
		
			int solCount = 0;
			w.write("solution=[");
			for (AndOrNode n : securityMetric.getNodes()) {	
				solCount++;
				Integer index = tseitinVisitor.getIdsMap().get(n.getId());
				w.write(index + ":" + weights.get(index) + ((solCount==securityMetric.getNodes().size())?"]\n":","));
			}
		}
		
	    w.flush();
	}
		
	public void toWCNF(OutputStream os) throws IOException {
		// MaxSAT Evaluation 2019
		//p wcnf nbvar nbclauses top
		/*
		c
		c comment line
		c
		p wcnf 4 5 16
		16 1 -2 4 0
		16 -1 -2 3 0
		8 -2 -4 0
		4 -3 2 0
		3 1 3 0
		*/
		
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os));
	    
		int top = 1000000; 
		long nbclauses = hardClauses.size() + weights.size();
				
		w.write("c\nc META4ICS\n");
		
		if (securityMetric != null) {
		    
			if (securityMetric.getCost() != null && securityMetric.getCost().compareTo(MAX_DOUBLE)==0) {				
				w.write("c #total_cost = inf\n");
			} else {		
				Double cost = securityMetric.getCost();
				if (cost % 1 == 0) {
					w.write("c #total_cost = " + cost.longValue() + "\n");
				} else {
					w.write("c #total_cost = " + cost + "\n");
				}
				
			}
			
			w.write("c #total_nodes = " + securityMetric.getNodes().size() + "\n");
			
			int solCount = 0;

			w.write("c #solution = [");
			for (AndOrNode n : securityMetric.getNodes()) {	
				solCount++;
				Integer index = tseitinVisitor.getIdsMap().get(n.getId());
				long value = weights.get(index).longValue();
				w.write("(" + index + ":" + value + ((solCount==securityMetric.getNodes().size())?")]\n":"),"));
			}
			
			w.write("c #time_ms = " + executionTime + "\n");
			w.write("c\nc * WCNF SPECIFICATION *\nc\n");
		}
				
		w.write("p wcnf " + nodes.size() + " " + nbclauses + " " + top + "\n");
		
	    for (int i = 0; i < hardClauses.size(); i++) {	    
	    	List<Integer> clause = hardClauses.get(i);
	    	
	    	w.write(top + " ");
	    	for (int j = 0; j < clause.size(); j++) {
		    	w.write(clause.get(j) + " ");
		    }
	    	w.write(0 + "\n");		    
	    }    
	    
	    Double maxValue = MAX_DOUBLE;
	    
	    for (Map.Entry<Integer,Double> e : weights.entrySet()) {
	    	long value = maxValue.equals(e.getValue())?top:e.getValue().longValue();
	    	
	    	if (value > 0) {
	    		// Advice from Ruben Martins
	    		// Most solvers assume soft clauses to have a value equal or higher than 1
	    		// We are generating values from 1 onwards so only Tseitin clauses with 0 weight will be discarded
	    		w.write(value + " " + e.getKey() + " 0\n");
	    	}	    	
	    }	
	    
	    w.flush();
	}

}
