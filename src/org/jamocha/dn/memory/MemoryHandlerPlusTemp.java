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

import org.jamocha.dn.nodes.Node;
import org.jamocha.dn.nodes.Node.Edge;

/**
 * Interface for memories with short life time. {@link MemoryHandlerPlusTemp} instances are passed
 * around in the network as tokens. On creation of the {@link MemoryHandlerPlusTemp} a semaphore has
 * to be instantiated providing the possibility to call {@link MemoryHandlerPlusTemp#releaseLock()}
 * the number of times that was specified during creation. This would usually be the number of
 * {@link Edge edges}, the {@link MemoryHandlerPlusTemp} is passed over as a token. Each of the
 * {@link Edge edges} would call {@link MemoryHandlerPlusTemp#releaseLock()} and on the last call,
 * the {@link MemoryHandlerPlusTemp} would be committed to its originating {@link MemoryHandlerMain}
 * , which then adds the facts in the token and the validity of the {@link MemoryHandlerPlusTemp}
 * ends.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see MemoryHandlerMain
 * @see Node
 * @see Edge
 */
public interface MemoryHandlerPlusTemp extends MemoryHandlerTemp {
}
