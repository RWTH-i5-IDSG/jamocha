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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.javaimpl.SlotAddress;
import org.jamocha.dn.nodes.AlphaNode;
import org.jamocha.filter.Filter;
import org.jamocha.filter.Function;
import org.jamocha.filter.Path;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.PredicateWithArguments;
import org.jamocha.filter.TODODatenkrakeFunktionen;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.jamocha.util.FunctionBuilder;
import test.jamocha.util.PredicateBuilder;

/**
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 * 
 * @see Network
 */
public class NetworkTest {

	Network net;
	Path p1 = new Path(new Template(SlotType.STRING, SlotType.LONG));
	Path p2 = new Path(new Template(SlotType.LONG));
	Path p3 = new Path(new Template(SlotType.LONG));
	Path p4 = new Path(new Template(SlotType.LONG));
	Path p5 = new Path(new Template(SlotType.STRING));
	Path p6 = new Path(new Template(SlotType.STRING, SlotType.LONG));	
	SlotAddress a1 = new SlotAddress(0);
	SlotAddress a2 = new SlotAddress(0);
	SlotAddress a3 = new SlotAddress(0);
	SlotAddress a4 = new SlotAddress(0);
	SlotAddress a5 = new SlotAddress(1);
	SlotAddress a6 = new SlotAddress(0);
	PredicateWithArguments f, g, h, i, j, k, l;
	Function<?> plusD = TODODatenkrakeFunktionen.lookup("+", SlotType.DOUBLE, SlotType.DOUBLE);
	Function<?> minusD = TODODatenkrakeFunktionen.lookup("-", SlotType.DOUBLE, SlotType.DOUBLE);
	Function<?> eqD = TODODatenkrakeFunktionen.lookup("=", SlotType.DOUBLE, SlotType.DOUBLE);
	Function<?> plusL = TODODatenkrakeFunktionen.lookup("+", SlotType.LONG, SlotType.LONG);
	Function<?> minusL = TODODatenkrakeFunktionen.lookup("-", SlotType.LONG, SlotType.LONG);
	Function<?> lessL = TODODatenkrakeFunktionen.lookup("<", SlotType.LONG, SlotType.LONG);
	Function<?> eqL = TODODatenkrakeFunktionen.lookup("=", SlotType.LONG, SlotType.LONG);
	Function<?> eqS = TODODatenkrakeFunktionen.lookup("=", SlotType.STRING, SlotType.STRING);
	Filter a, b;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TODODatenkrakeFunktionen.load();
	}

	@Before
	public void init() {
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
						.addConstant("Max Mustermann", SlotType.STRING).addPath(p6, a6).build();
		j =
				new PredicateBuilder((Predicate) lessL).addConstant(18L, SlotType.LONG)
						.addPath(p1, a5).build();
		k =
				new PredicateBuilder((Predicate) lessL)
						.addConstant(50000, SlotType.LONG)
						.addFunction(
								new FunctionBuilder(plusL).addPath(p2, a3).addPath(p3, a4).build())
						.build();
		net = Network.DEFAULTNETWORK;
	}

	@Test
	public void testTryToShareNode() {
		a = new Filter(f);
		try {
			final Class<Network> networkClazz = Network.class;
			final Method tryToShareNode = networkClazz.getDeclaredMethod("tryToShareNode", Filter.class, Path[].class);
			tryToShareNode.setAccessible(true);
			net.getRootNode().addPaths(net,p1,p6);
			assertFalse((boolean)tryToShareNode.invoke(net, a, new Path[]{p1}));
			new AlphaNode(net, a);
			assertTrue((boolean)tryToShareNode.invoke(net, i, new Path[]{p6}));	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed due to Exception: " + e.getMessage());
		}
	}

	@Test
	public void testBuildRule() {
		//build some filters with the given predicates and build them with buildrule
		//After iterate the network and check if everything is in order.
		fail("Not yet implemented");
	}

}
