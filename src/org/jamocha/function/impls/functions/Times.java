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
package org.jamocha.function.impls.functions;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.function.CommutativeFunction;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;

/**
 * Implements the functionality of the binary times ({@code *}) operator.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionDictionary
 */
public abstract class Times<R> implements CommutativeFunction<R> {
	public static String inClips = "*";

	@Override
	public String inClips() {
		return inClips;
	}

	@Override
	public <V extends FunctionVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	static {
		FunctionDictionary.addImpl(new Times<Long>() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.LONG, SlotType.LONG };
			}

			@Override
			public SlotType getReturnType() {
				return SlotType.LONG;
			}

			@Override
			public Long evaluate(final Function<?>... params) {
				return (Long) params[0].evaluate() * (Long) params[1].evaluate();
			}
		});
		FunctionDictionary.addImpl(new Times<Double>() {
			@Override
			public SlotType[] getParamTypes() {
				return new SlotType[] { SlotType.DOUBLE, SlotType.DOUBLE };
			}

			@Override
			public SlotType getReturnType() {
				return SlotType.DOUBLE;
			}

			@Override
			public Double evaluate(final Function<?>... params) {
				return (Double) params[0].evaluate() * (Double) params[1].evaluate();
			}
		});
		FunctionDictionary.addGenerator(inClips, SlotType.LONG, (final SlotType[] paramTypes) -> {
			return new Times<Long>() {
				@Override
				public SlotType[] getParamTypes() {
					return paramTypes;
				}

				@Override
				public SlotType getReturnType() {
					return SlotType.LONG;
				}

				@Override
				public Long evaluate(final Function<?>... params) {
					Long value = 0L;
					for (final Function<?> param : params) {
						value *= (Long) param.evaluate();
					}
					return value;
				}
			};
		});
		FunctionDictionary.addGenerator(inClips, SlotType.DOUBLE,
				(final SlotType[] paramTypes) -> {
					return new Times<Double>() {
						@Override
						public SlotType[] getParamTypes() {
							return paramTypes;
						}

						@Override
						public SlotType getReturnType() {
							return SlotType.DOUBLE;
						}

						@Override
						public Double evaluate(final Function<?>... params) {
							Double value = 0.0;
							for (final Function<?> param : params) {
								value *= (Double) param.evaluate();
							}
							return value;
						}
					};
				});
	}
}
