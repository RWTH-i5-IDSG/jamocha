package test.jamocha.dn.nodes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite for the node package.
 * 
 * Please add new package test suites or single tests to the SuiteClasses
 * annotation.
 * 
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 */
@RunWith(Suite.class)
@SuiteClasses({ AlphaNodeTest.class, BetaNodeTest.class,
		ObjectTypeNodeTest.class, TerminalNodeTest.class })
public class AllNodeTests {

}
