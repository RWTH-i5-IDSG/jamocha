package test.jamocha;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RunWith(Suite.class)
@SuiteClasses({
		test.jamocha.engine.memory.javaimpl.MemoryHandlerTempTest.class,
		test.jamocha.engine.filter.AllFilterTests.class,
		test.jamocha.engine.nodes.AllNodeTests.class })
public class AllTests {

}
