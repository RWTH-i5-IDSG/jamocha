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

import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Function;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class Modify implements FunctionWithArguments {
	@Override
	public <T extends FunctionWithArgumentsVisitor> T accept(final T visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType[] getParamTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SlotType getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<?> lazyEvaluate(Function<?>... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evaluate(Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashPositionIsIrrelevant() {
		// TODO Auto-generated method stub
		return 0;
	}
}
