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
package org.jamocha.filter;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            Collection type the addresses are collected in
 */
public class SlotInFactAddressCollector<T extends Collection<SlotInFactAddress>> {

	private final T addresses;

	public SlotInFactAddressCollector(final T addresses) {
		this.addresses = addresses;
	}

	public SlotInFactAddressCollector<T> collect(final AddressFilter filter) {
		for (final AddressFilterElement filterElement : filter.getFilterElements()) {
			collect(filterElement);
		}
		return this;
	}

	public SlotInFactAddressCollector<T> collect(final AddressFilterElement filterElement) {
		for (final SlotInFactAddress address : filterElement.getAddressesInTarget()) {
			this.getAddresses().add(address);
		}
		return this;
	}

	public static SlotInFactAddressCollector<HashSet<SlotInFactAddress>> newHashSet() {
		return new SlotInFactAddressCollector<HashSet<SlotInFactAddress>>(new HashSet<SlotInFactAddress>());
	}

	public static SlotInFactAddressCollector<LinkedHashSet<SlotInFactAddress>> newLinkedHashSet() {
		return new SlotInFactAddressCollector<LinkedHashSet<SlotInFactAddress>>(new LinkedHashSet<SlotInFactAddress>());
	}

	public static SlotInFactAddressCollector<ArrayList<SlotInFactAddress>> newArrayList() {
		return new SlotInFactAddressCollector<ArrayList<SlotInFactAddress>>(new ArrayList<SlotInFactAddress>());
	}

	public static SlotInFactAddressCollector<LinkedList<SlotInFactAddress>> newLinkedList() {
		return new SlotInFactAddressCollector<LinkedList<SlotInFactAddress>>(new LinkedList<SlotInFactAddress>());
	}

	/**
	 * @return the addresses
	 */
	public T getAddresses() {
		return this.addresses;
	}

	/**
	 * @return the addresses
	 */
	public SlotInFactAddress[] getAddressesArray() {
		return toArray(getAddresses(), SlotInFactAddress[]::new);
	}

}
