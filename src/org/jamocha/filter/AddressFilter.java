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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.Getter;

import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.function.fwa.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public class AddressFilter extends Filter<ParameterLeaf, AddressFilter.AddressFilterElement> {

	@Data
	public static class AddressMatchingConfiguration {
		final FactAddress factAddress;
		final List<MatchingConfigurationElement> matchingElements;

		public AddressMatchingConfiguration(final FactAddress factAddress,
				final List<MatchingConfigurationElement> matchingElements) {
			this.factAddress = factAddress;
			this.matchingElements = matchingElements;
			this.matchingElements.sort(Comparator.naturalOrder());
		}
	}

	public static AddressFilter empty = new NormalAddressFilter(new HashSet<FactAddress>(), new HashSet<FactAddress>(),
			new AddressFilterElement[] {}, Collections.emptyList());

	public static class NormalAddressFilter extends AddressFilter {
		public NormalAddressFilter(final Set<FactAddress> positiveExistentialAddresses,
				final Set<FactAddress> negativeExistentialAddresses, final AddressFilterElement[] filterElements,
				final List<AddressMatchingConfiguration> matchingConfigurations) {
			super(positiveExistentialAddresses, negativeExistentialAddresses, filterElements,
					(NormalAddressFilter) null, matchingConfigurations);
		}

		@Override
		public NormalAddressFilter getNormalisedVersion() {
			return this;
		}
	}

	protected final Set<FactAddress> positiveExistentialAddresses, negativeExistentialAddresses;
	private final NormalAddressFilter normalisedVersion;
	private final List<AddressMatchingConfiguration> matchingConfigurations;

	/**
	 * Checks whether the FactAddress is existential by calling the contains method on both sets
	 * (positive and negative).
	 * 
	 * @param factAddress
	 *            the fact address to check
	 * @return true iff the fact address passed is contained in one of the existential fact address
	 *         sets
	 */
	public boolean isExistential(final FactAddress factAddress) {
		return positiveExistentialAddresses.contains(factAddress) || negativeExistentialAddresses.contains(factAddress);
	}

	public AddressFilter(final Set<FactAddress> positiveExistentialAddresses,
			final Set<FactAddress> negativeExistentialAddresses, final AddressFilterElement[] filterElements,
			final NormalAddressFilter normalisedVersion, final List<AddressMatchingConfiguration> matchingConfigurations) {
		super(filterElements);
		this.positiveExistentialAddresses = positiveExistentialAddresses;
		this.negativeExistentialAddresses = negativeExistentialAddresses;
		this.normalisedVersion = normalisedVersion;
		this.matchingConfigurations = matchingConfigurations;
	}

	public AddressFilter(final Set<FactAddress> positiveExistentialAddresses,
			final Set<FactAddress> negativeExistentialAddresses, final AddressFilterElement[] filterElements,
			final AddressFilterElement[] normalFilterElements,
			final List<AddressMatchingConfiguration> matchingConfigurations) {
		this(positiveExistentialAddresses, negativeExistentialAddresses, filterElements, new NormalAddressFilter(
				positiveExistentialAddresses, negativeExistentialAddresses, normalFilterElements,
				matchingConfigurations), matchingConfigurations);
	}

	@Getter
	public static class AddressFilterElement extends Filter.FilterElement<ParameterLeaf> {
		final SlotInFactAddress addressesInTarget[];

		public AddressFilterElement(final PredicateWithArguments<ParameterLeaf> function,
				final SlotInFactAddress[] addressesInTarget) {
			super(function);
			this.addressesInTarget = addressesInTarget;
		}

		public CounterColumn getCounterColumn() {
			return null;
		}
	}

	public static class ExistentialAddressFilterElement extends AddressFilterElement {
		@Getter(onMethod = @__({ @Override }))
		protected final CounterColumn counterColumn;

		public ExistentialAddressFilterElement(final PredicateWithArguments<ParameterLeaf> function,
				final SlotInFactAddress[] addressesInTarget, final CounterColumn counterColumn) {
			super(function, addressesInTarget);
			this.counterColumn = counterColumn;
		}
	}
}
