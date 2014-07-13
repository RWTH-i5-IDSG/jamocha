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
package org.jamocha.filter.impls.specials;

import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Value;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Function;
import org.jamocha.filter.fwa.FunctionWithArguments;
import org.jamocha.filter.impls.FunctionVisitor;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class Assert implements Function<FactIdentifier[]> {
	public static String inClips = "assert";

	@Value
	public static class TemplateContainer {
		final Template template;
		final FunctionWithArguments[] values;
	}

	final Network network;
	final List<TemplateContainer> toAssert;
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();

	@Override
	public String inClips() {
		return inClips;
	}

	@Override
	public SlotType getReturnType() {
		return SlotType.FACTADDRESSES;
	}

	private SlotType[] calculateParamTypes() {
		final ArrayList<SlotType> types =
				toAssert.stream().flatMap(tc -> Arrays.stream(tc.values))
						.map(FunctionWithArguments::getParamTypes).map(Arrays::asList)
						.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		return toArray(types, SlotType[]::new);
	}

	@Override
	public <V extends FunctionVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public FactIdentifier[] evaluate(final Function<?>... params) {
		int paramsIndex = 0;
		final Fact[] facts = new Fact[this.toAssert.size()];
		int factIndex = 0;
		for (final TemplateContainer tc : this.toAssert) {
			final Object[] args = new Object[tc.values.length];
			int argIndex = 0;
			for (final FunctionWithArguments fwa : tc.values) {
				final int length = fwa.getParamTypes().length;
				args[argIndex++] =
						fwa.lazyEvaluate(
								Arrays.copyOfRange(params, paramsIndex, paramsIndex + length))
								.evaluate();
				paramsIndex += length;
			}
			facts[factIndex++] = tc.template.newFact(args);
		}
		return network.assertFacts(facts);
		// return network.assertFacts(toArray(
		// toAssert.stream().map(
		// tc -> tc.template.newFact(Arrays.stream(tc.values)
		// .map(fwa -> fwa.evaluate()).toArray())), Fact[]::new));
	}
}
