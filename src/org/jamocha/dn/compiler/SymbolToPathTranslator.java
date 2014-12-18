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
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.FunctionWithArgumentsVisitor;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class SymbolToPathTranslator implements FunctionWithArgumentsVisitor<SymbolLeaf> {

	@Getter
	private FunctionWithArguments<PathLeaf> result;
	private final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf;

	public static FunctionWithArguments<PathLeaf> translate(final FunctionWithArguments<SymbolLeaf> toTranslate,
			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf) {
		return toTranslate.accept(new SymbolToPathTranslator(equivalenceClassToPathLeaf)).result;
	}

	public static PredicateWithArgumentsComposite<PathLeaf> translate(
			final PredicateWithArgumentsComposite<SymbolLeaf> toTranslate,
			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf) {
		return (PredicateWithArgumentsComposite<PathLeaf>) toTranslate.accept(new SymbolToPathTranslator(
				equivalenceClassToPathLeaf)).result;
	}

	public static PredicateWithArguments<PathLeaf> translate(final PredicateWithArguments<SymbolLeaf> toTranslate,
			final Map<EquivalenceClass, PathLeaf> equivalenceClassToPathLeaf) {
		return (PredicateWithArguments<PathLeaf>) toTranslate.accept(new SymbolToPathTranslator(
				equivalenceClassToPathLeaf)).result;
	}

	@SuppressWarnings("unchecked")
	private void handleArgs(final FunctionWithArguments<SymbolLeaf> fwa, final FunctionWithArguments<?>[] args) {
		for (int i = 0; i < args.length; ++i) {
			args[i] = translate((FunctionWithArguments<SymbolLeaf>) args[i], equivalenceClassToPathLeaf);
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

	@SuppressWarnings("unchecked")
	@Override
	public void visit(GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
		result = (FunctionWithArguments<PathLeaf>) (FunctionWithArguments<?>) globalVariableLeaf;
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
		this.result =
				new Modify.SlotAndValue<PathLeaf>(fwa.getSlotName(), translate(fwa.getValue(),
						equivalenceClassToPathLeaf));
	}

	@Override
	public void visit(final SymbolLeaf fwa) {
		result = equivalenceClassToPathLeaf.get(fwa.getSymbol().getEqual());
	}
}