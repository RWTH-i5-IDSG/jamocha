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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamocha.filter.PathFilter.DummyPathFilterElement;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;

/**
 * Collects all paths used within the filter.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <T>
 *            collection type to use while collecting the paths
 */
public class PathCollector<T extends Collection<Path>> implements PathFilterElementVisitor {
	private final T paths;

	public PathCollector(final T paths) {
		this.paths = paths;
	}

	public PathCollector<T> collect(final PathFilter filter) {
		this.paths.addAll(filter.getPositiveExistentialPaths());
		this.paths.addAll(filter.getNegativeExistentialPaths());
		for (final PathFilterElement filterElement : filter.getFilterElements()) {
			collect(filterElement);
		}
		return this;
	}

	@Override
	public void visit(final PathFilterElement fe) {
		fe.getFunction().accept(new PathCollectorInFWA());
	}

	@Override
	public void visit(final DummyPathFilterElement fe) {
		paths.addAll(Arrays.asList(fe.getPaths()));
	}

	public PathCollector<T> collect(final PathFilterElement filterElement) {
		filterElement.accept(this);
		return this;
	}

	public static PathCollector<HashSet<Path>> newHashSet() {
		return new PathCollector<HashSet<Path>>(new HashSet<Path>());
	}

	public static PathCollector<LinkedHashSet<Path>> newLinkedHashSet() {
		return new PathCollector<LinkedHashSet<Path>>(new LinkedHashSet<Path>());
	}

	public static PathCollector<ArrayList<Path>> newArrayList() {
		return new PathCollector<ArrayList<Path>>(new ArrayList<Path>());
	}

	public static PathCollector<LinkedList<Path>> newLinkedList() {
		return new PathCollector<LinkedList<Path>>(new LinkedList<Path>());
	}

	/**
	 * @return the paths
	 */
	public T getPaths() {
		return this.paths;
	}

	/**
	 * @return the addresses
	 */
	public Path[] getPathsArray() {
		return getPaths().toArray(new Path[getPaths().size()]);
	}

	class PathCollectorInFWA implements FunctionWithArgumentsVisitor {

		@Override
		public void visit(final ConstantLeaf constantLeaf) {
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite functionWithArgumentsComposite) {
			for (final FunctionWithArguments fwa : functionWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
			for (final FunctionWithArguments fwa : predicateWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final ParameterLeaf parameterLeaf) {
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			paths.add(pathLeaf.getPath());
		}

		@Override
		public void visit(final Assert fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final Modify fwa) {
			fwa.getTargetFact().accept(this);
			for (final SlotAndValue child : fwa.getArgs()) {
				child.getValue().accept(this);
			}
		}

		@Override
		public void visit(final Modify.SlotAndValue fwa) {
			fwa.getValue().accept(this);
		}

		@Override
		public void visit(final Retract fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final SymbolLeaf fwa) {
			throw new IllegalArgumentException(
					"There should not be SymbolLeafs in the argument to PathCollector!");
		}

		@Override
		public void visit(final Assert.TemplateContainer fwa) {
			for (final FunctionWithArguments child : fwa.getArgs()) {
				child.accept(this);
			}
		}
	}
}
