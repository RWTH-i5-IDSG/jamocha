package test.jamocha.dn.compiler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({FactVariableCollectorTest.class, PathFilterConsolidatorTest.class, SymbolToPathTranslatorTest.class})
public class AllCompilerTests {

}
