package org.jamocha.dn.memory;

import java.util.List;

import org.jamocha.dn.nodes.Edge;

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

	public void enqueueInEdge(final Edge edge);

	/**
	 * Releases the lock for the calling edge.
	 */
	public void releaseLock();

}