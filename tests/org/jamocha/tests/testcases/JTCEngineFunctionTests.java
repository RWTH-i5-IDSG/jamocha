/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.tests.testcases;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.engine.functions.Function;
import org.jamocha.engine.functions.FunctionDescription;
import org.jamocha.tests.AbstractJamochaTest;

/**
 * class to execute all jamocha engine functions and check for exceptions
 * 
 * @author Sebastian Reinartz
 */
public class JTCEngineFunctionTests extends AbstractJamochaTest {

	public JTCEngineFunctionTests(String arg0) {
		super(arg0);
	}

	/**
	 * this method collects all engine functions registered in jamocha, executes
	 * their example and checks for exceptions.
	 * 
	 */
	@Override
	public void test() {
		List<Function> functions = new LinkedList<Function>(engine
				.getFunctionMemory().getAllFunctions());

		FunctionDescription descr;

		// traverse all functions and execute their example if possible.
		for (Function currentFunction : functions) {
			descr = currentFunction.getDescription();

			if (descr.isResultAutoGeneratable()) {

				System.out.println("execute example for function:"
						+ currentFunction);
				// execute:
				String result = executeCommandReturnLast(descr.getExample(),
						"Error while testing " + currentFunction.getName());

				if (descr.getExpectedResult() != null)
					assertEquals("error while testing "
							+ currentFunction.getName(), descr
							.getExpectedResult().toString(), result);

				// cleanup engine:
				try {
					setUp();
				} catch (Exception e) {
					fail(e.getMessage());
				}
			}

		}
	}
}
