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
import java.util.Optional;

import org.jamocha.dn.nodes.Edge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class Test {

	/* * * * * * * * * * MAIN MEMORIES * * * * * * * * * */

	static class MainMemory {
		private ArrayList<Row> rows;

		// joins use unfiltered rows when joining existential facts with main memory or doing
		// deletes
		public ArrayList<Row> getRows() {
			return rows;
		}

		// joins use filtered rows when accessing parent data via edges
		public ArrayList<Row> getValidRows() {
			return rows;
		}

		public void deleteInFiltered(@SuppressWarnings("unused") final ArrayList<Row> toDelete) {
		}
	}

	static class MainMemoryWithExistentials extends MainMemory {
		ArrayList<Row> validRows;

		@Override
		public ArrayList<Row> getValidRows() {
			return validRows;
		}

		@Override
		public void deleteInFiltered(final ArrayList<Row> toDelete) {
			final int mainSize = validRows.size();
			final int partSize = toDelete.size();
			final LazyListCopy mainCopy = new LazyListCopy(validRows);
			outerLoop: for (int mainIndex = 0; mainIndex < mainSize; ++mainIndex) {
				final Row mainTuple = validRows.get(mainIndex);
				for (int partIndex = 0; partIndex < partSize; ++partIndex) {
					final Row partTuple = toDelete.get(partIndex);
					if (mainTuple == partTuple) {
						mainCopy.drop(mainIndex);
						continue outerLoop;
					}
				}
				// facts differ at some point, add to remaining facts
				mainCopy.keep(mainIndex);
			}
			this.validRows = mainCopy.getList();
		}
	}

	/* * * * * * * * * * PSEUDO TEMP MEMORY * * * * * * * * * */

	static class TempPairDistributer {
		MemoryHandlerPlusTemp plus;
		MemoryHandlerMinusTemp minus;

		public void enqueueInEdges(final Edge edge) {
			edge.enqueueMemory(plus);
			edge.enqueueMemory(minus);
		}

		void releaseLock() {
			// never called, do nothing
		}
	}

	/* * * * * * * * * * PLUS TEMP MEMORIES * * * * * * * * * */

	static class PlusTempNewRowsAndCounterUpdates {
		static class Data {
			ArrayList<CounterUpdate> counterUpdates;
			ArrayList<Row> newRows;
			ArrayList<Row> newValidRows; // subseteq newRows
		}

		Data original;
		// the filtered part is only there for the following:
		// minus temps are processed directly and thus have to delete the corresponding rows also in
		// pending plus temps, that would otherwise add the fact to its parent despite its removal.
		// the follow-up network should still use the original rows, because access to filtered is
		// not synchronized
		Optional<Data> filtered;

		// newValidRows is the part of the token relevant for the follow-up network
		public ArrayList<Row> getRowsForSucessorNodes() {
			return original.newValidRows;
		}

		void releaseLock() {
			final Data source = filtered.orElse(original);
			// add source.newRows to main.unfiltered
			// add source.newValidRows to main.filtered
			// apply counterUpdates
			// -> create -token for invalidated rows
			// -> create +token for validated rows: PlusTempValidRowsAdder(+diff)
		}
	}

	static class PlusTempValidRowsAdder {
		ArrayList<Row> newValidRows;
		Optional<ArrayList<Row>> filteredNewValidRows;

		public ArrayList<Row> getRowsForSucessorNodes() {
			return newValidRows;
		}

		void releaseLock() {
			// add newValidRows to main.filtered
		}
	}

	/* * * * * * * * * * MINUS TEMP MEMORIES * * * * * * * * * */

	/*
	 * regular main: partials and completes entering produce a partial leaving (possibly blown up
	 * for existentials)
	 * 
	 * existential main: if -temp enters via edge not containing existential, we can create a
	 * partial -temp as in the regular case, as the filtered and unfiltered rows are directly
	 * removed and only the filtered version has to be passed downwards
	 * 
	 * otherwise we create a MinusTempExistential bearing the (possibly blown up) partial temp
	 * deleting the valid subset of the rows deleted in the main and the counter updates
	 */

	// usually used
	static class MinusTempRegularPartial {
		ArrayList<Row> partial;
	}

	// generated instead of MinusTempRegularPartial if one of the target edges contains existentials
	// also produced by counter updates (the complete part equals the partial part in these cases)
	static class MinusTempRegularComplete extends MinusTempRegularPartial {
		ArrayList<Row> complete;
	}

	static class MinusTempExistential {
		ArrayList<CounterUpdate> counterUpdates;
		MinusTempRegularPartial partial; // w/ complete if needed

		void releaseLock() {
			// apply counterUpdates
			// -> create -token for invalidated rows: MinusTempRegularComplete (-diff)
			// -> create +token for validated rows: PlusTempValidRowsAdder(+diff)
		}
	}
}
