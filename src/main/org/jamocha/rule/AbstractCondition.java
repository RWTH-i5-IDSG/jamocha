package org.jamocha.rule;

public abstract class AbstractCondition implements Condition {

	protected static int complexity = 1;

	protected int totalComplexity = 0;

	public int getComplexity() {
		return AbstractCondition.complexity;
	}

	public int getTotalComplexity() {
		return (AbstractCondition.complexity + totalComplexity);
	}

	public void incrementTotalComplexityBy(int value) {
		totalComplexity += value;
	}

	public void setComplexity(int value) {
		AbstractCondition.complexity = value;
	}

}
