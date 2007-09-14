package org.jamocha.rete.rulecompiler.hokifisch;

import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;

/**
 * @author Josef Alexander Hahn
 * a SlotCompareConfiguration describes a compare between two slots
 * inside one facttuple. it has fact&slot index for right and left side
 * and an operator.
 */
public class SlotCompareConfiguration {
	public int leftIndex;

	public int rightIndex;

	public int leftSlot;

	public int rightSlot;

	public int operator;

	public String varName;

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("SlotCompareConfiguration: (");
		result.append(leftIndex);
		result.append(",");
		result.append(leftSlot);
		result.append(")");
		result.append(ConversionUtils.getOperatorDescription(operator));
		result.append("(");
		result.append(rightIndex);
		result.append(",");
		result.append(rightSlot);
		result.append(")");
		return result.toString();
	}

	public SlotCompareConfiguration(BindingAddress left, BindingAddress right,
			String varName) {
		super();
		this.varName = varName;
		this.leftIndex = left.factIndex;
		this.rightIndex = right.factIndex;
		this.leftSlot = left.slotIndex;
		this.rightSlot = right.slotIndex;
		if (left.operator == Constants.EQUAL) {
			this.operator = right.operator;
		} else if (right.operator == Constants.EQUAL) {
			this.operator = left.operator;
		} else {
			this.operator = Constants.NILL;
		}
	}

}
