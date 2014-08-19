package test.jamocha.languages.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite for the common package.
 * <p>
 * Please add new package test suites or single tests to the SuiteClasses annotation.
 * </p>
 * 
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 */

@RunWith(Suite.class)
@SuiteClasses({ RuleConditionProcessorTest.class })
public class AllCommonTests {

}
