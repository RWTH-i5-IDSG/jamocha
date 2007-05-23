package org.jamocha.rete;


public abstract class AbstractMemory {

	public AbstractMemory() {
		super();
	}
	
	protected abstract String contentToString();
	
	public abstract void clear();
	
	public abstract int getSize();
	
	public String toPPString() {
		StringBuffer result = new StringBuffer();
		result.append(" Memory Size: ");
		result.append(getSize());
		result.append("; Content: ");
		result.append("\n");
		result.append("[");
		result.append(contentToString());
		result.append("]");
		return result.toString();
	}

	public String toString() {
		return toPPString();
	}

}