package test.jamocha;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for the whole Jamocha project.
 * <p>
 * Please add new package test suites or single tests to the SuiteClasses annotation.
 * </p>
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Suite.class)
@SuiteClasses({ test.jamocha.dn.AllDnTests.class, test.jamocha.filter.AllFilterTests.class,
		test.jamocha.languages.AllLanguageTests.class })
public class AllTests {

}
