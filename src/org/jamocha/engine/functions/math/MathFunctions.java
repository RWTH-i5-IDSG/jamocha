/*
 * Copyright 2002-2008 The Jamocha Team
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

package org.jamocha.engine.functions.math;

import org.jamocha.engine.functions.FunctionGroup;
import org.jamocha.engine.functions.FunctionMemory;

public class MathFunctions extends FunctionGroup {

	private static final long serialVersionUID = 1L;

	public MathFunctions() {
		super();
		name = "MathFunctions";
		description = "This Group provides functions to do some arthmetical calculations.";
	}

	@Override
	public void loadFunctions(FunctionMemory functionMem) {
		addFunction(functionMem, new Abs());
		addFunction(functionMem, new Acos());
		addFunction(functionMem, new Add());
		addFunction(functionMem, new Asin());
		addFunction(functionMem, new Atan());
		addFunction(functionMem, new Ceil());
		addFunction(functionMem, new Cos());
		addFunction(functionMem, new Cosh());
		addFunction(functionMem, new DegreesToRadians());
		addFunction(functionMem, new Divide());
		addFunction(functionMem, new E());
		addFunction(functionMem, new Evenp());
		addFunction(functionMem, new Exp());
		addFunction(functionMem, new Floor());
		addFunction(functionMem, new Log());
		addFunction(functionMem, new Log10());
		addFunction(functionMem, new Max());
		addFunction(functionMem, new Min());
		addFunction(functionMem, new Mod());
		addFunction(functionMem, new Multiply());
		addFunction(functionMem, new Oddp());
		addFunction(functionMem, new Pi());
		addFunction(functionMem, new Pow());
		addFunction(functionMem, new RadiansToDegrees());
		addFunction(functionMem, new Random());
		addFunction(functionMem, new Rint());
		addFunction(functionMem, new Round());
		addFunction(functionMem, new Signum());
		addFunction(functionMem, new Sin());
		addFunction(functionMem, new Sinh());
		addFunction(functionMem, new Sqrt());
		addFunction(functionMem, new Subtract());
		addFunction(functionMem, new Tan());
		addFunction(functionMem, new Tanh());
	}

}
