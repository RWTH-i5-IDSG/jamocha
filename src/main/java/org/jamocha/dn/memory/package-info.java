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

/**
 * Contains the interface to the Memory.
 *
 * The network gains access to intances of memory for its {@link org.jamocha.dn.nodes.Node nodes} via the {@link
 * org.jamocha.dn.memory.MemoryFactory}. It returns instances of the {@link org.jamocha.dn.memory.MemoryHandlerMain}
 * interface, representing the main memory of a {@link org.jamocha.dn.nodes.Node node}. Via the {@link
 * org.jamocha.dn.memory.MemoryHandlerMain}, nodes can create instances of
 * {@link org.jamocha.dn.memory.MemoryHandlerPlusTemp}
 * and pass them around in the network as tokens. <br /> This package also provides the {@link
 * org.jamocha.dn.memory.SlotAddress}, {@link org.jamocha.dn.memory.FactAddress}, {@link org.jamocha.dn.memory.SlotType}
 * and {@link org.jamocha.dn.memory.Template} classes. These are used to address values in the memory and get memory
 * instances for specific types of values.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryFactory
 * @see org.jamocha.dn.memory.MemoryHandler
 * @see org.jamocha.dn.memory.MemoryHandlerMain
 * @see org.jamocha.dn.memory.MemoryHandlerPlusTemp
 * @see org.jamocha.dn.memory.Template
 * @see org.jamocha.dn.memory.SlotType
 * @see org.jamocha.dn.memory.FactAddress
 * @see org.jamocha.dn.memory.SlotAddress
 */
package org.jamocha.dn.memory;
