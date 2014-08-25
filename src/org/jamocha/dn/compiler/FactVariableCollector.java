/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.dn.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.jamocha.filter.Path;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.DefaultConditionalElementsVisitor;
import org.jamocha.languages.common.SingleFactVariable;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class FactVariableCollector implements DefaultConditionalElementsVisitor {

	@Getter
	private List<SingleFactVariable> factVariables = null;

	public static Map<SingleFactVariable, Path> collectPaths(ConditionalElement ce) {
		// Collect all FactVariables defined in the CEs TemplateCEs and InitialFactCEs
		return ce
				.accept(new FactVariableCollector())
				.getFactVariables()
				.stream()
				// Create Paths with the corresponding Templates
				// for all collected FactVariables
				.collect(
						Collectors.toMap(
								variable -> variable,
								(SingleFactVariable variable) -> new Path(variable
										.getTemplate())));
	}

	@Override
	public void defaultAction(ConditionalElement ce) {
		// Just ignore all other ConditionalElements
	}

	@Override
	public void visit(OrFunctionConditionalElement ce) {
		throw new Error("There should not be any Or ConditionalElements at this level.");
	}

	@Override
	public void visit(AndFunctionConditionalElement ce) {
		ce.getChildren().stream()
				.map(child -> child.accept(new FactVariableCollector()).getFactVariables())
				.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public void visit(SharedConditionalElementWrapper ce) {
		ce.getCe().accept(this);
	}

	@Override
	public void visit(TemplatePatternConditionalElement ce) {
		factVariables = Arrays.asList(ce.getFactVariable());
	}

	@Override
	public void visit(InitialFactConditionalElement ce) {
		factVariables = Arrays.asList(ce.getInitialFactVariable());
	}
}