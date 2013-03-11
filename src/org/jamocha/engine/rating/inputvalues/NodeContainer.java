package org.jamocha.engine.rating.inputvalues;

import org.jamocha.engine.rating.FilterState;

public class NodeContainer {
	final int id;
	final FilterState state;

	public NodeContainer(final int id, final FilterState state) {
		this.id = id;
		this.state = state;
	}

	/**
	 * eclipse generated version
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		result = prime * result
				+ ((this.state == null) ? 0 : this.state.hashCode());
		return result;
	}

	/**
	 * eclipse generated version
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NodeContainer other = (NodeContainer) obj;
		if (this.id != other.id)
			return false;
		if (this.state != other.state)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeContainer [id=" + id + ", state=" + state + "]";
	}

}
