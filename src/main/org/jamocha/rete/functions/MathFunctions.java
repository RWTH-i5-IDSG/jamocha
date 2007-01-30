/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class MathFunctions implements FunctionGroup {

	private static final long serialVersionUID = 1L;

	private ArrayList<Function> funcs = new ArrayList<Function>();

	public MathFunctions() {
		super();
	}

	public String getName() {
		return (MathFunctions.class.getSimpleName());
	}

	public void loadFunctions(Rete engine) {
		Abs abs = new Abs();
		engine.declareFunction(abs);
		funcs.add(abs);
		Acos acos = new Acos();
		engine.declareFunction(acos);
		funcs.add(acos);
		Add add = new Add();
		engine.declareFunction(add);
		funcs.add(add);
		Asin asin = new Asin();
		engine.declareFunction(asin);
		funcs.add(asin);
		Atan atan = new Atan();
		engine.declareFunction(atan);
		funcs.add(atan);
		Ceil ceil = new Ceil();
		engine.declareFunction(ceil);
		funcs.add(ceil);
		Const cnst = new Const();
		engine.declareFunction(cnst);
		funcs.add(cnst);
		Cos cos = new Cos();
		engine.declareFunction(cos);
		funcs.add(cos);
		Cosh cosh = new Cosh();
		engine.declareFunction(cosh);
		funcs.add(cosh);
		Degrees degrees = new Degrees();
		engine.declareFunction(degrees);
		funcs.add(degrees);
		Divide div = new Divide();
		engine.declareFunction(div);
		funcs.add(div);
		EqFunction eqf = new EqFunction();
		engine.declareFunction(eqf);
		funcs.add(eqf);
		Evenp evenp = new Evenp();
		engine.declareFunction(evenp);
		funcs.add(evenp);
		Exp exp = new Exp();
		engine.declareFunction(exp);
		funcs.add(exp);
		Floor floor = new Floor();
		engine.declareFunction(floor);
		funcs.add(floor);
		Greater gr = new Greater();
		engine.declareFunction(gr);
		funcs.add(gr);
		GreaterOrEqual gre = new GreaterOrEqual();
		engine.declareFunction(gre);
		funcs.add(gre);
		Less le = new Less();
		engine.declareFunction(le);
		funcs.add(le);
		LessOrEqual leoe = new LessOrEqual();
		engine.declareFunction(leoe);
		funcs.add(leoe);
		Log log = new Log();
		engine.declareFunction(log);
		funcs.add(log);
		Log10 log10 = new Log10();
		engine.declareFunction(log10);
		funcs.add(log10);
		Max max = new Max();
		engine.declareFunction(max);
		funcs.add(max);
		Min min = new Min();
		engine.declareFunction(min);
		funcs.add(min);
		Mod mod = new Mod();
		engine.declareFunction(mod);
		funcs.add(mod);
		Multiply mul = new Multiply();
		engine.declareFunction(mul);
		funcs.add(mul);
		NeqFunction neq = new NeqFunction();
		engine.declareFunction(neq);
		funcs.add(neq);
		Oddp oddp = new Oddp();
		engine.declareFunction(oddp);
		funcs.add(oddp);
		Pow pow = new Pow();
		engine.declareFunction(pow);
		funcs.add(pow);
		Radians radians = new Radians();
		engine.declareFunction(radians);
		funcs.add(radians);
		Random random = new Random();
		engine.declareFunction(random);
		funcs.add(random);
		Rint rint = new Rint();
		engine.declareFunction(rint);
		funcs.add(rint);
		Round round = new Round();
		engine.declareFunction(round);
		funcs.add(round);
		Sin sin = new Sin();
		engine.declareFunction(sin);
		funcs.add(sin);
		Sinh sinh = new Sinh();
		engine.declareFunction(sinh);
		funcs.add(sinh);
		Sqrt sqrt = new Sqrt();
		engine.declareFunction(sqrt);
		funcs.add(sqrt);
		Subtract sub = new Subtract();
		engine.declareFunction(sub);
		funcs.add(sub);
		Tan tan = new Tan();
		engine.declareFunction(tan);
		funcs.add(tan);
		Tanh tanh = new Tanh();
		engine.declareFunction(tanh);
		funcs.add(tanh);
		// now we add the functions under alias
		engine.declareFunction("+", add);
		engine.declareFunction("-", sub);
		engine.declareFunction("*", mul);
		engine.declareFunction("/", div);
		engine.declareFunction("**", pow);
		engine.declareFunction(">", gr);
		engine.declareFunction(">=", gre);
		engine.declareFunction("<", le);
		engine.declareFunction("<=", leoe);

	}

	public List listFunctions() {
		return funcs;
	}

}
