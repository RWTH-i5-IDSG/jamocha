/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package test.jamocha.engine.filter.functions;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamocha.engine.memory.SlotType;
import org.jamocha.filter.Function;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 */
@RunWith(Theories.class)
public class PlusTest {

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(RandomLongsSupplier.class)
	public @interface RandomLong {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(RandomDoublesSupplier.class)
	public @interface RandomDouble {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	public static class RandomLongsSupplier extends ParameterSupplier {
		@Override
		public List<PotentialAssignment> getValueSources(
				ParameterSignature signature) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			Random randomGen = new Random();
			for (int i = 0; i < 100; ++i) {
				list.add(PotentialAssignment.forValue("", randomGen.nextLong()));
			}
			return list;
		}
	}

	public static class RandomDoublesSupplier extends ParameterSupplier {
		@Override
		public List<PotentialAssignment> getValueSources(
				ParameterSignature signature) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			Random randomGen = new Random();
			for (int i = 0; i < 100; ++i) {
				list.add(PotentialAssignment.forValue("",
						randomGen.nextGaussian()));
			}
			return list;
		}
	}

	private Function plusL, plusD;
	
	@Before
	public void setUp() {
		plusL = TODODatenkrakeFunktionen.lookup("+", SlotType.LONG,
				SlotType.LONG);
		plusD = TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE,
				SlotType.DOUBLE);
	}

	@Theory
	public void testLong(Long left,Long right) {
		assertEquals((Long)(left + right), (Long) (plusL.evaluate(left, right)));
	}
	
	@Theory
	public void testDouble(Double left, Double right) {
		assertEquals((Double)(left + right), (Double) (plusD.evaluate(left, right)));
	}

}
