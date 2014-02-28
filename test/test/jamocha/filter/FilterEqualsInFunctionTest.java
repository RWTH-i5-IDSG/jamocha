package test.jamocha.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.jamocha.util.PathFilterElementToCounterColumnMockup.fe2ccmockup;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.FilterTranslator;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

public class FilterEqualsInFunctionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueEquality() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		PathFilter pf = new PathFilter(f), pg = new PathFilter(g);

		assertTrue(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pf, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pg, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueNormalize() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p2, a2).addPath(p1, a1).build())
						.buildPFE();
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionFalseDifferentSlotAddress() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		SlotAddress a4 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertFalse(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueDifferentPath() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		Path p4 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionFalseDifferentSlotType() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertFalse(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));

	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionFalseCombinations() {
		Path p1 = new Path(new Template(SlotType.DOUBLE));
		Path p2 = new Path(new Template(SlotType.DOUBLE));
		Path p3 = new Path(new Template(SlotType.DOUBLE));
		Path p5 = new Path(new Template(SlotType.DOUBLE));
		SlotAddress a1 = new SlotAddress(0);
		SlotAddress a2 = new SlotAddress(0);
		SlotAddress a3 = new SlotAddress(0);
		SlotAddress a5 = new SlotAddress(0);
		Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337l, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(plus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertFalse(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p5, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pg = new PathFilter(g);
		assertFalse(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
		g =
				new PredicateBuilder(FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE,
						SlotType.DOUBLE))
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a5).build())
						.addFunction(
								new FunctionBuilder(minus).addConstant(1337.0, SlotType.DOUBLE)
										.addPath(p3, a3).build()).buildPFE();
		pg = new PathFilter(g);
		assertFalse(FilterFunctionCompare.equals(pf, FilterTranslator.translate(pg, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(pg, FilterTranslator.translate(pf, fe2ccmockup)));
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
		PathFilterElement f, g, h, i, j, k, l;
		Function<?> plusD = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> minusD = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		Predicate eqD = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		Function<?> plusL = FunctionDictionary.lookup("+", SlotType.LONG, SlotType.LONG);
		Function<?> minusL = FunctionDictionary.lookup("-", SlotType.LONG, SlotType.LONG);
		Predicate lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		Predicate eqL = FunctionDictionary.lookupPredicate("=", SlotType.LONG, SlotType.LONG);
		Predicate eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		f =
				new PredicateBuilder((Predicate) eqS)
						.addConstant("Max Mustermann", SlotType.STRING).addPath(p1, a1).buildPFE();
		g =
				new PredicateBuilder((Predicate) lessL).addConstant(18L, SlotType.LONG)
						.addPath(p1, a5).buildPFE();
		h =
				new PredicateBuilder((Predicate) lessL)
						.addConstant(50000, SlotType.LONG)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.buildPFE();
		i =
				new PredicateBuilder((Predicate) eqS)
						.addConstant("Max Mustermann", SlotType.STRING).addPath(p1, a1).buildPFE();
		j =
				new PredicateBuilder((Predicate) lessL).addConstant(18L, SlotType.LONG)
						.addPath(p1, a5).buildPFE();
		k =
				new PredicateBuilder((Predicate) lessL)
						.addConstant(50000, SlotType.LONG)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.buildPFE();
		PathFilter a, b;
		a = new PathFilter(f, g, h);
		b = new PathFilter(f, g, h);
		assertTrue(FilterFunctionCompare.equals(a, FilterTranslator.translate(b, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(b, FilterTranslator.translate(a, fe2ccmockup)));
		b = new PathFilter(i, j, k);
		assertTrue(FilterFunctionCompare.equals(a, FilterTranslator.translate(b, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(b, FilterTranslator.translate(a, fe2ccmockup)));
		b = new PathFilter(f, j, h);
		assertTrue(FilterFunctionCompare.equals(a, FilterTranslator.translate(b, fe2ccmockup)));
		assertTrue(FilterFunctionCompare.equals(b, FilterTranslator.translate(a, fe2ccmockup)));

		l =
				new PredicateBuilder((Predicate) lessL).addConstant(17L, SlotType.LONG)
						.addPath(p1, a5).buildPFE();
		b = new PathFilter(f, l, h);
		assertFalse(FilterFunctionCompare.equals(a, FilterTranslator.translate(b, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(b, FilterTranslator.translate(a, fe2ccmockup)));
		b = new PathFilter(f, l, h);
		assertFalse(FilterFunctionCompare.equals(a, FilterTranslator.translate(b, fe2ccmockup)));
		assertFalse(FilterFunctionCompare.equals(b, FilterTranslator.translate(a, fe2ccmockup)));
	}
}
