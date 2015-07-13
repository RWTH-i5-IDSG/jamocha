/**
 * <p>
 * Contains all classes relevant for the discrimination network. The discrimination network
 * implementation is split into two layers.
 * </p>
 * 
 * <p>
 * The {@linkplain org.jamocha.dn.nodes network layer} defines the structure of the discrimination
 * network and manages the traversal of tokens through the network.
 * </p>
 * 
 * <p>
 * The {@linkplain org.jamocha.dn.memory memory layer} is responsible for managing the storage of
 * intermediate results in nodes, performing joins and generating the token contents. The memory
 * layer can be exchanged easily by providing an implementation of the
 * {@linkplain org.jamocha.dn.memory.MemoryFactory MemoryFactory interface}.
 * </p>
 */
package org.jamocha.dn;
