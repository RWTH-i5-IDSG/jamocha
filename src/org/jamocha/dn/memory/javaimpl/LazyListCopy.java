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

import lombok.AllArgsConstructor;
import lombok.Delegate;
import lombok.Getter;

/**
 * Tiny state pattern implementation for the following use case: <br />
 * A filter is applied to elements in a list and we want to get a list of the elements that matched
 * the filter. Thus, if the filter matches all elements, the original list may be returned.
 * Otherwise we need to copy parts of it. <br />
 * To achieve this, a reference to that list is stored in the start-state (SameList) and keep is
 * called as long as the entries match the filter. As soon as the first element not matching the
 * filter leads to a drop-call, the list is copied up to (but not including) the current position
 * and the state is changed to CopiedList. Now, all further drop calls are ignored and all
 * keep-calls have the corresponding element copied into the new list. There is no further state
 * change.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class LazyListCopy {
	private static interface LazyListCopyState {
		void keep(final int index);

		void keepStartingAt(final int index);

		void drop(final int index);

		ArrayList<Row> getList();
	}

	@Delegate
	LazyListCopyState state;

	public LazyListCopy(final ArrayList<Row> list) {
		this.state = new SameList(list);
	}

	/**
	 * @see LazyListCopy
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@AllArgsConstructor
	public class SameList implements LazyListCopyState {
		@Getter(onMethod = @_(@Override))
		final ArrayList<Row> list;

		@Override
		public void drop(final int index) {
			final int size = this.list.size();
			final ArrayList<Row> copy = new ArrayList<>(this.list);
			copy.subList(index, size).clear();
			// TODO write your own arraylist to do new ArrayList<>(original, from, to);
			// final ArrayList<Fact[]> copy = new ArrayList<>(this.list.size());
			// for (int i = 0; i < index; i++) {
			// copy.add(this.list.get(i));
			// }
			LazyListCopy.this.state = new CopiedList(this.list, copy);
		}

		@Override
		public void keep(final int index) {
		}

		@Override
		public void keepStartingAt(final int index) {
		}
	}

	/**
	 * @see LazyListCopy
	 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
	 */
	@AllArgsConstructor
	public class CopiedList implements LazyListCopyState {
		final ArrayList<Row> list;
		final ArrayList<Row> copy;

		@Override
		public void keep(final int index) {
			this.copy.add(this.list.get(index));
		}

		@Override
		public void keepStartingAt(final int index) {
			final int size = this.list.size();
			for (int i = index; i < size; ++i) {
				this.copy.add(this.list.get(i));
			}
		}

		@Override
		public void drop(final int index) {
		}

		@Override
		public ArrayList<Row> getList() {
			return this.copy;
		}
	}

}