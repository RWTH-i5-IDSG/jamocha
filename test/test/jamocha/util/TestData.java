/*
 * Copyright 2002-2013 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;

/**
 * Class that has multiple ParameterSupplier to supply test data for Theory tests.
 * <p>
 * The Interfaces have @RetentionPolicy.RUNTIME and @ParametersSuppliedBy() annotations and define a
 * new annotation which can be used in the Theory parameter definition to get the data from the
 * \@ParameterSuppliedBy class into the Theory. The classes implementing ParameterSupplier implement
 * the interface and return test data in a list.
 * </p>
 * <p>
 * All combinations of input are tested on a Theory.
 * </p>
 * 
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 */
public class TestData {

	/*
	 * **************OBJECTS**************
	 */

	/*
	 * Interfaces
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(RandomStuffSupplier.class)
	public @interface SomeStuff {
	}

	/*
	 * Supplier
	 */

	// List of random stuff
	public static class RandomStuffSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature signature) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			list.add(PotentialAssignment.forValue("", new Object[] { -100L, 50L, 1L, 11L }));
			list.add(PotentialAssignment.forValue("", new Object[] { null }));
			list.add(PotentialAssignment.forValue("", new Object[] { "OMGWTFBBQ", "TEST test!!!" }));
			list.add(PotentialAssignment.forValue("", new Object[] { -100L }));
			list.add(PotentialAssignment.forValue("", new Object[] { 0.3562 }));
			list.add(PotentialAssignment.forValue("", new Object[] {}));
			return list;
		}

	}

	/*
	 * **************LONGS**************
	 */

	/*
	 * Interfaces
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(RandomLongsSupplier.class)
	public @interface LotsOfRandomLongs {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(SomeLongsSupplier.class)
	public @interface ListOfLongs {
	}

	/*
	 * Supplier
	 */

	// Long List of random Longs
	public static class RandomLongsSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature signature) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			Random randomGen = new Random();
			for (int i = 0; i < 20; ++i) {
				list.add(PotentialAssignment.forValue("", randomGen.nextLong()));
			}
			return list;
		}

	}

	// Just some Longs for equals testing and similar
	public static class SomeLongsSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature param) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			list.add(PotentialAssignment.forValue("", -100L));
			list.add(PotentialAssignment.forValue("", -71L));
			list.add(PotentialAssignment.forValue("", -33L));
			list.add(PotentialAssignment.forValue("", -11L));
			list.add(PotentialAssignment.forValue("", -3L));
			list.add(PotentialAssignment.forValue("", 0L));
			list.add(PotentialAssignment.forValue("", 3L));
			list.add(PotentialAssignment.forValue("", 63L));
			list.add(PotentialAssignment.forValue("", 29138642L));
			list.add(PotentialAssignment.forValue("", 132389822L));
			list.add(PotentialAssignment.forValue("", 27L));
			list.add(PotentialAssignment.forValue("", 99L));
			list.add(PotentialAssignment.forValue("", -100L));
			list.add(PotentialAssignment.forValue("", -71L));
			list.add(PotentialAssignment.forValue("", -33L));
			list.add(PotentialAssignment.forValue("", -11L));
			list.add(PotentialAssignment.forValue("", -3L));
			list.add(PotentialAssignment.forValue("", 0L));
			list.add(PotentialAssignment.forValue("", 3L));
			list.add(PotentialAssignment.forValue("", 63L));
			list.add(PotentialAssignment.forValue("", 29138642L));
			list.add(PotentialAssignment.forValue("", 132389822L));
			list.add(PotentialAssignment.forValue("", 27L));
			list.add(PotentialAssignment.forValue("", 99L));
			list.add(PotentialAssignment.forValue("", -100L));
			list.add(PotentialAssignment.forValue("", -71L));
			list.add(PotentialAssignment.forValue("", -33L));
			list.add(PotentialAssignment.forValue("", -11L));
			list.add(PotentialAssignment.forValue("", -3L));
			list.add(PotentialAssignment.forValue("", 0L));
			list.add(PotentialAssignment.forValue("", 3L));
			list.add(PotentialAssignment.forValue("", 63L));
			list.add(PotentialAssignment.forValue("", 29138642L));
			list.add(PotentialAssignment.forValue("", 132389822L));
			list.add(PotentialAssignment.forValue("", 27L));
			list.add(PotentialAssignment.forValue("", 99L));
			return list;
		}

	}

	/*
	 * **************DOUBLES**************
	 */

	/*
	 * Interfaces
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(RandomDoublesSupplier.class)
	public @interface LotsOfRandomDoubles {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(SomeDoublesSupplier.class)
	public @interface ListOfDoubles {
	}

	/*
	 * Supplier
	 */

	public static class RandomDoublesSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature signature) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			Random randomGen = new Random();
			for (int i = 0; i < 20; ++i) {
				list.add(PotentialAssignment.forValue("", randomGen.nextGaussian()));
			}
			return list;
		}

	}

	// Just some Longs for equals testing and similar
	public static class SomeDoublesSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature param) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			list.add(PotentialAssignment.forValue("", -100.394903490283610835));
			list.add(PotentialAssignment.forValue("", -71.));
			list.add(PotentialAssignment.forValue("", -33.5));
			list.add(PotentialAssignment.forValue("", -11.));
			list.add(PotentialAssignment.forValue("", -3.));
			list.add(PotentialAssignment.forValue("", 0.));
			list.add(PotentialAssignment.forValue("", 3.1431));
			list.add(PotentialAssignment.forValue("", 63.));
			list.add(PotentialAssignment.forValue("", 29138642.9352));
			list.add(PotentialAssignment.forValue("", 132389822.));
			list.add(PotentialAssignment.forValue("", 27.));
			list.add(PotentialAssignment.forValue("", 99.));
			list.add(PotentialAssignment.forValue("", -100.394903490283610835));
			list.add(PotentialAssignment.forValue("", -71.));
			list.add(PotentialAssignment.forValue("", -33.5));
			list.add(PotentialAssignment.forValue("", -11.));
			list.add(PotentialAssignment.forValue("", -3.));
			list.add(PotentialAssignment.forValue("", 0.));
			list.add(PotentialAssignment.forValue("", 3.1431));
			list.add(PotentialAssignment.forValue("", 63.));
			list.add(PotentialAssignment.forValue("", 29138642.9352));
			list.add(PotentialAssignment.forValue("", 132389822.));
			list.add(PotentialAssignment.forValue("", 27.));
			list.add(PotentialAssignment.forValue("", 99.));
			list.add(PotentialAssignment.forValue("", -100.394903490283610835));
			list.add(PotentialAssignment.forValue("", -71.));
			list.add(PotentialAssignment.forValue("", -33.5));
			list.add(PotentialAssignment.forValue("", -11.));
			list.add(PotentialAssignment.forValue("", -3.));
			list.add(PotentialAssignment.forValue("", 0.));
			list.add(PotentialAssignment.forValue("", 3.1431));
			list.add(PotentialAssignment.forValue("", 63.));
			list.add(PotentialAssignment.forValue("", 29138642.9352));
			list.add(PotentialAssignment.forValue("", 132389822.));
			list.add(PotentialAssignment.forValue("", 27.));
			list.add(PotentialAssignment.forValue("", 99.));
			return list;
		}

	}

	/*
	 * **************STRINGS**************
	 */

	/*
	 * Interfaces
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(SomeStringsSupplier.class)
	public @interface ListOfStrings {
	}

	/*
	 * Supplier
	 */

	public static class SomeStringsSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature param) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			list.add(PotentialAssignment.forValue("", "foo"));
			list.add(PotentialAssignment.forValue("", "bar"));
			list.add(PotentialAssignment.forValue("", "foo"));
			list.add(PotentialAssignment.forValue("", "bar"));
			list.add(PotentialAssignment.forValue("", "foobar"));
			list.add(PotentialAssignment.forValue("", "foobar"));
			list.add(PotentialAssignment.forValue("", "foobar"));
			list.add(PotentialAssignment.forValue("", "whut?"));
			list.add(PotentialAssignment.forValue("", "OMGWTFBBQ"));
			list.add(PotentialAssignment.forValue("", "muahhahahahahahahaha"));
			list.add(PotentialAssignment.forValue("", "ijrobbeoubgt3t9hboebalnpajtih39"));
			list.add(PotentialAssignment.forValue("", "ijrobbeoubgt3t9hboebalnpajtih39"));
			return list;
		}

	}

	/*
	 * **************BOOLEANS**************
	 */

	/*
	 * Interfaces
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@ParametersSuppliedBy(SomeBooleanSupplier.class)
	public @interface ListOfBooleans {
	}

	/*
	 * Supplier
	 */

	public static class SomeBooleanSupplier extends ParameterSupplier {

		@Override
		public List<PotentialAssignment> getValueSources(ParameterSignature arg0) {
			ArrayList<PotentialAssignment> list = new ArrayList<>();
			list.add(PotentialAssignment.forValue("", new Boolean(true)));
			list.add(PotentialAssignment.forValue("", new Boolean(true)));
			list.add(PotentialAssignment.forValue("", new Boolean(true)));
			list.add(PotentialAssignment.forValue("", new Boolean(false)));
			list.add(PotentialAssignment.forValue("", new Boolean(false)));
			list.add(PotentialAssignment.forValue("", new Boolean(false)));
			list.add(PotentialAssignment.forValue("", new Boolean(false)));
			return list;
		}

	}

}
