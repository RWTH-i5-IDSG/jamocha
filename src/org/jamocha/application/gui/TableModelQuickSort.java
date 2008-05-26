/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.application.gui;

/**
 * This is a {@link TableRowModel} sorter. As most TableModels have their own
 * way to hold the data, this is the right way to avoid inconsistencies.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class TableModelQuickSort {

	private boolean ascending;

	private TableRowModel model;

	private int column;

	@SuppressWarnings("unchecked")
	private Class clazz;

	/**
	 * This is the static sorting method.
	 * 
	 * @param model
	 *            The Model whose data should be sorted.
	 * @param ascending
	 *            If <code>true</code> we sort ascending, otherwise
	 *            descending.
	 * @param column
	 *            Index of the column used for sorting.
	 */
	public static void sort(final TableRowModel model, final boolean ascending,
			final int column) {
		final TableModelQuickSort quick = new TableModelQuickSort();
		quick.model = model;
		quick.ascending = ascending;
		quick.column = column;
		quick.clazz = model.getColumnClass(column);
		quick.quicksort(0, model.getRowCount() - 1);
		model.fireTableDataChanged();
		return;
	}

	private void quicksort(final int p, final int r) {
		if (p < r) {
			int q = partition(p, r);
			if (q == r) {
				q--;
			}
			quicksort(p, q);
			quicksort(q + 1, r);
		}
	}

	private int partition(int lo, int hi) {
		final Object pivot = model.getValueAt(lo, column);
		while (true) {
			while (compare(model.getValueAt(hi, column), pivot) >= 0 && lo < hi) {
				hi--;
			}
			while (compare(model.getValueAt(lo, column), pivot) < 0 && lo < hi) {
				lo++;
			}
			if (lo < hi) {
				final Object temp = model.getRowAt(lo);
				model.setRowAt(model.getRowAt(hi), lo);
				model.setRowAt(temp, hi);
			} else {
				return hi;
			}
		}
	}

	/**
	 * Compares to Objects. We can only compare Strings, Longs and Integers at
	 * the moment.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private long compare(final Object a, final Object b) {
		long res = 0;
		if (clazz.equals(java.lang.Integer.class)) {
			res = ((Integer) a).intValue() - ((Integer) b).intValue();
		} else if (clazz.equals(java.lang.Long.class)) {
			res = ((Long) a).longValue() - ((Long) b).longValue();
		} else if (clazz.equals(java.lang.String.class)) {
			res = ((String) a).compareToIgnoreCase((String) b);
		}
		if (!ascending) {
			res *= -1;
		}
		return res;
	}
}