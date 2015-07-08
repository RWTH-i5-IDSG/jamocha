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
 * Collects all PathLeafs used within the FunctionWithArguments in order of occurrence.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class PathLeafCollector implements FunctionWithArgumentsVisitor<PathLeaf> {
	private final ArrayList<PathLeaf> pathLeafs = new ArrayList<>();

	public static ArrayList<PathLeaf> collect(final FunctionWithArguments<PathLeaf> fwa) {
		final PathLeafCollector instance = new PathLeafCollector();
		fwa.accept(instance);
		return instance.pathLeafs;
	}

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
		pathLeafs.add(pathLeaf);
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
