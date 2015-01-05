/*
 * Copyright 2002-2008 The Jamocha Team
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

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.DefaultFunctionWithArgumentsLeafVisitor;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.FunctionWithArgumentsComposite;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.impls.predicates.And;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.function.impls.predicates.Not;
import org.jamocha.function.impls.predicates.Or;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Specificity implements DefaultFunctionWithArgumentsLeafVisitor<SymbolLeaf>,
		DefaultConditionalElementsVisitor {
	int specificity = 0;

	public static int calculate(final ConditionalElement ce) {
		final Specificity instance = new Specificity();
		ce.accept(instance);
		instance.specificity +=
				DeepFactVariableCollector
						.collect(ce)
						.stream()
						.flatMap(fv -> fv.getSlotVariables().stream())
						.map(sv -> sv.getEqual())
						.distinct()
						.mapToInt(
								ec -> ec.getEqualFWAs().size() + ec.getEqualSlotVariables().size()
										+ (ec.getFactVariables().isEmpty() ? 0 : 1) - 1).filter(i -> i > 0).sum();
		return instance.specificity;
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		ce.getChildren().forEach(child -> child.accept(this));
	}

	@Override
	public void visit(final TemplatePatternConditionalElement ce) {
		++specificity;
	}

	@Override
	public void visit(final ConstantLeaf<SymbolLeaf> constantLeaf) {
	}

	@Override
	public void visit(final GlobalVariableLeaf<SymbolLeaf> globalVariableLeaf) {
	}

	@Override
	public void visit(final SymbolLeaf leaf) {
	};

	@Override
	public void visit(final TestConditionalElement ce) {
		ce.getPredicateWithArguments().accept(this);
	}

	@Override
	public void visit(final PredicateWithArgumentsComposite<SymbolLeaf> fwa) {
		if (fwa.getFunction().inClips().equals(Equals.inClips)) {
			// ignore
		} else if (Arrays.asList(And.inClips, Or.inClips, Not.inClips).contains(fwa.getFunction().inClips())) {
			for (final FunctionWithArguments<SymbolLeaf> arg : fwa.getArgs()) {
				arg.accept(this);
			}
		} else {
			++specificity;
		}
	}

	@Override
	public void visit(final FunctionWithArgumentsComposite<SymbolLeaf> fwa) {
		++specificity;
	}
}
