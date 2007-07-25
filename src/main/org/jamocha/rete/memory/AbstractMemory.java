package org.jamocha.rete.memory;


public abstract class AbstractMemory {

	public AbstractMemory() {
		super();
	}
	
	protected abstract String contentToString();
	
	protected abstract String contentToString(int length);
	
	public abstract void clear();
	
	public abstract int getSize();
	
	protected StringBuffer ppStringHelper() {
		StringBuffer result = new StringBuffer();
		result.append(" Memory Size: ");
		result.append(getSize());
		result.append("; Content: ");
		result.append("\n");
		return result;
		
	}
	
	public String toPPString() {
		return ppStringHelper().append(contentToString()).toString();
	}
	
	public String toPPString(int length) {
		return ppStringHelper().append(contentToString(length)).toString();
	}

	public String toString() {
		return toPPString();
	}

}