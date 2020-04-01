/**
 * 
 */
package uk.ac.imperial.isst.metric;

import uk.ac.imperial.isst.metric.model.AndOrGraph;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class ProblemSolution {

	private AndOrGraph graph;
	private SecurityMetric cut;
	
	public ProblemSolution(ProblemSpecification problem, SecurityMetric cut) {
		super();
		this.graph = problem.getGraph();
		this.cut = cut;
	}

	public AndOrGraph getGraph() {
		return graph;
	}

	public void setGraph(AndOrGraph graph) {
		this.graph = graph;
	}

	public SecurityMetric getCut() {
		return cut;
	}

	public void setCut(SecurityMetric cut) {
		this.cut = cut;
	}

}
