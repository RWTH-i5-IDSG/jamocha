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

import static java.util.Arrays.stream;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.CounterColumnMatcher;
import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.AddressFilter.ExistentialAddressFilterElement;
import org.jamocha.filter.PathFilter.DummyPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwatransformer.FWAPathToAddressTranslator;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathFilterToAddressFilterTranslator {
	public static AddressFilter translate(final PathFilter pathFilter,
			final CounterColumnMatcher filterElementToCounterColumn) {
		return translate(pathFilter, pathFilter.normalise(), filterElementToCounterColumn);
	}

	public static AddressFilter translate(final PathFilter pathFilter,
			final PathFilter normalisedVersion,
			final CounterColumnMatcher filterElementToCounterColumn) {
		return new AddressFilter(toFactAddressSet(pathFilter.getPositiveExistentialPaths()),
				toFactAddressSet(pathFilter.getNegativeExistentialPaths()), translateFEs(
						pathFilter, filterElementToCounterColumn), translateFEs(normalisedVersion,
						filterElementToCounterColumn));
	}

	private static AddressFilterElement[] translateFEs(final PathFilter pathFilter,
			final CounterColumnMatcher filterElementToCounterColumn) {
		return toArray(
				stream(pathFilter.getFilterElements()).map(
						fe -> fe.accept(new PathFilterElementTranslator(
								filterElementToCounterColumn)).result), AddressFilterElement[]::new);
	}

	static Set<FactAddress> toFactAddressSet(final Set<Path> existentialPaths) {
		return existentialPaths.stream().map(Path::getFactAddressInCurrentlyLowestNode)
				.collect(Collectors.toSet());
	}

	@Data
	static class PathFilterElementTranslator implements PathFilterElementVisitor {
		final CounterColumnMatcher counterColumnMatcher;
		AddressFilterElement result;

		@Override
		public void visit(final PathFilterElement pathFilterElement) {
			final ArrayList<SlotInFactAddress> addresses = new ArrayList<>();
			final PredicateWithArguments predicateWithArguments =
					pathFilterElement
							.getFunction()
							.accept(new FWAPathToAddressTranslator.PWAPathToAddressTranslator(
									addresses)).getFunctionWithArguments();
			final SlotInFactAddress[] addressArray = toArray(addresses, SlotInFactAddress[]::new);
			final CounterColumn counterColumn =
					counterColumnMatcher.getCounterColumn(pathFilterElement);
			if (null == counterColumn)
				this.result = new AddressFilterElement(predicateWithArguments, addressArray);
			this.result =
					new ExistentialAddressFilterElement(predicateWithArguments, addressArray,
							counterColumn);
		}

		@Override
		public void visit(final DummyPathFilterElement pathFilterElement) {
			final PredicateWithArguments predicateWithArguments = pathFilterElement.getFunction();
			final SlotInFactAddress[] addressArray =
					toArray(Arrays.stream(pathFilterElement.getPaths()).map(
							path -> new SlotInFactAddress(path
									.getFactAddressInCurrentlyLowestNode(), (SlotAddress) null)),
							SlotInFactAddress[]::new);
			final CounterColumn counterColumn =
					counterColumnMatcher.getCounterColumn(pathFilterElement);
			if (null == counterColumn)
				this.result = new AddressFilterElement(predicateWithArguments, addressArray);
			this.result =
					new ExistentialAddressFilterElement(predicateWithArguments, addressArray,
							counterColumn);
		}
	}
}
