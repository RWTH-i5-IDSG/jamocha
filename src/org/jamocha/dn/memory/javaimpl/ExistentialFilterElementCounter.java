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
import org.jamocha.filter.PathFilter.ExistentialPathFilterElement;
import org.jamocha.filter.PathFilter.NegatedExistentialPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.visitor.FilterElementVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ExistentialFilterElementCounter implements FilterElementVisitor {

	final List<Boolean> negated = new LinkedList<>();

	public boolean[] getNegated() {
		final int size = negated.size();
		final boolean[] array = new boolean[size];
		for (int i = 0; i < size; ++i) {
			array[i] = negated.get(i);
		}
		return array;
	}

	@Override
	public void visit(final AddressFilterElement fe) {
	}

	@Override
	public void visit(final ExistentialAddressFilterElement fe) {
		negated.add(false);
	}

	@Override
	public void visit(final NegatedExistentialAddressFilterElement fe) {
		negated.add(true);
	}

	@Override
	public void visit(final PathFilterElement fe) {
	}

	@Override
	public void visit(final ExistentialPathFilterElement fe) {
		negated.add(false);
	}

	@Override
	public void visit(final NegatedExistentialPathFilterElement fe) {
		negated.add(true);
	}
}
