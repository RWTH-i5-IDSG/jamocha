/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
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
     * Splits the {@link MemoryHandlerTemp} into several memories each holding {@code size} entries (with the possible
     * exception of the last returned handler, which may contain less).
     *
     * @param size
     *         number of entries to be held by each handler
     * @return list of handlers holding {@code size} entries (the last one may contain less)
     */
    List<MemoryHandler> splitIntoChunksOfSize(final int size);

    /**
     * Enqueues this {@link MemoryHandlerTemp} in the {@link Edge} passed. This is done by calling the corresponding
     * {@link Edge#enqueueMemory} method.
     *
     * @param edge
     *         {@link Edge edge} the {@link MemoryHandlerTemp} is to be enqueued in.
     */
    void enqueueInEdge(final Edge edge);

    /**
     * Releases the lock for the calling edge.
     */
    void releaseLock();
}
