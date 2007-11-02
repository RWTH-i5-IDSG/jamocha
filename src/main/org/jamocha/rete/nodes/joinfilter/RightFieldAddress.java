package org.jamocha.rete.nodes.joinfilter;

import org.jamocha.formatter.Formatter;

public class RightFieldAddress extends FieldAddress {

	protected int slotIndex;
	protected int posIndex;
	
	public Object clone(){
		return this;
	}
	
	public RightFieldAddress() {
		this(-1, -1);
	}
	
	public RightFieldAddress(int slotIndex) {
		this(slotIndex, -1);
	}
	
	public RightFieldAddress(int slotIndex, int posIndex) {
		this.slotIndex = slotIndex;
		this.posIndex = posIndex;
	}
	
	public boolean refersWholeFact() {
		return slotIndex == -1;
	}
	
	public int getSlotIndex() throws FieldAddressingException {
		if (slotIndex == -1) throw new FieldAddressingException();
		return slotIndex;
	}
	
	public String toPPString(){
		return getExpressionString();
	}
	
	public String getExpressionString(){
		if (slotIndex == -1) {
			return "right(whole fact)";
		} else {
			StringBuffer result = new StringBuffer();
			result.append("right(slot=").append(slotIndex).append(")");
			return result.toString();
		}
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}
	
}
