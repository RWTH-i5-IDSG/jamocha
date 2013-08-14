package org.jamocha.engine.memory;

public interface MemoryHandler {
	public int size();

	public Template[] getTemplate();

	public Object getValue(final MemoryFactAddress address,
			final SlotAddress slot, final int row);
}
