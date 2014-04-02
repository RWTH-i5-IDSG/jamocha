package org.jamocha.dn.memory;

/**
 * Super interface of the memory handlers {@link MemoryHandlerMain} and {@link MemoryHandlerTemp}.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see MemoryHandlerMain
 * @see MemoryHandlerTemp
 */
public interface MemoryHandler {
	/**
	 * Gets the size of the underlying memory.
	 * 
	 * @return the size of the underlying memory
	 */
	public int size();

	/**
	 * Gets the {@link Template} of the facts in the underlying memory.
	 * 
	 * @return the {@link Template} of the facts in the underlying memory.
	 * @see Template
	 */
	public Template[] getTemplate();

	/**
	 * Fetches a value from the memory fully identified by a {@link FactAddress}, a
	 * {@link SlotAddress} and a row number.
	 * 
	 * @param address
	 *            a {@link FactAddress} identifying the fact the wanted value is in
	 * @param slot
	 *            a {@link SlotAddress} identifying the slot the wanted value is in
	 * @param row
	 *            the row number in the underlying memory
	 * @return a value from the memory identified by the given parameters
	 * @see FactAddress
	 * @see SlotAddress
	 */
	public Object getValue(final FactAddress address, final SlotAddress slot, final int row);
}
