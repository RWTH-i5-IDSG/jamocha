package test.jamocha.filter;

import static org.junit.Assert.*;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;

public class FilterEqualsInFunctionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunction() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		Path p4 = new Path(new Template(SlotType.DOUBLE));
		Path p5 = new Path(new Template(SlotType.STRING));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		SlotAddress a4 = new SlotAddress(0);
		SlotAddress a5 = new SlotAddress(1);
		Function<?> plus = TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = TODODatenkrakeFunktionen.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		FunctionWithArguments f =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		FunctionWithArguments g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertTrue(f.equalsInFunction(f));
		assertTrue(g.equalsInFunction(g));
		assertTrue(f.equalsInFunction(g));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.build();
		assertTrue(f.equalsInFunction(g));
		assertTrue(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p2, a2).addPath(p1, a1).build())
						.build();
		assertTrue(f.equalsInFunction(g));
		assertTrue(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.LONG,
						SlotType.LONG))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(plus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.LONG)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));	
	}
}
