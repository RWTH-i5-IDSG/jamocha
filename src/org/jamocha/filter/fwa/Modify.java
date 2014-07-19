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
package org.jamocha.filter.fwa;

import static java.util.stream.Collectors.toCollection;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.Function;
import org.jamocha.languages.common.errors.NoSlotForThatNameError;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class Modify implements FunctionWithArguments {
	@Value
	public static class SlotAndValue {
		final String slotName;
		final FunctionWithArguments value;
	}

	@Getter
	@NonNull
	final Network network;
	@Getter
	@NonNull
	final FunctionWithArguments targetFact;
	@Getter
	final SlotAndValue[] args;
	@Getter(lazy = true, onMethod = @__(@Override))
	private final SlotType[] paramTypes = calculateParamTypes();

	private SlotType[] calculateParamTypes() {
		return calculateParamTypes(this.args);
	}

	static private SlotType[] calculateParamTypes(final SlotAndValue[] args) {
		final ArrayList<SlotType> types =
				Arrays.stream(args).map(sav -> sav.value.getReturnType()).map(Arrays::asList)
						.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
		return toArray(types, SlotType[]::new);
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType getReturnType() {
		return SlotType.FACTADDRESS;
	}

	@Override
	public Function<Object> lazyEvaluate(final Function<?>... params) {
		final Deque<FunctionWithArguments> fwas =
				Arrays.stream(args).map(sav -> sav.value).collect(toCollection(LinkedList::new));
		fwas.addFirst(targetFact);
		final FunctionWithArguments[] array = toArray(fwas, FunctionWithArguments[]::new);
		return new GenericWithArgumentsComposite.LazyObject(GenericWithArgumentsComposite
				.staticLazyEvaluate(
						fs -> {
							final FactIdentifier factIdentifier = Retract.toFactIdentifier(fs[0]);
							final Fact fact =
									network.getMemoryFact(factIdentifier.getId()).toMutableFact();
							network.retractFacts(factIdentifier);
							final Template template = fact.getTemplate();
							for (int i = 0; i < args.length; ++i) {
								final String slotName = args[i].getSlotName();
								final SlotAddress slotAddress = template.getSlotAddress(slotName);
								if (null == slotAddress) {
									throw new NoSlotForThatNameError(slotName);
								}
								template.setValue(fact, slotAddress, fs[i + 1].evaluate());
							}
							return network.assertFacts(fact)[0];
						}, "assert", array, params).evaluate());
	}

	@Override
	public Object evaluate(final Object... params) {
		return GenericWithArgumentsComposite.staticEvaluate(this::lazyEvaluate, params);
	}

	@Override
	public int hashPositionIsIrrelevant() {
		// TODO Auto-generated method stub
		return 0;
	}
}
