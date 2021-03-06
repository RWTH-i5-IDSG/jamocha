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
package org.jamocha.filter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressNodeFilterSet.AddressFilter;

import java.util.*;

import static org.jamocha.util.ToArray.toArray;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FactAddressCollector<T extends Collection<FactAddress>> {

    private final T addresses;

    public FactAddressCollector(final T addresses) {
        this.addresses = addresses;
    }

    public FactAddressCollector<T> collect(final AddressNodeFilterSet filter) {
        for (final AddressFilter filterElement : filter.getFilters()) {
            collect(filterElement);
        }
        return this;
    }

    public FactAddressCollector<T> collect(final AddressFilter filterElement) {
        for (final SlotInFactAddress address : filterElement.getAddressesInTarget()) {
            this.addresses.add(address.getFactAddress());
        }
        return this;
    }

    public static FactAddressCollector<HashSet<FactAddress>> newHashSet() {
        return new FactAddressCollector<HashSet<FactAddress>>(new HashSet<FactAddress>());
    }

    public static FactAddressCollector<LinkedHashSet<FactAddress>> newLinkedHashSet() {
        return new FactAddressCollector<LinkedHashSet<FactAddress>>(new LinkedHashSet<FactAddress>());
    }

    public static FactAddressCollector<ArrayList<FactAddress>> newArrayList() {
        return new FactAddressCollector<ArrayList<FactAddress>>(new ArrayList<FactAddress>());
    }

    public static FactAddressCollector<LinkedList<FactAddress>> newLinkedList() {
        return new FactAddressCollector<LinkedList<FactAddress>>(new LinkedList<FactAddress>());
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
    public FactAddress[] getAddressesArray() {
        return toArray(getAddresses(), FactAddress[]::new);
    }

}
