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
import org.jamocha.filter.visitor.FilterElementVisitor;

/**
 * This class provides three FilterElement types:
 * <ul>
 * <li><b>AddressFilterElement:</b> class for regular filter elements</li>
 * <li><b>ExistentialAddressFilterElement:</b> class for existential filter elements, i.e. filter
 * elements using the <code>exists</code> keyword</li>
 * <li><b>NegatedExistentialAddressFilterElement:</b> class for negated existential filter elements,
 * i.e. filter elements using the <code>not</code> keyword</li>
 * </ul>
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class AddressFilter extends Filter<AddressFilter.AddressFilterElement> {

	public static AddressFilter empty = new AddressFilter(new AddressFilterElement[] {});

	public AddressFilter(final AddressFilterElement[] filterElements) {
		super(filterElements);
	}

	@Getter
	public static class AddressFilterElement extends Filter.FilterElement {
		final SlotInFactAddress addressesInTarget[];

		public AddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget) {
			super(function);
			this.addressesInTarget = addressesInTarget;
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	@Getter
	private static abstract class AbstractExistentialAddressFilterElement extends
			AddressFilterElement {
		final FactAddress existentialAddressInTarget;

		public AbstractExistentialAddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget,
				final FactAddress existentialAddressInTarget) {
			super(function, addressesInTarget);
			this.existentialAddressInTarget = existentialAddressInTarget;
		}
	}

	public static class ExistentialAddressFilterElement extends
			AbstractExistentialAddressFilterElement {
		public ExistentialAddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget,
				final FactAddress existentialAddressInTarget) {
			super(function, addressesInTarget, existentialAddressInTarget);
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}

	public static class NegatedExistentialAddressFilterElement extends
			AbstractExistentialAddressFilterElement {
		public NegatedExistentialAddressFilterElement(final PredicateWithArguments function,
				final SlotInFactAddress[] addressesInTarget,
				final FactAddress existentialAddressInTarget) {
			super(function, addressesInTarget, existentialAddressInTarget);
		}

		@Override
		public <V extends FilterElementVisitor> V accept(final V visitor) {
			visitor.visit(this);
			return visitor;
		}
	}
}
