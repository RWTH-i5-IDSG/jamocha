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
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PathLeaf.ParameterLeaf;
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

	@SuppressWarnings("unchecked")
	public static <T extends FunctionWithArguments> T translate(final T toTranslate,
			final Map<SingleFactVariable, Path> paths) {
		return (T) toTranslate.accept(new SymbolToPathTranslator(paths)).result;
	}

	private <T extends FunctionWithArguments> void handleArgs(final FunctionWithArguments fwa, final T[] args) {
		for (int i = 0; i < args.length; ++i) {
			args[i] = translate(args[i], paths);
		}
		this.result = fwa;
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final ConstantLeaf constantLeaf) {
		result = constantLeaf;
	}

	@Override
	public void visit(final ParameterLeaf parameterLeaf) {
		throw new Error("ParameterLeaf should not exists at this stage");
	}

	@Override
	public void visit(final PathLeaf pathLeaf) {
		// FIXME can we ignore this because of shared nodes?
		this.result = pathLeaf;
		// throw new Error("PathLeaf should not exists at this stage");
	}

	@Override
	public void visit(final Assert fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Assert.TemplateContainer fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Retract fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Modify fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Modify.SlotAndValue fwa) {
		this.result = new Modify.SlotAndValue(fwa.getSlotName(), translate(fwa.getValue(), paths));
	}

	@Override
	public void visit(final SymbolLeaf fwa) {
		assert (fwa.getSymbol().getPositiveSlotVariables().size() > 0);
		final SingleSlotVariable variable = fwa.getSymbol().getPositiveSlotVariables().get(0);
		final Path path = this.paths.get(variable.getFactVariable());
		result = new PathLeaf(path, variable.getSlot());
	}
}