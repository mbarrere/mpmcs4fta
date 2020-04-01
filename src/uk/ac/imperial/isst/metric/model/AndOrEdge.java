/**
 * 
 */
package uk.ac.imperial.isst.metric.model;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class AndOrEdge {

	private String source;
	private String target;
	private String value;
	private String label;
	
	public AndOrEdge() {
		// default construct for JSON Jackson
	}
	
	public AndOrEdge(String source, String target, String value, String label) {
		super();
		this.source = source;
		this.target = target;
		this.value = value;
		this.label = label;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		AndOrEdge other = (AndOrEdge) obj;
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

	@Override
	public String toString() {
		return "AndOrEdge [source=" + source + ", target=" + target
				+ ", value=" + value + ", label=" + label + "]";
	}

}
