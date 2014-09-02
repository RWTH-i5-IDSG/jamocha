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
package org.jamocha.dn.compiler;

import java.util.Map;

import lombok.Getter;

import org.jamocha.filter.Path;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.Modify.SlotAndValue;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class SymbolToPathTranslator implements FunctionWithArgumentsVisitor {

	@Getter
	private FunctionWithArguments result;
	private final Map<SingleFactVariable, Path> paths;

	public SymbolToPathTranslator(final Map<SingleFactVariable, Path> paths) {
		this.paths = paths;
	}

	public static PredicateWithArguments translate(final PredicateWithArguments toTranslate,
			final Map<SingleFactVariable, Path> paths) {
		toTranslate.accept(new SymbolToPathTranslator(paths));
		return toTranslate;
	}

	private void handleGWAC(final GenericWithArgumentsComposite<?, ?> gwac) {
		FunctionWithArguments[] args = gwac.getArgs();
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].accept(new SymbolToPathTranslator(paths)).getResult();
		}
		this.result = gwac;
	}

	@Override
	public void visit(FunctionWithArgumentsComposite functionWithArgumentsComposite) {
		handleGWAC(functionWithArgumentsComposite);
	}

	@Override
	public void visit(PredicateWithArgumentsComposite predicateWithArgumentsComposite) {
		handleGWAC(predicateWithArgumentsComposite);
	}

	@Override
	public void visit(ConstantLeaf constantLeaf) {
		result = constantLeaf;
	}

	@Override
	public void visit(ParameterLeaf parameterLeaf) {
		throw new Error("ParameterLeaf should not exists at this stage");
	}

	@Override
	public void visit(PathLeaf pathLeaf) {
		throw new Error("PathLeaf should not exists at this stage");
	}

	@Override
	public void visit(Assert fwa) {
		throw new Error("Assert in predicate");
	}

	@Override
	public void visit(TemplateContainer fwa) {
		throw new Error("TemplateContainer should not exists at this stage");
	}

	@Override
	public void visit(Retract fwa) {
		throw new Error("Retract in predicate");
	}

	@Override
	public void visit(Modify fwa) {
		throw new Error("Modify in predicate");
	}

	@Override
	public void visit(SlotAndValue fwa) {
		throw new Error("SlotAndValue in predicate");
	}

	@Override
	public void visit(SymbolLeaf fwa) {
		assert (fwa.getSymbol().getPositiveSlotVariables().size() > 0);
		final SingleSlotVariable variable = fwa.getSymbol().getPositiveSlotVariables().get(0);
		final Path path = this.paths.get(variable.getFactVariable());
		result = new PathLeaf(path, variable.getSlot());
	}
}