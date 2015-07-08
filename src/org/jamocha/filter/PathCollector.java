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

import static org.jamocha.util.ToArray.toArray;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.jamocha.filter.PathFilterSet.PathExistentialSet;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Bind;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;

/**
 * Collects all paths used within the filter.
 *
 * @param <T>
 *            collection type to use while collecting the paths
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathCollector<T extends Collection<Path>> implements PathFilterSetVisitor {
	private final T paths;

	public PathCollector(final T paths) {
		this.paths = paths;
	}

	public PathCollector<T> collectAll(final PathNodeFilterSet filterSet) {
		this.paths.addAll(filterSet.getPositiveExistentialPaths());
		this.paths.addAll(filterSet.getNegativeExistentialPaths());
		return collectOnlyInFilters(filterSet);
	}

	public PathCollector<T> collectAll(final Iterable<PathNodeFilterSet> filterSets) {
		for (final PathNodeFilterSet filterSet : filterSets) {
			collectAll(filterSet);
		}
		return this;
	}

	public PathCollector<T> collectOnlyInFilters(final PathNodeFilterSet filterSet) {
		for (final PathFilter filter : filterSet.getFilters()) {
			collect(filter);
		}
		return this;
	}

	public PathCollector<T> collectOnlyInFilters(final Iterable<PathNodeFilterSet> filterSet) {
		for (final PathNodeFilterSet filter : filterSet) {
			collectOnlyInFilters(filter);
		}
		return this;
	}

	public PathCollector<T> collectOnlyNonExistential(final PathFilterList filterSet) {
		for (final PathNodeFilterSet filter : filterSet) {
			collectOnlyInFilters(filter);
			this.paths.removeAll(filter.getPositiveExistentialPaths());
			this.paths.removeAll(filter.getNegativeExistentialPaths());
		}
		return this;
	}

	@Override
	public void visit(final PathFilter f) {
		f.getFunction().accept(new PathCollectorInFWA());
	}

	@Override
	public void visit(final PathExistentialSet set) {

	}

	public PathCollector<T> collect(final PathFilter filter) {
		filter.accept(this);
		return this;
	}

	public PathCollector<T> collect(final FunctionWithArguments<PathLeaf> fwa) {
		fwa.accept(new PathCollectorInFWA());
		return this;
	}

	public static PathCollector<HashSet<Path>> newHashSet() {
		return new PathCollector<>(new HashSet<>());
	}

	public static PathCollector<LinkedHashSet<Path>> newLinkedHashSet() {
		return new PathCollector<>(new LinkedHashSet<>());
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
		return toArray(getPaths(), Path[]::new);
	}

	class PathCollectorInFWA implements FunctionWithArgumentsVisitor<PathLeaf> {

		@Override
		public void visit(final ConstantLeaf<PathLeaf> constantLeaf) {
		}

		@Override
		public void visit(final GlobalVariableLeaf<PathLeaf> globalVariableLeaf) {
		}

		@Override
		public void visit(final FunctionWithArgumentsComposite<PathLeaf> functionWithArgumentsComposite) {
			for (final FunctionWithArguments<PathLeaf> fwa : functionWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final PredicateWithArgumentsComposite<PathLeaf> predicateWithArgumentsComposite) {
			for (final FunctionWithArguments<PathLeaf> fwa : predicateWithArgumentsComposite.getArgs()) {
				fwa.accept(this);
			}
		}

		@Override
		public void visit(final PathLeaf pathLeaf) {
			paths.add(pathLeaf.getPath());
		}

		@Override
		public void visit(final Bind<PathLeaf> fwa) {
			for (final FunctionWithArguments<PathLeaf> child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final Assert<PathLeaf> fwa) {
			for (final FunctionWithArguments<PathLeaf> child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final Modify<PathLeaf> fwa) {
			fwa.getTargetFact().accept(this);
			for (final SlotAndValue<PathLeaf> child : fwa.getArgs()) {
				child.getValue().accept(this);
			}
		}

		@Override
		public void visit(final Modify.SlotAndValue<PathLeaf> fwa) {
			fwa.getValue().accept(this);
		}

		@Override
		public void visit(final Retract<PathLeaf> fwa) {
			for (final FunctionWithArguments<PathLeaf> child : fwa.getArgs()) {
				child.accept(this);
			}
		}

		@Override
		public void visit(final Assert.TemplateContainer<PathLeaf> fwa) {
			for (final FunctionWithArguments<PathLeaf> child : fwa.getArgs()) {
				child.accept(this);
			}
		}
	}
}
