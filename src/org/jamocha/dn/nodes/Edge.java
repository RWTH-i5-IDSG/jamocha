/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.dn.nodes;

import java.util.LinkedList;
import java.util.Map;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.MemoryHandlerMinusTemp;
import org.jamocha.dn.memory.MemoryHandlerPlusTemp;
import org.jamocha.dn.memory.MemoryHandlerTemp;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.AddressFilter.AddressFilterElement;

/**
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface Edge {

	public void processPlusToken(final MemoryHandlerTemp memory)
			throws CouldNotAcquireLockException;

	public void processMinusToken(final MemoryHandlerTemp memory)
			throws CouldNotAcquireLockException;

	public Node getSourceNode();

	public Node getTargetNode();

	/**
	 * Transforms an address valid for the source node of the input into the corresponding address
	 * valid for the target node of the input.
	 * 
	 * @param addressInSource
	 *            an address valid in the source node of the input
	 * @return an address valid in the target node of the input
	 */
	public FactAddress localizeAddress(final FactAddress addressInSource);

	/**
	 * Sets the map used for {@link Edge#localizeAddress(FactAddress)}
	 * 
	 * @param map
	 *            Map used for {@link Edge#localizeAddress(FactAddress)}
	 */
	public void setAddressMap(final Map<? extends FactAddress, ? extends FactAddress> map);

	/**
	 * Disconnects the nodeInput from the formerly connected nodes. This will remove the input from
	 * the target node inputs as well as from the source node children.
	 */
	public void disconnect();

	public void setFilter(final AddressFilter filter);

	public AddressFilter getFilter();

	public AddressFilterElement[] getFilterPartsForCounterColumns();

	public LinkedList<MemoryHandlerPlusTemp> getTempMemories();

	public void enqueueMemory(final MemoryHandlerPlusTemp mem);

	public void enqueueMemory(final MemoryHandlerMinusTemp mem);
}