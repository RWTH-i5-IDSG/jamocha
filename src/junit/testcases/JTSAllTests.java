package testcases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JTSAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for testcases");
		// $JUnit-BEGIN$
		suite.addTestSuite(JTCDeftemplate.class);
		suite.addTestSuite(JamochaTest.class);
		// $JUnit-END$
		return suite;
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(JTSAllTests.class);
	}
}
