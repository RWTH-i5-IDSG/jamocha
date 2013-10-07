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
package test.jamocha.dn.nodes;

import java.util.LinkedList;
import java.util.List;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.filter.ConstantLeaf;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionWithArguments;
import org.jamocha.filter.FunctionWithArgumentsComposite;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathLeaf;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.PredicateWithArgumentsComposite;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * 
 */
public class TokenProcessingTest {
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public static PredicateWithArguments combine() {
		return null;
	}

	static class PredicateBuilder {
		final Predicate predicate;
		final List<FunctionWithArguments> args = new LinkedList<>();

		public PredicateBuilder(final Predicate predicate) {
			this.predicate = predicate;
		}

		public PredicateBuilder addPath(final Path path, final SlotAddress slot) {
			final SlotType[] paramTypes = this.predicate.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != path.getTemplateSlotType(slot)) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(new PathLeaf(path, slot));
			return this;
		}

		public PredicateBuilder addConstant(final Object value, final SlotType type) {
			final SlotType[] paramTypes = this.predicate.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != type) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(new ConstantLeaf(value, type));
			return this;
		}

		public PredicateBuilder addFunction(final FunctionWithArguments function) {
			final SlotType[] paramTypes = this.predicate.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != function.getReturnType()) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(function);
			return this;
		}

		public PredicateWithArguments build() {
			if (this.predicate.getParamTypes().length != this.args.size()) {
				throw new IllegalArgumentException("Wrong number of arguments!");
			}
			return new PredicateWithArgumentsComposite(this.predicate,
					this.args.toArray(new FunctionWithArguments[this.args.size()]));
		}
	}

	static class FunctionBuilder {
		final Function<?> function;
		final List<FunctionWithArguments> args = new LinkedList<>();

		public FunctionBuilder(final Function<?> function) {
			this.function = function;
		}

		public FunctionBuilder addPath(final Path path, final SlotAddress slot) {
			final SlotType[] paramTypes = this.function.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != path.getTemplateSlotType(slot)) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(new PathLeaf(path, slot));
			return this;
		}

		public FunctionBuilder addConstant(final Object value, final SlotType type) {
			final SlotType[] paramTypes = this.function.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != type) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(new ConstantLeaf(value, type));
			return this;
		}

		public FunctionBuilder addFunction(final FunctionWithArguments function) {
			final SlotType[] paramTypes = this.function.getParamTypes();
			if (paramTypes.length != this.args.size()) {
				throw new IllegalArgumentException("All arguments already set!");
			}
			if (paramTypes[this.args.size() + 1] != function.getReturnType()) {
				throw new IllegalArgumentException("Wrong argument type!");
			}
			this.args.add(function);
			return this;
		}

		public FunctionWithArguments build() {
			if (this.function.getParamTypes().length != this.args.size()) {
				throw new IllegalArgumentException("Wrong number of arguments!");
			}
			return new FunctionWithArgumentsComposite(this.function,
					this.args.toArray(new FunctionWithArguments[this.args.size()]));
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	@Test
	public void testTokenProcessing() {
		final Network network = Network.DEFAULTNETWORK;
		final Template t1 = new Template(SlotType.LONG, SlotType.STRING, SlotType.BOOLEAN);
		final Path p1 = new Path(t1);
		final SlotAddress slotLong = new SlotAddress(0), slotBool = new SlotAddress(2);

		final Predicate lessLongLong =
				TODODatenkrakeFunktionen.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
		final Predicate eqBoolBool =
				TODODatenkrakeFunktionen.lookupPredicate("=", SlotType.BOOLEAN, SlotType.BOOLEAN);

		final Filter filter =
				new Filter(new PredicateBuilder(eqBoolBool)
						.addPath(p1, slotBool)
						.addFunction(
								new FunctionBuilder(lessLongLong).addPath(p1, slotLong)
										.addConstant(3L, SlotType.LONG).build()).build());

	}
}
