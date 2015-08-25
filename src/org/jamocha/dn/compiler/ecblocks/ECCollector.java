/*
 * Copyright 2002-2015 The Jamocha Team
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
package org.jamocha.dn.compiler.ecblocks;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.jamocha.filter.ECFilter;
import org.jamocha.filter.ECFilterList;
import org.jamocha.filter.ECFilterList.ECExistentialList;
import org.jamocha.filter.ECFilterList.ECNodeFilterSet;
import org.jamocha.filter.ECFilterList.ECSharedListWrapper.ECSharedList;
import org.jamocha.filter.ECFilterListVisitor;
import org.jamocha.filter.ECFilterSet;
import org.jamocha.filter.ECFilterSet.ECExistentialSet;
import org.jamocha.filter.ECFilterSetVisitor;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ECCollector implements DefaultFunctionWithArgumentsLeafVisitor<ECLeaf>, ECFilterListVisitor,
		ECFilterSetVisitor {
	@Getter
	final Set<EquivalenceClass> equivalenceClasses = new HashSet<>();

	static Set<EquivalenceClass> collect(final ECFilterList filter) {
		return filter.accept(new ECCollector()).equivalenceClasses;
	}

	static Set<EquivalenceClass> collect(final ECFilterSet set) {
		return set.accept(new ECCollector()).equivalenceClasses;
	}

	static Set<EquivalenceClass> collect(final ECFilter filter) {
		return collect(filter.getFunction());
	}

	static Set<EquivalenceClass> collect(final FunctionWithArguments<ECLeaf> fwa) {
		return fwa.accept(new ECCollector()).equivalenceClasses;
	}

	@Override
	public void visit(final ECLeaf leaf) {
		this.equivalenceClasses.add(leaf.getEc());
	}

	@Override
	public void visit(final ConstantLeaf<ECLeaf> constantLeaf) {
	}

	@Override
	public void visit(final GlobalVariableLeaf<ECLeaf> globalVariableLeaf) {
	}

	@Override
	public void visit(final ECExistentialList list) {
		list.getPurePart().accept(this);
		list.getExistentialClosure().accept(this);
	}

	@Override
	public void visit(final ECNodeFilterSet list) {
		list.getFilters().forEach(f -> f.getFunction().accept(this));
	}

	@Override
	public void visit(final ECExistentialSet set) {
		set.getPurePart().forEach(s -> s.accept(this));
		set.getExistentialClosure().accept(this);
	}

	@Override
	public void visit(final ECFilter list) {
		list.getFunction().accept(this);
	}

	@Override
	public void visit(final ECSharedList list) {
		list.getFilters().forEach(f -> f.accept(this));
	}
}
