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

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Path;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;
import org.jamocha.languages.common.SingleFactVariable;

import lombok.Getter;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */
public class ShallowFactVariableCollector implements DefaultConditionalElementsVisitor {

	@Getter
	private List<SingleFactVariable> factVariables;

	public static Pair<Path, Map<EquivalenceClass, Path>> generatePaths(final Template initialFactTemplate,
			final ConditionalElement ce) {
		final ShallowFactVariableCollector instance = new ShallowFactVariableCollector();
		// Collect all FactVariables defined in the CEs TemplateCEs and InitialFactCEs
		final List<SingleFactVariable> factVariables = ce.accept(instance).getFactVariables();
		// if there is an initial fact, the path to be used may not be null
		final Path initialFactPath = new Path(initialFactTemplate);
		assert!factVariables.stream().anyMatch(sfv -> sfv.getTemplate() == initialFactTemplate)
				|| null != initialFactPath;
		return Pair.of(initialFactPath,
				factVariables.stream()
						// Create Paths with the corresponding Templates for all collected
						// FactVariables
						.map(SingleFactVariable::getEqual).distinct()
						.collect(Collectors.toMap(Function.identity(), (final EquivalenceClass ec) -> {
							final SingleFactVariable fv = ec.getFactVariables().getFirst();
							return (fv.getTemplate() == initialFactTemplate ? initialFactPath
									: new Path(fv.getTemplate()));
						})));
	}

	public static Pair<SingleFactVariable, Set<SingleFactVariable>> collectVariables(final Template initialFactTemplate,
			final ConditionalElement ce) {
		// Collect all FactVariables defined in the CEs TemplateCEs and InitialFactCEs
		final Map<Boolean, Set<SingleFactVariable>> partition =
				collect(ce).stream().collect(partitioningBy(fv -> fv.getTemplate() == initialFactTemplate, toSet()));
		assert partition.get(Boolean.TRUE).size() == 1;
		return Pair.of(partition.get(Boolean.TRUE).iterator().next(), partition.get(Boolean.FALSE));
	}

	public static List<SingleFactVariable> collect(final ConditionalElement ce) {
		return ce.accept(new ShallowFactVariableCollector()).getFactVariables();
	}

	@Override
	public void defaultAction(final ConditionalElement ce) {
		// Just ignore all other ConditionalElements
		this.factVariables = Collections.emptyList();
	}

	@Override
	public void visit(final OrFunctionConditionalElement ce) {
		throw new Error("There should not be any Or ConditionalElements at this level.");
	}

	@Override
	public void visit(final AndFunctionConditionalElement ce) {
		this.factVariables = ce.getChildren().stream().flatMap(child -> collect(child).stream())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void visit(final TemplatePatternConditionalElement ce) {
		this.factVariables = Collections.singletonList(ce.getFactVariable());
	}

	@Override
	public void visit(final InitialFactConditionalElement ce) {
		this.factVariables = Collections.singletonList(ce.getInitialFactVariable());
	}
}