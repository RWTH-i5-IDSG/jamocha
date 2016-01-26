/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.util.builder.fwa;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.Function;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.languages.common.RuleCondition;

/**
 * Derived classes have to use themselves or one of their super classes as T.
 *
 * @param <R>
 * 		return type of function
 * @param <F>
 * 		function type
 * @param <T>
 * 		current subclass of generic builder
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class ECGenericBuilder<R, F extends Function<? extends R>, T extends ECGenericBuilder<R, F, T>>
		extends GenericBuilder<ECLeaf, R, F, T> {

	protected ECGenericBuilder(final F function) {
		super(function);
	}

	@SuppressWarnings("unchecked")
	public T addEC(final RuleCondition.EquivalenceClass equivalenceClass) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != equivalenceClass.getType()) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new ECLeaf(equivalenceClass));
		return (T) this;
	}
}