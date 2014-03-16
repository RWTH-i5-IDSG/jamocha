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
package org.jamocha.dn.memory.javaimpl;

import java.util.ArrayList;

/**
 * Interface to easy code re-usage where the only difference was the comparison of fact tuples.
 * The parameters of equals can be seen as the union of the parameters needed for the three
 * implementations, thus some are only needed in special cases. <br />
 * Implementations
 * <ul>
 * <li><b>root:</b> checks for equal content in both facts (first element of fact tuple) and
 * replaces the negative fact with its corresponding original if matched to allow for
 * referential comparison in the rest of the network (for better performance)</li>
 * <li><b>alpha:</b> checks for referential equality of the first elements of the fact tuples</li>
 * <li><b>beta:</b> checks for referential equality of all elements of the fact tuples, uses
 * fact address translation to do so</li>
 * <li><b>equalRow:</b> checks for referential equality of the FactTuple objects</li>
 * <li><b>equalFactTuple:</b> checks for referential equality of the Fact[] object in the
 * FactTuples</li>
 * </ul>
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
interface EqualityChecker {
	boolean equals(final FactTuple originalFactTuple, final FactTuple minusFactTuple,
			final ArrayList<FactTuple> minusFacts, final int minusFactsIndex,
			final FactAddress[] factAddresses);

	static EqualityChecker root = new EqualityChecker() {
		@Override
		public boolean equals(final FactTuple originalFactTuple,
				final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
				final int minusFactsIndex, final FactAddress[] factAddresses) {
			final Fact originalFact = originalFactTuple.getFactTuple()[0];
			final Fact minusFact = minusFactTuple.getFactTuple()[0];
			if (Fact.equalContent(originalFact, minusFact)) {
				minusFacts.set(minusFactsIndex, originalFactTuple);
				return true;
			}
			return false;
		}
	};
	static EqualityChecker alpha = new EqualityChecker() {
		@Override
		public boolean equals(final FactTuple originalFactTuple,
				final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
				final int minusFactsIndex, final FactAddress[] factAddresses) {
			final Fact originalFact = originalFactTuple.getFactTuple()[0];
			final Fact minusFact = minusFactTuple.getFactTuple()[0];
			return minusFact == originalFact;
		}
	};
	static EqualityChecker beta = new EqualityChecker() {
		@Override
		public boolean equals(final FactTuple originalFactTuple,
				final FactTuple minusFactTuple, final ArrayList<FactTuple> minusFacts,
				final int minusFactsIndex, final FactAddress[] factAddresses) {
			for (int i = 0; i < factAddresses.length; ++i) {
				final int originalAddress = factAddresses[i].index;
				final int minusAddress = i;
				final Fact originalFact = originalFactTuple.getFactTuple()[originalAddress];
				final Fact minusFact = minusFactTuple.getFactTuple()[minusAddress];
				if (minusFact != originalFact) {
					return false;
				}
			}
			return true;
		}
	};
	static EqualityChecker equalRow = new EqualityChecker() {
		@Override
		public boolean equals(FactTuple originalFactTuple, FactTuple minusFactTuple,
				ArrayList<FactTuple> minusFacts, int minusFactsIndex,
				FactAddress[] factAddresses) {
			return originalFactTuple == minusFactTuple;
		}
	};
	static EqualityChecker equalFactTuple = new EqualityChecker() {
		@Override
		public boolean equals(FactTuple originalFactTuple, FactTuple minusFactTuple,
				ArrayList<FactTuple> minusFacts, int minusFactsIndex,
				FactAddress[] factAddresses) {
			return originalFactTuple.getFactTuple() == minusFactTuple.getFactTuple();
		}
	};
}