package test.jamocha.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.memory.javaimpl.Template;
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
		final Path p1 = new Path(Template.DOUBLE);
		final Path p2 = new Path(Template.DOUBLE);
		final Path p3 = new Path(Template.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals =
				FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;

		f =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		g =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		final PathFilter pf = new PathFilter(f), pg = new PathFilter(g);

		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pf, counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pg, counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pf, counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup)));
	}

	@Test
	public void testEqualsInFunctionFalseDifferentSlotAddress() {
		final Path p1 =
				new Path(MemoryFactory.getMemoryFactory().newTemplate("",
						new Slot(SlotType.DOUBLE, ""), new Slot(SlotType.DOUBLE, "")));
		final Path p2 = new Path(Template.DOUBLE);
		final Path p3 = new Path(Template.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final SlotAddress a4 = new SlotAddress(1);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals =
				FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;

		f =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		g =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		final PathFilter pf = new PathFilter(f), pg = new PathFilter(g);

		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pf, counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pg, counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(pf, counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(pg, counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueNormalize() {
		final Path p1 = new Path(Template.DOUBLE);
		final Path p2 = new Path(Template.DOUBLE);
		final Path p3 = new Path(Template.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals =
				FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		g =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(FilterTranslator.translate(pf,
				counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup).getNormalisedVersion()));
		assertTrue(FilterFunctionCompare.equals(FilterTranslator.translate(pg,
				counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup).getNormalisedVersion()));
		g =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.addFunction(
								new FunctionBuilder(plus).addPath(p2, a2).addPath(p1, a1).build())
						.buildPFE();
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(FilterTranslator.translate(pf,
				counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup).getNormalisedVersion()));
		assertTrue(FilterFunctionCompare.equals(FilterTranslator.translate(pg,
				counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup).getNormalisedVersion()));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueDifferentPath() {
		final Path p1 = new Path(Template.DOUBLE);
		final Path p2 = new Path(Template.DOUBLE);
		final Path p3 = new Path(Template.DOUBLE);
		final Path p4 = new Path(Template.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals =
				FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilterElement f, g;
		PathFilter pf, pg;

		f =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		g =
				new PredicateBuilder(equals)
						.addFunction(
								new FunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
						.addFunction(
								new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.buildPFE();
		pf = new PathFilter(f);
		pg = new PathFilter(g);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pf, counterColumnMatcherMockup),
				FilterTranslator.translate(pg, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(pg, counterColumnMatcherMockup),
				FilterTranslator.translate(pf, counterColumnMatcherMockup)));
	}

	@Test
	public void testFilterEqualsInFunction() {
		final Path p1 =
				new Path(MemoryFactory.getMemoryFactory().newTemplate("",
						new Slot(SlotType.STRING, ""), new Slot(SlotType.LONG, "")));
		final Path p2 = new Path(Template.LONG);
		final Path p3 = new Path(Template.LONG);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final SlotAddress a4 = new SlotAddress(0);
		final SlotAddress a5 = new SlotAddress(1);
		PathFilterElement f, g, h, i, j, k, l;
		final Function<?> plusL = FunctionDictionary.lookup("+", SlotType.LONG, SlotType.LONG);
		final Predicate lessL =
				FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqS =
				FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		f = new PredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildPFE();
		g = new PredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildPFE();
		h =
				new PredicateBuilder(lessL)
						.addLong(50000)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.buildPFE();
		i = new PredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildPFE();
		j = new PredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildPFE();
		k =
				new PredicateBuilder(lessL)
						.addLong(50000)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.buildPFE();
		PathFilter a, b;
		a = new PathFilter(f, g, h);
		b = new PathFilter(f, g, h);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(a, counterColumnMatcherMockup),
				FilterTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(b, counterColumnMatcherMockup),
				FilterTranslator.translate(a, counterColumnMatcherMockup)));
		b = new PathFilter(i, j, k);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(a, counterColumnMatcherMockup),
				FilterTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(b, counterColumnMatcherMockup),
				FilterTranslator.translate(a, counterColumnMatcherMockup)));
		b = new PathFilter(f, j, h);
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(a, counterColumnMatcherMockup),
				FilterTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				FilterTranslator.translate(b, counterColumnMatcherMockup),
				FilterTranslator.translate(a, counterColumnMatcherMockup)));

		l = new PredicateBuilder(lessL).addLong(17L).addPath(p1, a5).buildPFE();
		b = new PathFilter(f, l, h);
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(a, counterColumnMatcherMockup),
				FilterTranslator.translate(b, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(b, counterColumnMatcherMockup),
				FilterTranslator.translate(a, counterColumnMatcherMockup)));
		b = new PathFilter(f, l, h);
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(a, counterColumnMatcherMockup),
				FilterTranslator.translate(b, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				FilterTranslator.translate(b, counterColumnMatcherMockup),
				FilterTranslator.translate(a, counterColumnMatcherMockup)));
	}
}
