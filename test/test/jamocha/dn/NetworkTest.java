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
package test.jamocha.dn;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.FunctionDictionary;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 * @see Network
 */
public class NetworkTest {

	final Function<Double> plusD = FunctionDictionary.<Double> lookup("+", SlotType.DOUBLE,
			SlotType.DOUBLE);
	final Function<Double> minusD = FunctionDictionary.<Double> lookup("-", SlotType.DOUBLE,
			SlotType.DOUBLE);
	final Predicate eqD = FunctionDictionary.lookupPredicate("=", SlotType.DOUBLE, SlotType.DOUBLE);
	final Function<Long> plusL = FunctionDictionary
			.<Long> lookup("+", SlotType.LONG, SlotType.LONG);
	final Function<Long> minusL = FunctionDictionary.<Long> lookup("-", SlotType.LONG,
			SlotType.LONG);
	final Predicate lessL = FunctionDictionary.lookupPredicate("<", SlotType.LONG, SlotType.LONG);
	final Predicate eqL = FunctionDictionary.lookupPredicate("=", SlotType.LONG, SlotType.LONG);
	final Predicate eqS = FunctionDictionary.lookupPredicate("=", SlotType.STRING, SlotType.STRING);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FunctionDictionary.load();
	}

	@Before
	public void init() {
	}

	private static boolean tryToShareNode(final Network network, final Filter filter)
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method tryToShareNode =
				Network.class.getDeclaredMethod("tryToShareNode", Filter.class);
		tryToShareNode.setAccessible(true);
		return (Boolean) tryToShareNode.invoke((Object) network, (Object) filter);
	}

	@Test
	public void testTryToShareNodeSimpleAlphaCase() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
	}

	@Test
	public void testBuildRule() {
		// build some filters with the given predicates and build them with buildrule
		// After iterate the network and check if everything is in order.
		fail("Not yet implemented");
	}

}
