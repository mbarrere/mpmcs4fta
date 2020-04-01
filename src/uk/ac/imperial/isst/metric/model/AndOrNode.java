/**
 * 
 */
package uk.ac.imperial.isst.metric.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class AndOrNode {

	private String id;
	private String type;
	private String value;
	private String label;
	private List<MeasureInstance> measures;
	private String icon;
	
	@JsonIgnore
	private String originalValue;
	
	@JsonIgnore
	public String getOriginalValue() {
		return originalValue;
	}

	@JsonIgnore
	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	public AndOrNode() {
		// default construct for JSON Jackson
	}
	
	public AndOrNode(String id, String type, String value, String label) {
		this(id, type, value, label, null);		
	}
		
	
	public AndOrNode(String id, String type, String value, String label,
			List<MeasureInstance> measures) {
		this(id, type, value, label, measures, null);			
	}
	
	public AndOrNode(String id, String type, String value, String label,
			List<MeasureInstance> measures, String icon) {
		super();
		this.id = id;
		this.type = type;
		this.value = value;
		this.label = label;
		this.measures = measures;
		this.icon = icon;
	}

	public AndOrNode(String id, String type, String value) {
		this(id, type, value, null);		
	}
	
	public AndOrNode(String id, String type) {
		this(id, type, null);		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public List<MeasureInstance> getMeasures() {
		return measures;
	}

	public void setMeasures(List<MeasureInstance> measures) {		
		this.measures = measures;
	}
		
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AndOrNode other = (AndOrNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
	@Override
	public String toString() {
		return "AndOrNode [id=" + id + ", type=" + type + ", value=" + value
				+ ", label=" + label + ", measures=" + measures + "]";
	}

	@JsonIgnore
	public boolean isInitType() {
		return "init".equalsIgnoreCase(this.getType());
	}
	
	@JsonIgnore
	public boolean isAndType() {
		return "and".equalsIgnoreCase(this.getType());
	}
	
	@JsonIgnore
	public boolean isOrType() {
		return "or".equalsIgnoreCase(this.getType());
	}
	
	@JsonIgnore
	public boolean isAtomicType() {
		return !this.isAndType() && ! this.isOrType();
	}
	
	@JsonIgnore
	public boolean isGoalOrFunction() {
		return "goal".equalsIgnoreCase(this.getType()) || "function".equalsIgnoreCase(this.getType());
	}	
}
