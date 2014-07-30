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
package org.jamocha.function.fwa;

import org.jamocha.function.Function;

/**
 * Instantiation of {@link GenericWithArgumentsComposite} holding a Function.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class FunctionWithArgumentsComposite extends
		GenericWithArgumentsComposite<Object, Function<?>> {
	public FunctionWithArgumentsComposite(final Function<?> function,
			final FunctionWithArguments... args) {
		super(function, args);
	}

	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}
}
