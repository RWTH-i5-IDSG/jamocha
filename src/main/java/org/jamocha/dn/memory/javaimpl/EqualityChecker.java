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
package org.jamocha.dn.memory.javaimpl;

/**
 * Interface to easy code re-usage where the only difference was the comparison of fact tuples. The parameters of equals
 * can be seen as the union of the parameters needed for the three implementations, thus some are only needed in special
 * cases. <br /> Implementations <ul> <li><b>ROOT:</b> checks for equal content in both facts (first element of fact
 * tuple) and replaces the negative fact with its corresponding original if matched to allow for referential comparison
 * in the rest of the network (for better performance)</li> <li><b>ALPHA:</b> checks for referential equality of the
 * first elements of the fact tuples</li> <li><b>BETA:</b> checks for referential equality of all elements of the fact
 * tuples, uses fact address translation to do so, assumes equality for null entries in the fact address array</li>
 * <li><b>EQUAL_ROW:</b> checks for referential equality of the Row objects</li> <li><b>EQUAL_FACT_TUPLE:</b> checks for
 * referential equality of the Fact[] object in the FactTuples </li> </ul>
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
interface EqualityChecker {
    boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
            final int minusRowsIndex, final FactAddress[] factAddresses);

    EqualityChecker ROOT = new EqualityChecker() {
        @Override
        public boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
                final int minusRowsIndex, final FactAddress[] factAddresses) {
            final Fact originalFact = originalRow.getFactTuple()[0];
            final Fact minusFact = minusRow.getFactTuple()[0];
            if (Fact.equalContent(originalFact, minusFact)) {
                minusRows.set(minusRowsIndex, originalRow);
                return true;
            }
            return false;
        }
    };
    EqualityChecker ALPHA = new EqualityChecker() {
        @Override
        public boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
                final int minusRowsIndex, final FactAddress[] factAddresses) {
            final Fact originalFact = originalRow.getFactTuple()[0];
            final Fact minusFact = minusRow.getFactTuple()[0];
            return minusFact == originalFact;
        }
    };
    EqualityChecker BETA = new EqualityChecker() {
        @Override
        public boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
                final int minusRowsIndex, final FactAddress[] factAddresses) {
            for (int i = 0; i < factAddresses.length; ++i) {
                final FactAddress originalFactAddress = factAddresses[i];
                if (null == originalFactAddress) continue;
                final int originalIndex = originalFactAddress.index;
                final int minusIndex = i;
                final Fact originalFact = originalRow.getFactTuple()[originalIndex];
                final Fact minusFact = minusRow.getFactTuple()[minusIndex];
                if (minusFact != originalFact) {
                    return false;
                }
            }
            return true;
        }
    };
    EqualityChecker EQUAL_ROW = new EqualityChecker() {
        @Override
        public boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
                final int minusRowsIndex, final FactAddress[] factAddresses) {
            return originalRow == minusRow;
        }
    };
    EqualityChecker EQUAL_FACT_TUPLE = new EqualityChecker() {
        @Override
        public boolean equals(final Row originalRow, final Row minusRow, final JamochaArray<Row> minusRows,
                final int minusRowsIndex, final FactAddress[] factAddresses) {
            return originalRow.getFactTuple() == minusRow.getFactTuple();
        }
    };
}
