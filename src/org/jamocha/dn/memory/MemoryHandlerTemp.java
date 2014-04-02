package org.jamocha.dn.memory;

import java.util.List;

import org.jamocha.dn.nodes.Edge;

/**
 * Base Interface for memories with short life time.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Edge
 */
public interface MemoryHandlerTemp extends MemoryHandler {
	/**
	 * Splits the {@link MemoryHandlerTemp} into several memories each holding {@code size} entries
	 * (with the possible exception of the last returned handler, which may contain less).
	 * 
	 * @param size
	 *            number of entries to be held by each handler
	 * @return list of handlers holding {@code size} entries (the last one may contain less)
	 */
	public List<MemoryHandler> splitIntoChunksOfSize(final int size);

	/**
	 * Enqueues this {@link MemoryHandlerTemp} in the {@link Edge} passed. This is done by calling
	 * the corresponding {@link Edge#enqueueMemory} method.
	 * 
	 * @param edge
	 *            {@link Edge edge} the {@link MemoryHandlerTemp} is to be enqueued in.
	 */
	public void enqueueInEdge(final Edge edge);

	/**
	 * Releases the lock for the calling edge.
	 */
	public void releaseLock();

}