package uk.ac.imperial.isst.metric.model;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class MeasureInstance {

	private String id;
	private String type;	
	
	public MeasureInstance() {
		// default construct for JSON Jackson
	}
	
	public MeasureInstance(String id, String type) {
		super();
		this.id = id;
		this.type = type;		
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
		MeasureInstance other = (MeasureInstance) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasureInstance [id=" + id + ", type=" + type + "]";
	}	

}
