package test.jamocha.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static test.jamocha.util.CounterColumnMatcherMockup.counterColumnMatcherMockup;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.javaimpl.MemoryFactory;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.FilterFunctionCompare;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.filter.PathNodeFilterSet.PathFilter;
import org.jamocha.filter.PathNodeFilterSetToAddressNodeFilterSetTranslator;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.Predicate;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;
import test.jamocha.util.Slots;

public class FilterEqualsInFunctionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueEquality() {
		final Path p1 = new Path(Slots.DOUBLE);
		final Path p2 = new Path(Slots.DOUBLE);
		final Path p3 = new Path(Slots.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilter f, g;

		f =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		g =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		final PathNodeFilterSet pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f), pg =
				PathNodeFilterSet.newRegularPathNodeFilterSet(g);

		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)));
	}

	@Test
	public void testEqualsInFunctionFalseDifferentSlotAddress() {
		final Path p1 =
				new Path(MemoryFactory.getMemoryFactory().newTemplate("", "", Slots.newDouble("s1"),
						Slots.newDouble("s2")));
		final Path p2 = new Path(Slots.DOUBLE);
		final Path p3 = new Path(Slots.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final SlotAddress a4 = new SlotAddress(1);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilter f, g;

		f =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		g =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a4).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		final PathNodeFilterSet pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f), pg =
				PathNodeFilterSet.newRegularPathNodeFilterSet(g);

		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueNormalize() {
		final Path p1 = new Path(Slots.DOUBLE);
		final Path p2 = new Path(Slots.DOUBLE);
		final Path p3 = new Path(Slots.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilter f, g;
		PathNodeFilterSet pf, pg;

		f =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		g =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build()).buildFilter();
		pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f);
		pg = PathNodeFilterSet.newRegularPathNodeFilterSet(g);
		assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf,
				counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)
						.getNormalisedVersion()));
		assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg,
				counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)
						.getNormalisedVersion()));
		g =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build())
						.addFunction(new FunctionBuilder(plus).addPath(p2, a2).addPath(p1, a1).build()).buildFilter();
		pg = PathNodeFilterSet.newRegularPathNodeFilterSet(g);
		assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf,
				counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)
						.getNormalisedVersion()));
		assertTrue(FilterFunctionCompare.equals(PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg,
				counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)
						.getNormalisedVersion()));
	}

	@Test
	public void testFunctionWithArgumentsCompositeEqualsInFunctionTrueDifferentPath() {
		final Path p1 = new Path(Slots.DOUBLE);
		final Path p2 = new Path(Slots.DOUBLE);
		final Path p3 = new Path(Slots.DOUBLE);
		final Path p4 = new Path(Slots.DOUBLE);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a2 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final Function<?> plus = FunctionDictionary.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
		final Function<?> minus = FunctionDictionary.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
		final Predicate equals = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
		PathFilter f, g;
		PathNodeFilterSet pf, pg;

		f =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p1, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		g =
				new PredicateBuilder(equals)
						.addFunction(new FunctionBuilder(plus).addPath(p4, a1).addPath(p2, a2).build())
						.addFunction(new FunctionBuilder(minus).addDouble(1337.).addPath(p3, a3).build()).buildFilter();
		pf = PathNodeFilterSet.newRegularPathNodeFilterSet(f);
		pg = PathNodeFilterSet.newRegularPathNodeFilterSet(g);
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pg, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(pf, counterColumnMatcherMockup)));
	}

	@Test
	public void testFilterEqualsInFunction() {
		final Path p1 =
				new Path(MemoryFactory.getMemoryFactory().newTemplate("", "", Slots.newString("s1"),
						Slots.newLong("s2")));
		final Path p2 = new Path(Slots.LONG);
		final Path p3 = new Path(Slots.LONG);
		final SlotAddress a1 = new SlotAddress(0);
		final SlotAddress a3 = new SlotAddress(0);
		final SlotAddress a4 = new SlotAddress(0);
		final SlotAddress a5 = new SlotAddress(1);
		PathFilter f, g, h, i, j, k, l;
		final Function<?> plusL = FunctionDictionary.lookup("+", SlotType.LONG, SlotType.LONG);
		final Predicate lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);
		f = new PredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildFilter();
		g = new PredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildFilter();
		h =
				new PredicateBuilder(lessL).addLong(50000)
						.addFunction(new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build()).buildFilter();
		i = new PredicateBuilder(eqS).addString("Max Mustermann").addPath(p1, a1).buildFilter();
		j = new PredicateBuilder(lessL).addLong(18L).addPath(p1, a5).buildFilter();
		k =
				new PredicateBuilder(lessL).addLong(50000)
						.addFunction(new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build()).buildFilter();
		PathNodeFilterSet a, b;
		a = PathNodeFilterSet.newRegularPathNodeFilterSet(f, g, h);
		b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, g, h);
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup)));
		b = PathNodeFilterSet.newRegularPathNodeFilterSet(i, j, k);
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup)));
		b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, j, h);
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup)));
		assertTrue(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup)));

		l = new PredicateBuilder(lessL).addLong(17L).addPath(p1, a5).buildFilter();
		b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, l, h);
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup)));
		b = PathNodeFilterSet.newRegularPathNodeFilterSet(f, l, h);
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup)));
		assertFalse(FilterFunctionCompare.equals(
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(b, counterColumnMatcherMockup),
				PathNodeFilterSetToAddressNodeFilterSetTranslator.translate(a, counterColumnMatcherMockup)));
	}
}
