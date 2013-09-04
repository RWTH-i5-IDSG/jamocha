/**
 * Contains all classes relevant for the discrimination network. The discrimination network
 * implementation is split into two layers. The {@link org.jamocha.dn.nodes network layer} defines
 * the structure of the discrimination network and manages the traversal of tokens through the
 * network. The {@link org.jamocha.dn.memory memory layer} is responsible for managing the storage
 * of intermediate results in nodes, performing joins and generating the token contents. The memory
 * layer can be exchanged easily by providing an implementation of the
 * {@link org.jamocha.dn.memory.MemoryFactory MemoryFactory interface}.
 */
package org.jamocha.dn;

