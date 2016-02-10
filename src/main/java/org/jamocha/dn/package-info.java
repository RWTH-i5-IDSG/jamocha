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
 * <p> Contains all classes relevant for the discrimination network. The discrimination network implementation is split
 * into two layers. </p>
 *
 * <p> The {@linkplain org.jamocha.dn.nodes network layer} defines the structure of the discrimination network and
 * manages the traversal of tokens through the network. </p>
 *
 * <p> The {@linkplain org.jamocha.dn.memory memory layer} is responsible for managing the storage of intermediate
 * results in nodes, performing joins and generating the token contents. The memory layer can be exchanged easily by
 * providing an implementation of the {@linkplain org.jamocha.dn.memory.MemoryFactory MemoryFactory interface}. </p>
 */
package org.jamocha.dn;

