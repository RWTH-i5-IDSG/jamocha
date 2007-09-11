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
package testframework.testcases;

import testframework.AbstractJamochaTest;

/**
 * simple JUnit test case to test the jamocha test framework functions by 
 * comparing a simple add result.
 * @author Sebastian Reinartz
 */
public class JTCSimpleAdd extends AbstractJamochaTest {

	public JTCSimpleAdd(String arg0) {
		super(arg0);
	}
	
	/**
	 * function to execute a test for a simple add example.
	 * 
	 */
	public void test() {
		executeTestEquals("(+ 2 2)", "4");
	}

}
