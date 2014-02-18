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

import java.util.LinkedList;
import java.util.List;

import org.jamocha.filter.AddressFilter.AddressFilterElement;
import org.jamocha.filter.AddressFilter.ExistentialAddressFilterElement;
import org.jamocha.filter.AddressFilter.NegatedExistentialAddressFilterElement;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Filter.FilterElement;
import org.jamocha.filter.PathFilter.ExistentialPathFilterElement;
import org.jamocha.filter.PathFilter.NegatedExistentialPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.visitor.FilterElementVisitor;

/**
 * Visitor for {@link org.jamocha.filter.Filter.FilterElement FilterElements} to determine how many
 * of them are existential filter elements and whether they are negated.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ExistentialFilterElementCounter implements FilterElementVisitor {

	final List<Boolean> negated = new LinkedList<>();

	public static boolean[] getNegatedArrayFromFilter(final Filter<? extends FilterElement> filter) {
		final ExistentialFilterElementCounter fevisitor = new ExistentialFilterElementCounter();
		for (final FilterElement fe : filter.getFilterElements()) {
			fe.accept(fevisitor);
		}
		return fevisitor.getNegated();
	}

	public boolean[] getNegated() {
		final int size = this.negated.size();
		final boolean[] array = new boolean[size];
		for (int i = 0; i < size; ++i) {
			array[i] = this.negated.get(i);
		}
		return array;
	}

	@Override
	public void visit(final AddressFilterElement fe) {
	}

	@Override
	public void visit(final ExistentialAddressFilterElement fe) {
		this.negated.add(false);
	}

	@Override
	public void visit(final NegatedExistentialAddressFilterElement fe) {
		this.negated.add(true);
	}

	@Override
	public void visit(final PathFilterElement fe) {
	}

	@Override
	public void visit(final ExistentialPathFilterElement fe) {
		this.negated.add(false);
	}

	@Override
	public void visit(final NegatedExistentialPathFilterElement fe) {
		this.negated.add(true);
	}
}
