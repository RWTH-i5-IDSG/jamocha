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

import java.util.ArrayList;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.nodes.SlotInFactAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.jamocha.util.TestData.SomeStuff;

/**
 * A mockup filter implementation for testing purposes.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FilterMockup extends Filter {

	@RequiredArgsConstructor
	public static class FunctionWithArgumentsMockup implements FunctionWithArguments {
		
		final private boolean returnValue;
		final private Path[] paths;
		
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
		public void gatherPaths(final Set<Path> p) {
			for (Path path : paths) {
				p.add(path);
			}
		}

		@Override
		public boolean equalsInFunction(Function function) {
			if (! (function instanceof FunctionWithArgumentsMockup))
				return false;
			FunctionWithArgumentsMockup fwam = (FunctionWithArgumentsMockup) function;
			return (fwam.returnValue == this.returnValue && fwam.paths.length == this.paths.length);
		}
	}

	public FilterMockup(final boolean returnValue, final Path... paths) {
		super(new FunctionWithArguments[] { new FunctionWithArgumentsMockup(returnValue, paths) });
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
	 * Test class for {@link test.jamocha.filter.FilterMockup} using Theories.
	 * 
	 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
	 * 
	 */
	@RunWith(Theories.class)
	public static class FilterMockupTest {
		/**
		 * Test method for {@link test.jamocha.filter.FilterMockup#alwaysTrue()}.
		 */
		@Theory
		public void testAlwaysTrue(@SomeStuff Object... obj) {
			final Filter alwaysTrue = FilterMockup.alwaysTrue();
			for (final FilterElement filterElement : alwaysTrue.getFilterElements()) {
				assertTrue((Boolean) filterElement.getFunction().evaluate(obj));
			}
		}

		/**
		 * Test method for {@link test.jamocha.filter.FilterMockup#alwaysFalse()} .
		 */
		@Theory
		public void testAlwaysFalse(@SomeStuff Object... obj) {
			final Filter alwaysFalse = FilterMockup.alwaysFalse();
			for (final FilterElement filterElement : alwaysFalse.getFilterElements()) {
				assertFalse((Boolean) filterElement.getFunction().evaluate(obj));
			}
		}
	}
}