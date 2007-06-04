package org.jamocha.rete.joinfilter;


public class LeftFieldAddress extends FieldAddress {
	protected int slotIndex;
	protected int rowIndex;
	
	public LeftFieldAddress(int rowIndex) {
		this(rowIndex, -1);
	}
	
	public LeftFieldAddress(int rowIndex, int slotIndex) {
		this.slotIndex = slotIndex;
		this.rowIndex = rowIndex;
	}
	
	public boolean refersWholeFact() {
		return slotIndex == -1;
	}
	
	public int getSlotIndex() throws FieldAddressingException {
		if (slotIndex == -1) throw new FieldAddressingException();
		return slotIndex;
	}
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public String toPPString(){
		StringBuffer result = new StringBuffer();
		result.append("left(row=");
		result.append(rowIndex);
		if (slotIndex == -1) {
			result.append(";whole fact)");
		} else {
			result.append(";slot=").append(slotIndex).append(")");
		}
		return result.toString();
	}


}
