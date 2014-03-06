package org.jamocha.dn.memory;

import java.util.List;

import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.AddressFilter;

/**
 * Base interface for memories with short life time.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Edge
 */
public interface MemoryHandlerTemp extends MemoryHandler {
	/**
	 * Splits the temp memory into several memories each holding {@code size} entries (with the
	 * possible exception of the last returned handler)
	 * 
	 * @param size
	 *            number of entries to be held by each handler
	 * @return list of handlers holding {@code size} entries (the last one may contain less)
	 */
	public List<MemoryHandler> splitIntoChunksOfSize(final int size);

	public MemoryHandlerTemp newBetaTemp(final MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException;

	public MemoryHandlerTemp newAlphaTemp(final MemoryHandlerMain originatingMainHandler,
			final Edge originIncomingEdge, final AddressFilter filter)
			throws CouldNotAcquireLockException;

	public void enqueueInEdge(final Edge edge);

	/**
	 * Releases the lock for the calling edge. Creates a new memory handler in case some lines are
	 * now valid that could not have been found to be valid at the time this temp was created.
	 * 
	 * @return the new memory handler or null if it would be empty
	 */
	public MemoryHandlerTemp releaseLock();

}