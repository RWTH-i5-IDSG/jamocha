package org.jamocha.dn.memory;

import java.util.Collection;
import java.util.List;

import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.AddressFilter;

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

	public void enqueueInEdges(final Collection<? extends Edge> edges);

	/**
	 * Releases the lock for the calling edge.
	 * 
	 * @return true iff this call was the last awaited call, thus ending the validity of the temp
	 *         handler
	 */
	public boolean releaseLock();

}