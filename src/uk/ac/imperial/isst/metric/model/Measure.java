package uk.ac.imperial.isst.metric.model;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class Measure {

	private String id;
	private String cost;
	private String desc;	
	
	public Measure() {
		// default construct for JSON Jackson
	}	
	
	public Measure(String id, String cost, String desc) {
		super();
		this.id = id;
		this.cost = cost;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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
		Measure other = (Measure) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Measure [id=" + id + ", cost=" + cost + ", desc=" + desc + "]";
	}

}
