package org.jamocha.rete.nodes.joinfilter;

public class RightFieldAddress extends FieldAddress {

	protected int slotIndex;
	
	public Object clone(){
		return new RightFieldAddress(slotIndex);
	}
	
	public RightFieldAddress() {
		this(-1);
	}
	
	public RightFieldAddress(int slotIndex) {
		this.slotIndex = slotIndex;
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
	
}
