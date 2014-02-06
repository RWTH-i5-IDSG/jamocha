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

import org.jamocha.filter.AddressFilter;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.visitor.FilterTranslator;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import test.jamocha.util.TestData.SomeStuff;

/**
 * A mockup filter implementation for testing purposes.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FilterMockup extends PathFilter {

	public FilterMockup(final boolean returnValue, final Path... paths) {
		super(new PathFilterElement(new PredicateWithArgumentsMockup(returnValue, paths)));
	}

	public static FilterMockup alwaysTrue(final Path... paths) {
		return new FilterMockup(true, paths);
	}

	public static FilterMockup alwaysFalse(final Path... paths) {
		return new FilterMockup(false, paths);
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
			final AddressFilter alwaysTrue = FilterTranslator.translate(FilterMockup.alwaysTrue());
			for (final FilterElement filterElement : alwaysTrue.getFilterElements()) {
				assertTrue(filterElement.getFunction().evaluate(obj));
			}
		}

		/**
		 * Test method for {@link test.jamocha.filter.FilterMockup#alwaysFalse()} .
		 */
		@Theory
		public void testAlwaysFalse(@SomeStuff Object... obj) {
			final AddressFilter alwaysFalse =
					FilterTranslator.translate(FilterMockup.alwaysFalse());
			for (final FilterElement filterElement : alwaysFalse.getFilterElements()) {
				assertFalse(filterElement.getFunction().evaluate(obj));
			}
		}
	}
}
