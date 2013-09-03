/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package test.jamocha.engine.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.engine.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class FilterMockup extends Filter {
	public FilterMockup(final boolean returnValue) {
		super(new FunctionWithArguments[] { new FunctionWithArguments() {
			@Override
			public SlotType getReturnType() {
				return SlotType.BOOLEAN;
			}

			@Override
			public SlotType[] getParamTypes() {
				return SlotType.empty;
			}

			@Override
			public Object evaluate(final Object... params) {
				return returnValue;
			}

			@Override
			public FunctionWithArguments translatePath(
					final ArrayList<SlotInFactAddress> addressesInTarget) {
				return this;
			}

			@Override
			public void gatherPaths(final Set<Path> paths) {
			}
		} });
	}

	public static FilterMockup alwaysTrue() {
		final FilterMockup filterMockup = new FilterMockup(true);
		filterMockup.translatePath();
		return filterMockup;
	}

	public static FilterMockup alwaysFalse() {
		final FilterMockup filterMockup = new FilterMockup(false);
		filterMockup.translatePath();
		return filterMockup;
	}

	/**
	 * Test method for
	 * {@link test.jamocha.engine.filter.FilterMockup#alwaysTrue()} .
	 */
	@Test
	public void testAlwaysTrue() {
		final Filter alwaysTrue = FilterMockup.alwaysTrue();
		for (final FilterElement filterElement : alwaysTrue.getFilterElements()) {
			assertTrue((Boolean) filterElement.getFunction().evaluate(1, 2, 3));
			assertTrue((Boolean) filterElement.getFunction().evaluate(
					(Object) null));
			assertTrue((Boolean) filterElement.getFunction().evaluate(
					new Object[] {}));
			assertTrue((Boolean) filterElement.getFunction().evaluate(
					"Hello World", "!"));
		}
	}

	/**
	 * Test method for
	 * {@link test.jamocha.engine.filter.FilterMockup#alwaysFalse()} .
	 */
	@Test
	public void testAlwaysFalse() {
		final Filter alwaysFalse = FilterMockup.alwaysFalse();
		for (final FilterElement filterElement : alwaysFalse
				.getFilterElements()) {
			assertFalse((Boolean) filterElement.getFunction().evaluate(1, 2, 3));
			assertFalse((Boolean) filterElement.getFunction().evaluate(
					(Object) null));
			assertFalse((Boolean) filterElement.getFunction().evaluate(
					new Object[] {}));
			assertFalse((Boolean) filterElement.getFunction().evaluate(
					"Hello World", "!"));
		}
	}
}
