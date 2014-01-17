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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.PathLeaf.ParameterLeaf;

import test.jamocha.filter.PredicateWithArgumentsMockup;

public class PathCollector<T extends Collection<Path>> implements Visitor {

	private final T paths;

	public PathCollector(final T paths) {
		this.paths = paths;
	}

	public PathCollector<T> collect(final PathFilter filter) {
		for (final PathFilterElement filterElement : filter.getFilterElements()) {
			collect(filterElement);
		}
		return this;
	}

	public PathCollector<T> collect(final PathFilterElement filterElement) {
		filterElement.getFunction().accept(this);
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
		return paths;
	}

	/**
	 * @return the addresses
	 */
	public Path[] getPathsArray() {
		return getPaths().toArray(new Path[getPaths().size()]);
	}

	@Override
	public void visit(final ConstantLeaf constantLeaf) {
	}

	@Override
	public void visit(FunctionWithArgumentsComposite functionWithArgumentsComposite) {
		for (final FunctionWithArguments fwa : functionWithArgumentsComposite.args) {
			fwa.accept(this);
		}
	}

	@Override
	public void visit(PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
		for (final FunctionWithArguments fwa : predicateWithArgumentsComposite.args) {
			fwa.accept(this);
		}
	}

	@Override
	public void visit(final ParameterLeaf parameterLeaf) {
	}

	@Override
	public void visit(final PathLeaf pathLeaf) {
		this.getPaths().add(pathLeaf.getPath());
	}

	public void visit(final PredicateWithArgumentsMockup predicateWithArgumentsMockup) {
		for (Path path : predicateWithArgumentsMockup.getPaths()) {
			this.getPaths().add(path);
		}
	}

}
