package test.jamocha.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.Path;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

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
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
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
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
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
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.LONG,
						SlotType.LONG))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(plus).addConstant(1337.0, SlotType.DOUBLE)
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
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p5, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
		g =
				new FunctionBuilder(TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a5).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).build();
		assertFalse(f.equalsInFunction(g));
		assertFalse(g.equalsInFunction(f));
	}

	@SuppressWarnings("unused")
	@Test
	public void testFilterEqualsInFunction() {
		Path p1 = new Path(new Template(SlotType.STRING, SlotType.LONG));
		Path p2 = new Path(new Template(SlotType.LONG));
		Path p3 = new Path(new Template(SlotType.LONG));
		Path p4 = new Path(new Template(SlotType.LONG));
		Path p5 = new Path(new Template(SlotType.STRING));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		SlotAddress a4 = new SlotAddress(0);
		SlotAddress a5 = new SlotAddress(1);
		PredicateWithArguments f, g, h, i, j, k, l;
		Function<?> plusD = TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minusD = TODODatenkrakeFunktionen.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> eqD = TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> plusL = TODODatenkrakeFunktionen.lookup("+", SlotType.LONG, SlotType.LONG);
		Function<?> minusL = TODODatenkrakeFunktionen.lookup("-", SlotType.LONG, SlotType.LONG);
		Function<?> lessL = TODODatenkrakeFunktionen.lookup("<", SlotType.LONG, SlotType.LONG);
		Function<?> eqL = TODODatenkrakeFunktionen.lookup("=", SlotType.LONG, SlotType.LONG);
		Function<?> eqS = TODODatenkrakeFunktionen.lookup("=", SlotType.STRING, SlotType.STRING);
		f =
				new PredicateBuilder((Predicate) eqS)
						.addConstant("Max Mustermann", SlotType.STRING).addPath(p1, a1).build();
		g =
				new PredicateBuilder((Predicate) lessL).addConstant(18L, SlotType.LONG)
						.addPath(p1, a5).build();
		h =
				new PredicateBuilder((Predicate) lessL)
						.addConstant(50000, SlotType.LONG)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.build();
		i =
				new PredicateBuilder((Predicate) eqS)
						.addConstant("Max Mustermann", SlotType.STRING).addPath(p1, a1).build();
		j =
				new PredicateBuilder((Predicate) lessL).addConstant(18L, SlotType.LONG)
						.addPath(p1, a5).build();
		k =
				new PredicateBuilder((Predicate) lessL)
						.addConstant(50000, SlotType.LONG)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.build();
		Filter a, b;
		a = new Filter(f, g, h);
		b = new Filter(f, g, h);
		assertTrue(a.equalsInFunction(b));
		assertTrue(b.equalsInFunction(a));
		b = new Filter(i, j, k);
		assertTrue(a.equalsInFunction(b));
		assertTrue(b.equalsInFunction(a));
		b = new Filter(f, j, h);
		assertTrue(a.equalsInFunction(b));
		assertTrue(b.equalsInFunction(a));

		l =
				new PredicateBuilder((Predicate) lessL).addConstant(17L, SlotType.LONG)
						.addPath(p1, a5).build();
		b = new Filter(f, l, h);
		assertFalse(a.equalsInFunction(b));
		assertFalse(b.equalsInFunction(a));
		b = new Filter(f, l, h);
		assertFalse(a.equalsInFunction(b));
		assertFalse(b.equalsInFunction(a));
	}
}
