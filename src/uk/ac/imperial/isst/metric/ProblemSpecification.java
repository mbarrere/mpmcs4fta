/**
 * 
 */
package uk.ac.imperial.isst.metric;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.imperial.isst.metric.cnf.CNF;
import uk.ac.imperial.isst.metric.cnf.Formula;
import uk.ac.imperial.isst.metric.model.AndOrGraph;
import uk.ac.imperial.isst.metric.model.AndOrNode;
import uk.ac.imperial.isst.metric.model.Measure;
import uk.ac.imperial.isst.metric.model.MeasureInstance;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class ProblemSpecification {

	private AndOrGraph graph;
	private String source;
	private String target;
	private List<Measure> measures;
	private Map<String, Measure> measureMap;
	private Map<String, Measure> measureByInstanceId;
	private Map<String, List<AndOrNode>> nodesByInstanceId;

	
	public ProblemSpecification(AndOrGraph graph, String source, String target, List<Measure> measures) {
		super();
		this.graph = graph;
		this.source = source;
		this.target = target;
		this.measures = measures;
		
		if (measures != null && !measures.isEmpty()) {
			
			this.measureMap = new LinkedHashMap<String,Measure>();
			for (Measure m : measures) {
				measureMap.put(m.getId(), m);
			}
			
			this.measureByInstanceId = new LinkedHashMap<String,Measure>();						
			this.nodesByInstanceId = new LinkedHashMap<String,List<AndOrNode>>();
			
			for (AndOrNode node : graph.getNodes()) {
				List<MeasureInstance> instances = node.getMeasures(); 
				if (instances != null && !instances.isEmpty()) {
					for (MeasureInstance mi : instances) {
						if (!this.measureByInstanceId.containsKey(mi.getId())) {
							this.measureByInstanceId.put(mi.getId(), measureMap.get(mi.getType()));
						}
						if (!this.nodesByInstanceId.containsKey(mi.getId())) {
							this.nodesByInstanceId.put(mi.getId(), new ArrayList<AndOrNode>());
						}
						List<AndOrNode> nodes = this.nodesByInstanceId.get(mi.getId());
						if (!nodes.contains(node)) {
							nodes.add(node);
						}
					}
				}
			}
		}
	}
	
	public boolean involvesMultipleMeasures() {
		return this.measureByInstanceId != null && !this.measureByInstanceId.isEmpty();
	}

	public ProblemSpecification(AndOrGraph graph, String source, String target) {
		this(graph, source, target, null);		
	}

	public AndOrGraph getGraph() {
		return graph;
	}

	public void setGraph(AndOrGraph graph) {
		this.graph = graph;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	
	public List<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}
		
	public Map<String, Measure> getMeasureMap() {
		return measureMap;
	}

	public void setMeasureMap(Map<String, Measure> measureMap) {
		this.measureMap = measureMap;
	}

	public Map<String, Measure> getMeasureByInstanceId() {
		return measureByInstanceId;
	}

	public void setMeasureByInstanceId(Map<String, Measure> measureByInstanceId) {
		this.measureByInstanceId = measureByInstanceId;
	}

	
	public Map<String, List<AndOrNode>> getNodesByInstanceId() {
		return nodesByInstanceId;
	}

	public void setNodesByInstanceId(Map<String, List<AndOrNode>> nodesByInstanceId) {
		this.nodesByInstanceId = nodesByInstanceId;
	}

	@Override
	public String toString() {
		return "ProblemSpecification [graph=" + graph + ", source=" + source
				+ ", target=" + target + ", measures=" + measures + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result
				+ ((measures == null) ? 0 : measures.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		ProblemSpecification other = (ProblemSpecification) obj;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		if (measures == null) {
			if (other.measures != null)
				return false;
		} else if (!measures.equals(other.measures))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	private List<AndOrNode> filterNodes (List<AndOrNode> incomingNodes, List<AndOrNode> visited) {
		List<AndOrNode> filtered = new ArrayList<AndOrNode>();
		for (AndOrNode n : incomingNodes) {
			if (!visited.contains(n)) {
				filtered.add(n);
			}
		}
		return filtered;
	}
	
	private String getMultiSentence(List<AndOrNode> nodes, String operator, List<AndOrNode> visited) throws Exception {
		if (nodes.size() == 0) {
			return "true";
		}
		String sentence = "(";
		for (int i = 0; i < nodes.size()-1; i++) {
			AndOrNode n = nodes.get(i);
			String s = getSentence(n, visited);
			
			sentence += " " + s + " " + operator;
		}
		AndOrNode n = nodes.get(nodes.size()-1);
		String s = getSentence(n, visited);		
		sentence += " " + s + " )";
		
		return sentence;
	}
	
	private String getSentence(AndOrNode node, List<AndOrNode> visited) throws Exception {
		visited.add(node); 
		List<AndOrNode> incoming = graph.getIncomingNodes(node.getId());
		
		String sentence = "";
		if (node.isAtomicType()) {
			if (incoming.isEmpty()) {
				sentence = node.getId();
			} else {
				AndOrNode inNode = incoming.get(0);
				if (visited.contains(inNode)) {
					sentence = "( " + node.getId() + " )";
				} else {
					sentence = "( " + node.getId() + " & " + getSentence(inNode, visited) + " )";
				}
			}
		}
		
		if (node.isAndType()) {
			if (incoming.isEmpty()) {
				throw new Exception("[ERROR] And-type node '" + node.getId() + "' has no incoming edges");				
			} else {
				incoming = filterNodes(incoming, visited);
				sentence = getMultiSentence(incoming, "&", visited); 
			}
		}
		
		if (node.isOrType()) {
			if (incoming.isEmpty()) {
				throw new Exception("[ERROR] Or-type node '" + node.getId() + "' has no incoming edges");				
			} else {
				incoming = filterNodes(incoming, visited);
				sentence = getMultiSentence(incoming, "|", visited); 
			}
		}
		visited.remove(node);
		return sentence;
	}
	
	public String getLogicalFormulaString() throws Exception {		
		AndOrNode tgt = graph.getNode(target);	
		List<AndOrNode> visited = new ArrayList<AndOrNode>();					
		return this.getSentence(tgt, visited);
	}
	
	public String negateString(String formula) throws Exception {						
		return "~" + formula;		
	}
	
	
	
	/*
	 * Tseitin
	 */
	
	public Formula negateFormula(Formula f) throws Exception {
		return CNF.neg(f);		
	}
	
	public Formula getLogicalFormula() throws Exception {		
		AndOrNode tgt = graph.getNode(target);	
		List<AndOrNode> visited = new ArrayList<AndOrNode>();					
		return this.getLogicalSentence(tgt, visited);
	}
	
	private Formula getNodeFormula(AndOrNode n) {
		Formula atomicNodeForm = CNF.var(n.getId()); 
		List<MeasureInstance> measures = n.getMeasures();
		if (measures == null || measures.isEmpty()) {
			return atomicNodeForm;
		} else {			
			List<Formula> forms = new ArrayList<Formula>();
			forms.add(atomicNodeForm);
			
			for (int i = 0; i < measures.size(); i++) {
				MeasureInstance m = measures.get(i);
				Formula f = CNF.var(m.getId());
				forms.add(f);			
			}
			
			return CNF.or(forms);
		}
	}
	
	private Formula getLogicalSentence(AndOrNode node, List<AndOrNode> visited) throws Exception {
		visited.add(node); 
		List<AndOrNode> incoming = graph.getIncomingNodes(node.getId());
		
		Formula fm = null;
		if (node.isAtomicType()) {
			if (incoming.isEmpty()) {				
				fm = getNodeFormula(node);
			} else {
				AndOrNode inNode = incoming.get(0);
				if (visited.contains(inNode)) {					
					fm = getNodeFormula(node);
				} else {									
					fm = CNF.and(getNodeFormula(node), getLogicalSentence(inNode, visited));
				}
			}
		}
		
		if (node.isAndType()) {
			if (incoming.isEmpty()) {
				throw new Exception("[ERROR] And-type node '" + node.getId() + "' has no incoming edges");				
			} else {
				incoming = filterNodes(incoming, visited);
				fm = getMultiLogicalSentence(incoming, "&", visited); 
			}
		}
		
		if (node.isOrType()) {
			if (incoming.isEmpty()) {
				throw new Exception("[ERROR] Or-type node '" + node.getId() + "' has no incoming edges");				
			} else {
				incoming = filterNodes(incoming, visited);
				fm = getMultiLogicalSentence(incoming, "|", visited); 
			}
		}
		visited.remove(node);
		return fm;
	}
	
	private Formula getMultiLogicalSentence(List<AndOrNode> nodes, String operator, List<AndOrNode> visited) throws Exception {
		if (nodes.size() == 0) {
			return CNF.tt();
		}
		List<Formula> forms = new ArrayList<Formula>();
		
		for (int i = 0; i < nodes.size(); i++) {
			AndOrNode n = nodes.get(i);
			Formula f = getLogicalSentence(n, visited);
			forms.add(f);			
		}
		
		if ("&".equals(operator)) {
			return CNF.and(forms);
		} else {
			return CNF.or(forms);
		}		
	}
}
