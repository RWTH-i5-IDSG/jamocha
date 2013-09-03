package org.jamocha.dn.memory;

public interface MemoryHandler {
	public int size();

	public Template[] getTemplate();

	public Object getValue(final FactAddress address, final SlotAddress slot,
			final int row);
}
