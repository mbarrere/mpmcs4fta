/**
 * 
 */
package uk.ac.imperial.isst.metric.solvers;

import java.util.Map;

//import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class OptimSolution {

	private String status;
	private Double cost;
	private Double time_ms;
	private Integer size;
	
	//@JsonIgnore
	private Map<String,Double> solution;
	
	public OptimSolution() {
		// TODO Auto-generated constructor stub
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getTime_ms() {
		return time_ms;
	}

	public void setTime_ms(Double time_ms) {
		this.time_ms = time_ms;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Map<String, Double> getSolution() {
		return solution;
	}

	public void setSolution(Map<String, Double> solution) {
		this.solution = solution;
	}

	@Override
	public String toString() {
		return "OptimSolution [status=" + status + ", cost=" + cost
				+ ", time_ms=" + time_ms + ", size=" + size + ", solution="
				+ solution + "]";
	}

}
