package org.jamocha.rule;

public interface Complexity {

	/**
	 * Returns the complexity of the current node.
	 * 
	 * @return
	 */
	public abstract int getComplexity();

	/**
	 * Sets the complexity of the current node.
	 * 
	 */
	public abstract void setComplexity(int value);

	/**
	 * Returns the complexity of the complete subtree.
	 * 
	 * @return
	 */
	public abstract int getTotalComplexity();

	/**
	 * Increments the total complexity by the specific value.
	 * 
	 * @param value
	 */
	public abstract void incrementTotalComplexityBy(int value);

}