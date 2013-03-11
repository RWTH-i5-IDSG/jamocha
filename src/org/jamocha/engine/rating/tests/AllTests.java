package org.jamocha.engine.rating.tests;

import static org.jamocha.engine.rating.tests.Matchers.greaterOrEqual;
import static org.jamocha.engine.rating.tests.Matchers.lessOrEqual;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.rating.CSVWriter;
import org.junit.BeforeClass;
import org.junit.Test;

public class AllTests {

	final double rootSize = 20, rootFInsert = 2, rootFDelete = 2, rootTPP = 50;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger.getLogger(Engine.class.getCanonicalName()).setLevel(
				Level.WARNING);
		(new File("temp")).mkdir();
	}

	@Test
	public void testAlphasSortedBySel() throws NodeException {
		final RatingTestCase.RatingResult resultAlphasSortedBySelAsc = RTC_AlphasSortedBySelAsc
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP)
				.getRatingResult();
		final RatingTestCase.RatingResult resultAlphasSortedBySelDesc = RTC_AlphasSortedBySelDesc
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP)
				.getRatingResult();
		final double ascMem = resultAlphasSortedBySelAsc.getMemoryCost();
		final double descMem = resultAlphasSortedBySelDesc.getMemoryCost();
		final double ascCpu = resultAlphasSortedBySelAsc.getRuntimeCost();
		final double descCpu = resultAlphasSortedBySelDesc.getRuntimeCost();

		assertThat(
				"Memory cost should be higher when sorting asc instead of desc",
				ascMem, greaterOrEqual(descMem));
		assertThat(
				"Cpu cost should be higher when sorting asc instead of desc",
				ascCpu, greaterOrEqual(descCpu));
	}

	@Test
	public void testTwoBetasAndOneAlpha() throws NodeException {
		final RatingTestCase.RatingResult resultTwoBetasShareOneAlpha = RTC_TwoBetasShareOneAlpha
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP)
				.getRatingResult();
		final RatingTestCase.RatingResult resultTwoBetasDontShareOneAlpha = RTC_TwoBetasDontShareOneAlpha
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP)
				.getRatingResult();

		assertThat("Memory cost should be lower when sharing nodes",
				resultTwoBetasShareOneAlpha.getMemoryCost(),
				lessOrEqual(resultTwoBetasDontShareOneAlpha.getMemoryCost()));
		assertThat("Cpu cost should be lower when sharing nodes",
				resultTwoBetasShareOneAlpha.getRuntimeCost(),
				lessOrEqual(resultTwoBetasDontShareOneAlpha.getRuntimeCost()));
	}

	@Test
	public void testNegation() throws NodeException, IOException {
		final double sel1 = 0.5, sel2 = 0.3, sel3 = 0.3;
		final double jsfA1B = 0.75;

		final CSVWriter writer = new CSVWriter(new java.io.File(
				"temp/testNegation.dat"), "jsfA2N_1", "mem1", "cpu1");

		for (double jsfA2N_1 = 0.1; jsfA2N_1 <= 1.0; jsfA2N_1 += 0.1) {
			final RatingTestCase.RatingResult r1 = RTC_Negation.newTest(
					rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2,
					sel3, jsfA2N_1, jsfA1B).getRatingResult();

			writer.addValues(jsfA2N_1, r1.getMemoryCost(), r1.getRuntimeCost());

			// for (double jsfA2N_2 = 0.1; jsfA2N_2 <= jsfA2N_1; jsfA2N_2 +=
			// 0.1) {
			// final RatingTestCase.RatingResult r2 = RTC_Negation.newTest(
			// rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
			// sel2, sel3, jsfA2N_2, jsfA1B).getRatingResult();
			//
			// assertThat(
			// "Memory cost should be lower when more gets filtered out",
			// r1.getMemoryCost(), lessOrEqual(r2.getMemoryCost()));
			// // assertThat("Cpu cost should be lower when sharing nodes",
			// // r1.getRuntimeCost(), lessOrEqual(r2.getRuntimeCost()));
			// }
		}
		writer.close();
	}

	@Test
	public void testJoinThreeAlphas() throws NodeException {
		for (int rootSize = 100; rootSize <= 1000; rootSize += 100) {
			for (int rootFInsert = 10; rootFInsert <= 100; rootFInsert += 10) {
				for (int rootFDelete = 10; rootFDelete <= 100; rootFDelete += 10) {
					for (int rootTPP = 10; rootTPP <= 100; rootTPP += 30) {
						testJoinThreeAlphas(rootSize, rootFInsert, rootFDelete,
								rootTPP);
					}
				}
			}
		}
	}

	public void testJoinThreeAlphas(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP) throws NodeException {
		final double sel1 = 0.5, sel2 = 0.5, sel3 = 0.5;
		final double jsfA1A2 = 0.49999999, jsfLA3 = 0.5;
		final double jsfA2A3 = 0.5, jsfRA1 = jsfA1A2 * jsfLA3 / jsfA2A3;
		final RatingTestCase.RatingResult l2r = RTC_JoinThreeAlphasL2R.newTest(
				rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2, sel3,
				jsfA1A2, jsfLA3).getRatingResult();
		final RatingTestCase.RatingResult r2l = RTC_JoinThreeAlphasR2L.newTest(
				rootSize, rootFInsert, rootFDelete, rootTPP, sel1, sel2, sel3,
				jsfA2A3, jsfRA1).getRatingResult();
		assertThat(
				"Memory cost should be lower when first joining to smaller exp result",
				l2r.getMemoryCost(), lessOrEqual(r2l.getMemoryCost()));
		assertThat(
				"Cpu cost should be lower when first joining to smaller exp result",
				l2r.getRuntimeCost(), lessOrEqual(r2l.getRuntimeCost() + 1));
	}

	@Test
	public void testJoinThreeAlphasMulti() throws NodeException {
		for (int rootSize = 100; rootSize <= 1000; rootSize += 450) {
			for (int rootFInsert = 10; rootFInsert <= 100; rootFInsert += 45) {
				for (int rootFDelete = 10; rootFDelete <= 100; rootFDelete += 45) {
					for (int rootTPP = 10; rootTPP <= 100; rootTPP += 45) {
						for (int jsf1 = 1; jsf1 <= 10; jsf1 += 1) {
							for (int jsf2 = 1; jsf2 <= 10; jsf2 += 1) {
								testJoinThreeAlphasMulti(rootSize, rootFInsert,
										rootFDelete, rootTPP, jsf1 / 10.0,
										jsf2 / 10.0, 1);
								testJoinThreeAlphasMulti(rootSize, rootFInsert,
										rootFDelete, rootTPP, jsf1 / 10.0, 1,
										jsf2 / 10.0);
								testJoinThreeAlphasMulti(rootSize, rootFInsert,
										rootFDelete, rootTPP, 1, jsf1 / 10.0,
										jsf2 / 10.0);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void testJoinThreeAlphasMulti(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double jsfA1A2, final double jsfA1A3,
			final double jsfA2A3) throws NodeException {
		final double sel1 = 0.5, sel2 = 0.5, sel3 = 0.5;
		final RatingTestCase.RatingResult multi = RTC_JoinThreeAlphasMulti
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, jsfA1A2, jsfA1A3, jsfA2A3)
				.getRatingResult();
		final RatingTestCase.RatingResult j12_3 = RTC_JoinThreeAlphasL2R
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, jsfA1A2, jsfA1A3 * jsfA2A3)
				.getRatingResult();
		final RatingTestCase.RatingResult j32_1 = RTC_JoinThreeAlphasR2L
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, jsfA2A3, jsfA1A2 * jsfA1A3)
				.getRatingResult();
		final RatingTestCase.RatingResult j13_2 = RTC_JoinThreeAlphasO2I
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, jsfA1A3, jsfA1A2 * jsfA2A3)
				.getRatingResult();
		assertThat(
				"Memory cost should always be lower for one multi-input node than for two two-input nodes",
				multi.getMemoryCost(),
				org.hamcrest.CoreMatchers.allOf(
						lessOrEqual(j12_3.getMemoryCost()),
						lessOrEqual(j32_1.getMemoryCost()),
						lessOrEqual(j13_2.getMemoryCost())));
		// no assertion for cpu cost
		// assertThat(
		// "Cpu cost should be lower for one multi-input node than for one combination of two two-input nodes",
		// multi.getRuntimeCost(),
		// org.hamcrest.CoreMatchers.anyOf(
		// lessOrEqual(j12_3.getRuntimeCost()),
		// lessOrEqual(j32_1.getRuntimeCost()),
		// lessOrEqual(j13_2.getRuntimeCost())));
	}

	@Test
	public void testJoinFourAlphasWithOrWithoutSharing() throws NodeException {
		final double sel1 = 0.5, sel2 = 0.5, sel3 = 0.5, sel4 = 0.5;
		final double jsfA1A2 = 0.5, jsfA1A3 = 1, jsfA2A3 = 0.5, jsfA2A4 = 1, jsfA3A4 = 0.5;
		final double jsfA1A23 = jsfA1A2, jsfA4A23 = jsfA3A4;
		final RatingTestCase.RatingResult noshare = RTC_JoinFourAlphasNoShare
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, sel4, jsfA1A2, jsfA1A3, jsfA2A3, jsfA2A4,
						jsfA3A4).getRatingResult();
		final RatingTestCase.RatingResult share = RTC_JoinFourAlphasShare
				.newTest(rootSize, rootFInsert, rootFDelete, rootTPP, sel1,
						sel2, sel3, sel4, jsfA2A3, jsfA1A23, jsfA4A23)
				.getRatingResult();
		assertThat(
				"Mem cost should be higher when sharing partial calculations",
				share.getMemoryCost(), greaterOrEqual(noshare.getMemoryCost()));
		assertThat(
				"Cpu cost should be lower when sharing partial calculations",
				share.getRuntimeCost(), lessOrEqual(noshare.getRuntimeCost()));
	}
}
