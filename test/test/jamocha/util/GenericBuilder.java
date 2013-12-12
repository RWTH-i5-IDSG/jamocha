/*
 * Copyright 2002-2013 The Jamocha Team
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
package test.jamocha.util;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.ConstantLeaf;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathLeaf;

/**
 * Derived classes have to use themselves or one of their super classes as T.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 * @param <R>
 *            return type of function
 * @param <F>
 *            function type
 * @param <T>
 *            current subclass of generic builder
 */
public abstract class GenericBuilder<R, F extends Function<? extends R>, T extends GenericBuilder<R, F, T>> {

	protected final F function;
	final List<FunctionWithArguments> args = new LinkedList<>();

	protected GenericBuilder(final F function) {
		this.function = function;
	}

	@SuppressWarnings("unchecked")
	public T addPath(final Path path, final SlotAddress slot) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != path.getTemplateSlotType(slot)) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new PathLeaf(path, slot));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T addConstant(final Object value, final SlotType type) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != type) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(new ConstantLeaf(value, type));
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T addFunction(final FunctionWithArguments function) {
		final SlotType[] paramTypes = this.function.getParamTypes();
		if (paramTypes.length == this.args.size()) {
			throw new IllegalArgumentException("All arguments already set!");
		}
		if (paramTypes[this.args.size()] != function.getReturnType()) {
			throw new IllegalArgumentException("Wrong argument type!");
		}
		this.args.add(function);
		return (T) this;
	}

	public abstract FunctionWithArguments build();
}