/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory;

import org.jamocha.dn.nodes.CouldNotAcquireLockException;
import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;
import org.jamocha.filter.Filter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public interface MemoryHandlerMain extends MemoryHandler {
	public boolean tryReadLock() throws InterruptedException;

	public void releaseReadLock();

	public void acquireWriteLock();

	public void releaseWriteLock();

	public void add(final MemoryHandlerTemp toAdd);

	public MemoryHandlerTemp processTokenInBeta(final MemoryHandlerTemp token,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException;

	public MemoryHandlerTemp processTokenInAlpha(final MemoryHandlerTemp token,
			final Edge originIncomingEdge, final Filter filter) throws CouldNotAcquireLockException;

	public MemoryHandlerTemp newToken(final Node otn, final Fact... facts);

}
