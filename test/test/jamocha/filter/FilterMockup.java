/*
 * Copyright 2002-2013 The Jamocha Team
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
package test.jamocha.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.Value;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.FilterTranslator;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.function.Function;
import org.jamocha.function.impls.FunctionVisitor;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.TestData.SomeStuff;

/**
 * A mockup filter implementation for testing purposes.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FilterMockup extends PathFilter {

	public FilterMockup(final boolean returnValue, final PathAndSlotAddress... pathAndSlotAddresses) {
		super(createDummyPathFilterElement(returnValue, pathAndSlotAddresses));
	}

	@Value
	public static class PathAndSlotAddress {
		Path path;
		SlotAddress slotAddress;
	}

	private static PathFilterElement createDummyPathFilterElement(final boolean returnValue,
			final PathAndSlotAddress... pathAndSlotAddresses) {
		final ArrayList<? extends SlotType> slotTypesC =
				Arrays.stream(pathAndSlotAddresses).map((final PathAndSlotAddress pasa) -> {
					return pasa.slotAddress.getSlotType(pasa.path.getTemplate());
				}).collect(Collectors.toCollection(ArrayList::new));
		final SlotType[] slotTypes = slotTypesC.toArray(new SlotType[slotTypesC.size()]);
		final PredicateBuilder predicateBuilder =
				new PredicateBuilder(new org.jamocha.function.Predicate() {
					@Override
					public String inClips() {
						return "DUMMY";
					}

					@Override
					public SlotType[] getParamTypes() {
						return slotTypes;
					}

					@Override
					public Boolean evaluate(final Function<?>... params) {
						return returnValue;
					}

					@Override
					public <V extends FunctionVisitor> V accept(final V visitor) {
						return visitor;
					}
				});
		for (final PathAndSlotAddress pasa : pathAndSlotAddresses) {
			predicateBuilder.addPath(pasa.path, pasa.slotAddress);
		}
		return predicateBuilder.buildPFE();
	}

	/**
	 * Create a Filter that always evaluates to true.
	 * 
	 * @param paths
	 *            {@link Path paths} to store in the predicate
	 * @return a filter that always evaluates to true
	 */
	public static FilterMockup alwaysTrue(final PathAndSlotAddress... pathAndSlotAddresses) {
		return new FilterMockup(true, pathAndSlotAddresses);
	}

	/**
	 * Create a Filter that always evaluates to false.
	 * 
	 * @param paths
	 *            {@link Path paths} to store in the predicate
	 * @return a filter that always evaluates to false
	 */
	public static FilterMockup alwaysFalse(final PathAndSlotAddress... pathAndSlotAddresses) {
		return new FilterMockup(false, pathAndSlotAddresses);
	}

	/**
	 * Test class for {@link test.jamocha.filter.FilterMockup} using Theories.
	 * 
	 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
	 * 
	 */
	@RunWith(Theories.class)
	public static class FilterMockupTest {
		/**
		 * Test method for {@link test.jamocha.filter.FilterMockup#alwaysTrue(Path...)}.
		 */
		@Theory
		public void testAlwaysTrue(@SomeStuff Object... obj) {
			final AddressFilter alwaysTrue =
					FilterTranslator.translate(FilterMockup.alwaysTrue(),
							counterColumnMatcherMockup);
			for (final FilterElement filterElement : alwaysTrue.getFilterElements()) {
				assertTrue(filterElement.getFunction().evaluate(obj));
			}
		}

		/**
		 * Test method for {@link test.jamocha.filter.FilterMockup#alwaysFalse(Path...)} .
		 */
		@Theory
		public void testAlwaysFalse(@SomeStuff Object... obj) {
			final AddressFilter alwaysFalse =
					FilterTranslator.translate(FilterMockup.alwaysFalse(),
							counterColumnMatcherMockup);
			for (final FilterElement filterElement : alwaysFalse.getFilterElements()) {
				assertFalse(filterElement.getFunction().evaluate(obj));
			}
		}
	}
}
