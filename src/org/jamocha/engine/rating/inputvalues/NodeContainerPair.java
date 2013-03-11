package org.jamocha.engine.rating.inputvalues;

public class NodeContainerPair {
	protected final NodeContainer one, two;

	public NodeContainerPair(final NodeContainer one, final NodeContainer two) {
		this.one = one;
		this.two = two;
	}

	/**
	 * @return the pair the other way around
	 */
	public NodeContainerPair swap() {
		return new NodeContainerPair(two, one);
	}

	/**
	 * eclipse generated version
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.one == null) ? 0 : this.one.hashCode());
		result = prime * result
				+ ((this.two == null) ? 0 : this.two.hashCode());
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
		final NodeContainerPair other = (NodeContainerPair) obj;
		if (this.one == null) {
			if (other.one != null)
				return false;
		} else if (!this.one.equals(other.one))
			return false;
		if (this.two == null) {
			if (other.two != null)
				return false;
		} else if (!this.two.equals(other.two))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeContainerPair [one=" + one + ", two=" + two + "]";
	}

}
