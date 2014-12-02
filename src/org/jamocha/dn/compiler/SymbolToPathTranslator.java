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
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SingleFactVariable.SingleSlotVariable;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class SymbolToPathTranslator implements FunctionWithArgumentsVisitor<SymbolLeaf> {

	@Getter
	private FunctionWithArguments<PathLeaf> result;
	private final Map<SingleFactVariable, Path> paths;

	public SymbolToPathTranslator(final Map<SingleFactVariable, Path> paths) {
		this.paths = paths;
	}

	public static FunctionWithArguments<PathLeaf> translate(final FunctionWithArguments<SymbolLeaf> toTranslate,
			final Map<SingleFactVariable, Path> paths) {
		return toTranslate.accept(new SymbolToPathTranslator(paths)).result;
	}

	public static PredicateWithArgumentsComposite<PathLeaf> translate(
			final PredicateWithArgumentsComposite<SymbolLeaf> toTranslate, final Map<SingleFactVariable, Path> paths) {
		return (PredicateWithArgumentsComposite<PathLeaf>) toTranslate.accept(new SymbolToPathTranslator(paths)).result;
	}

	public static PredicateWithArguments<PathLeaf> translate(final PredicateWithArguments<SymbolLeaf> toTranslate,
			final Map<SingleFactVariable, Path> paths) {
		return (PredicateWithArguments<PathLeaf>) toTranslate.accept(new SymbolToPathTranslator(paths)).result;
	}

	@SuppressWarnings("unchecked")
	private void handleArgs(final FunctionWithArguments<SymbolLeaf> fwa, final FunctionWithArguments<?>[] args) {
		for (int i = 0; i < args.length; ++i) {
			args[i] = translate((FunctionWithArguments<SymbolLeaf>) args[i], paths);
		}
		this.result = (FunctionWithArguments<PathLeaf>) (FunctionWithArguments<?>) fwa;
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
		result = (FunctionWithArguments<PathLeaf>) (FunctionWithArguments<?>) constantLeaf;
	}

	@Override
	public void visit(final Assert<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Assert.TemplateContainer<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Retract<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Modify<SymbolLeaf> fwa) {
		handleArgs(fwa, fwa.getArgs());
	}

	@Override
	public void visit(final Modify.SlotAndValue<SymbolLeaf> fwa) {
		this.result = new Modify.SlotAndValue<PathLeaf>(fwa.getSlotName(), translate(fwa.getValue(), paths));
	}

	@Override
	public void visit(final SymbolLeaf fwa) {
		assert (fwa.getSymbol().getPositiveSlotVariables().size() > 0);
		final SingleSlotVariable variable = fwa.getSymbol().getPositiveSlotVariables().get(0);
		final Path path = this.paths.get(variable.getFactVariable());
		assert null != path;
		result = new PathLeaf(path, variable.getSlot());
	}
}