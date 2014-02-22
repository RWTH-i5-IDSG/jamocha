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

import lombok.Getter;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.fwa.PredicateWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class AddressFilter extends Filter<AddressFilter.AddressFilterElement> {

	public static AddressFilter empty = new AddressFilter(new FactAddress[] {},
			new FactAddress[] {}, new AddressFilterElement[] {});

	@Getter
	protected final FactAddress positiveExistentialAddresses[], negativeExistentialAddresses[];

	public AddressFilter(final FactAddress[] positiveExistentialAddresses,
			final FactAddress[] negativeExistentialAddresses,
			final AddressFilterElement[] filterElements) {
		super(filterElements);
		this.positiveExistentialAddresses = positiveExistentialAddresses;
		this.negativeExistentialAddresses = negativeExistentialAddresses;
	}

	@Getter
	public static class AddressFilterElement extends Filter.FilterElement {
		final SlotInFactAddress addressesInTarget[];

		public AddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget) {
			super(function);
			this.addressesInTarget = addressesInTarget;
		}

		public int getCounterColumnIndex() {
			return -1;
		}
	}

	public static class ExistentialAddressFilterElement extends AddressFilterElement {
		final int counterColumnIndex;

		public ExistentialAddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget, final int counterColumnIndex) {
			super(function, addressesInTarget);
			this.counterColumnIndex = counterColumnIndex;
		}

		@Override
		public int getCounterColumnIndex() {
			return this.counterColumnIndex;
		}
	}

}
