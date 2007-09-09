package testframework;

import testcases.JTCDeftemplate;
import testcases.JTCEngineFunctionTests;
import testcases.JTCSimpleAdd;
import junit.framework.Test;
import junit.framework.TestSuite;

public class JTSAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Jamocha Tests");
		//add all test cases below:
		
		suite.addTestSuite(JTCSimpleAdd.class);
		suite.addTestSuite(JTCDeftemplate.class);
		suite.addTestSuite(JTCEngineFunctionTests.class);

		return suite;
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(JTSAllTests.class);
	}
}
