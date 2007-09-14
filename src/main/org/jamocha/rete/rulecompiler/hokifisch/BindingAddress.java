package org.jamocha.rete.rulecompiler.hokifisch;

import org.jamocha.rete.ConversionUtils;

/**
 * @author Josef Alexander Hahn
 * A Binding address is an address of one slot in a facttuple.
 * it contains of a factIndex, which addresses the fact inside the tuple
 * and a slotIndex, which addresses the slot inside the fact.
 */
public class BindingAddress implements Comparable<BindingAddress> {
	public int factIndex;

	public int slotIndex;

	public int operator;

	public boolean canBePivot;

	public BindingAddress(int conditionIndex, int slotIndex, int operator) {
		super();
		this.factIndex = conditionIndex;
		this.slotIndex = slotIndex;
		this.operator = operator;
	}

	public int compareTo(BindingAddress o) {
		int conditionDifference = this.factIndex - o.factIndex;

		if (conditionDifference != 0)
			return conditionDifference;

		return this.slotIndex - o.slotIndex;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("TupleIndex: ");
		result.append(factIndex);
		result.append(" SlotIndex: ");
		result.append(slotIndex);
		result.append(" Operator: ");
		result.append(ConversionUtils.getOperator(operator));
		result.append(" canBePivot: ");
		result.append(canBePivot);
		return result.toString();
	}

}