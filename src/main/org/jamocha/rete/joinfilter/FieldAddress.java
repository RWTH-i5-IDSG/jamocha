package org.jamocha.rete.joinfilter;

/**
 * @author Josef Alexander Hahn
 * This is the first step for replacing our 101 binding-like classes
 * by a more systematic structure of classes.
 * a FieldAddress is immutable, because that world sound so good ;)
 */

public abstract class FieldAddress {
	
	public abstract boolean refersWholeFact();
	public abstract int getSlotIndex() throws FieldAddressingException;
	public abstract String toPPString();
}
