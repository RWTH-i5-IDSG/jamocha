/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rete.functions.math;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.functions.FunctionMemory;

public class MathFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public String getName() {
		return (MathFunctions.class.getSimpleName());
	}

	public void loadFunctions(FunctionMemory functionMem) {
		Abs abs = new Abs();
		functionMem.declareFunction(abs);
		funcs.add(abs);
		Acos acos = new Acos();
		functionMem.declareFunction(acos);
		funcs.add(acos);
		Add add = new Add();
		functionMem.declareFunction(add);
		funcs.add(add);
		Asin asin = new Asin();
		functionMem.declareFunction(asin);
		funcs.add(asin);
		Atan atan = new Atan();
		functionMem.declareFunction(atan);
		funcs.add(atan);
		Ceil ceil = new Ceil();
		functionMem.declareFunction(ceil);
		funcs.add(ceil);
		Cos cos = new Cos();
		functionMem.declareFunction(cos);
		funcs.add(cos);
		Cosh cosh = new Cosh();
		functionMem.declareFunction(cosh);
		funcs.add(cosh);
		DegreesToRadians raddeg = new DegreesToRadians();
		functionMem.declareFunction(raddeg);
		funcs.add(raddeg);
		Divide div = new Divide();
		functionMem.declareFunction(div);
		funcs.add(div);
		E e = new E();
		functionMem.declareFunction(e);
		funcs.add(e);
		Evenp evenp = new Evenp();
		functionMem.declareFunction(evenp);
		funcs.add(evenp);
		Exp exp = new Exp();
		functionMem.declareFunction(exp);
		funcs.add(exp);
		Floor floor = new Floor();
		functionMem.declareFunction(floor);
		funcs.add(floor);
		Log log = new Log();
		functionMem.declareFunction(log);
		funcs.add(log);
		Log10 log10 = new Log10();
		functionMem.declareFunction(log10);
		funcs.add(log10);
		Max max = new Max();
		functionMem.declareFunction(max);
		funcs.add(max);
		Min min = new Min();
		functionMem.declareFunction(min);
		funcs.add(min);
		Mod mod = new Mod();
		functionMem.declareFunction(mod);
		funcs.add(mod);
		Multiply mul = new Multiply();
		functionMem.declareFunction(mul);
		funcs.add(mul);
		Oddp oddp = new Oddp();
		functionMem.declareFunction(oddp);
		funcs.add(oddp);
		
		Pi pi = new Pi();
		functionMem.declareFunction(pi);
		funcs.add(pi);
		
		Pow pow = new Pow();
		functionMem.declareFunction(pow);
		funcs.add(pow);
		RadiansToDegrees degrad = new RadiansToDegrees();
		functionMem.declareFunction(degrad);
		funcs.add(degrad);
		Random random = new Random();
		functionMem.declareFunction(random);
		funcs.add(random);
		Rint rint = new Rint();
		functionMem.declareFunction(rint);
		funcs.add(rint);
		Round round = new Round();
		functionMem.declareFunction(round);
		funcs.add(round);
		Signum signum = new Signum();
		functionMem.declareFunction(signum);
		funcs.add(signum);
		Sin sin = new Sin();
		functionMem.declareFunction(sin);
		funcs.add(sin);
		Sinh sinh = new Sinh();
		functionMem.declareFunction(sinh);
		funcs.add(sinh);
		Sqrt sqrt = new Sqrt();
		functionMem.declareFunction(sqrt);
		funcs.add(sqrt);
		Subtract sub = new Subtract();
		functionMem.declareFunction(sub);
		funcs.add(sub);
		Tan tan = new Tan();
		functionMem.declareFunction(tan);
		funcs.add(tan);
		Tanh tanh = new Tanh();
		functionMem.declareFunction(tanh);
		funcs.add(tanh);
		// now we add the functions under alias
		functionMem.declareFunction("+", add);
		functionMem.declareFunction("-", sub);
		functionMem.declareFunction("*", mul);
		functionMem.declareFunction("/", div);
		functionMem.declareFunction("**", pow);
	}

	public List listFunctions() {
		return funcs;
	}
	
	public void addFunction(Function function) {
		this.funcs.add(function);
	}

}
