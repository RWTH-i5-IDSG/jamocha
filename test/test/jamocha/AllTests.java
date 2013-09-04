package test.jamocha;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for the whole jamocha project.
 * 
 * Add new package test suites or single tests to the SuiteClasses annotation.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Suite.class)
@SuiteClasses({
		test.jamocha.dn.memory.javaimpl.MemoryHandlerTempTest.class,
		test.jamocha.filter.AllFilterTests.class,
		test.jamocha.dn.nodes.AllNodeTests.class })
public class AllTests {

}
