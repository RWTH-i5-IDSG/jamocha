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


import org.jamocha.tests.AbstractJamochaTest;

/**
 * simple JUnit test case to test deftemplate functionality
 * @author Sebastian Reinartz
 */
public class JTCDeftemplate extends AbstractJamochaTest {

	public JTCDeftemplate(String arg0) {
		super(arg0);
	}

	/**
	 * executes a Deftemplate CLIPS command and compares with jamocha result "true".
	 * 
	 */
	@Override
	public void test() {
		executeTestEquals("(deftemplate wurst(slot name)(slot size))", "true");
		executeTestEquals("(assert (wurst (name entenwurst)(size small)))", "f-1");
	}

}
