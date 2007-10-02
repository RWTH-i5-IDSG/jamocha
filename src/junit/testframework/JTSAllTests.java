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
package testframework;

import junit.framework.Test;
import junit.framework.TestSuite;
import testframework.testcases.JTCAssertTests;
import testframework.testcases.JTCBierWurstSenf;
import testframework.testcases.JTCDeftemplate;
import testframework.testcases.JTCEngineFunctionTests;
import testframework.testcases.JTCSimpleAdd;

/**
 * JUnit TestSuite for all Jamocha test cases and suites
 * @author Sebastian Reinartz
 */
public class JTSAllTests {

	/**
	 * defines the test suite for all jamocha tests. New tests have to be added by
	 * using the addTestSuite function.
	 * 
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Jamocha Tests");
		
		//add all test cases below:
		suite.addTestSuite(JTCBierWurstSenf.class);
		suite.addTestSuite(JTCSimpleAdd.class);
		suite.addTestSuite(JTCDeftemplate.class);
		suite.addTestSuite(JTCEngineFunctionTests.class);
		suite.addTestSuite(JTCAssertTests.class);
		return suite;
	}
	
	/**
	 * starts a gui-based test run for the test suite defined in this class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(JTSAllTests.class);
	}
}
