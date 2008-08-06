package org.jamocha.tests.testcases.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.DummyNode;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.FactTupleImpl;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.joinfilter.FieldAddress;
import org.jamocha.engine.nodes.joinfilter.GeneralizedFieldComparator;
import org.jamocha.engine.nodes.joinfilter.LeftFieldAddress;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.parser.EvaluationException;

public class JTCMultiJoin extends TestCase {

	public void test1() {
		Engine e = new Engine();
		
		MultiBetaJoinNode node = new MultiBetaJoinNode(1, e.getWorkingMemory(), e.getNet() );

		
		
		Fact f1 = new Deffact("wurst;name=bockwurst;gewicht=100",e);
		Fact f2 = new Deffact("wurst;name=bratwurst;gewicht=140",e);
		
		Fact f3 = new Deffact("bier;name=diebels;gewicht=100",e);
		Fact f4 = new Deffact("bier;name=bit;gewicht=140",e);

		Fact f5 = new Deffact("salat;name=kartoffelsalat;gewicht=100",e); // D
		Fact f6 = new Deffact("salat;name=gurkensalat;gewicht=140",e); // D

		Fact f7 = new Deffact("kaese;name=hollaender;gewicht=100",e); // A
		Fact f8 = new Deffact("kaese;name=stinkekaese;gewicht=140",e); // A

		Fact f9 = new Deffact("kaeseverschnitt;name=hollaender;gewicht=200",e); // E
		Fact f0 = new Deffact("kaeseverschnitt;name=stinkekaese;gewicht=240",e);     // E

		
		Fact[] fl1 = {f1, f3};                                         // B
		Fact[] fl2 = {f2, f4};                                         // C
		
		FactTuple ft1 = new FactTupleImpl(fl1);
		FactTuple ft2 = new FactTupleImpl(fl2);
		
		Node parent1 = new DummyNode(100,e.getWorkingMemory(), e.getNet());
		Node parent2 = new DummyNode(101,e.getWorkingMemory(), e.getNet());
		Node parent3 = new DummyNode(102,e.getWorkingMemory(), e.getNet());
		Node parent4 = new DummyNode(103,e.getWorkingMemory(), e.getNet());
		Node parent5 = new DummyNode(104,e.getWorkingMemory(), e.getNet());
		
		int slotNr1 = 0;
		int slotNr2 = 0;
		
		for (int i = 0; i<2; i++) {
			if (f7.getTemplate().getSlot(i).getName().equals("name")) slotNr1 = i;
			if (f0.getTemplate().getSlot(i).getName().equals("name")) slotNr2 = i;
		}
		
		
		FieldAddress field1 = new LeftFieldAddress(0,slotNr1);
		FieldAddress field2 = new LeftFieldAddress(6,slotNr2);
		
		
		node.addFilter( new GeneralizedFieldComparator(
				"foo",field1, Constants.NOTEQUAL, field2
				));

		
		try {
			parent1.addChild(node);
			parent2.addChild(node);
			parent3.addChild(node);
			parent4.addChild(node);
			parent5.addChild(node);
			
			node.activate();
			parent1.activate();
			parent2.activate();
			parent3.activate();
			parent4.activate();
			parent5.activate();
			
			parent1.addWME(null, f7);
			parent1.addWME(null, f8);
			parent2.addWME(null, ft1);
			parent3.addWME(null, ft2);
			parent4.addWME(null, f5);
			parent4.addWME(null, f6);
			parent5.addWME(null, f9);
			parent5.addWME(null, f0);
		} catch (NodeException e1) {
			fail(e1.getMessage());
		}
		
		final int NUMBER_RESULTS = 4;
		
		assertEquals(NUMBER_RESULTS, e.getWorkingMemory().size(node));

		Collection<String> resStrings = new ArrayList<String>();

		Iterator<WorkingMemoryElement> itr = node.memory().iterator();
		for( int i = 0; i<NUMBER_RESULTS; i++) {
			Fact[] wme = itr.next().getFactTuple().getFacts();
			
			String s = "";
			
			for (int i2=0 ; i2<7; i2++) {
				try {
					s += wme[i2].getSlotValue("name");
				} catch (EvaluationException e1) {
					fail(e1.getMessage());
				}
			}
			
			resStrings.add(s);
		}
		
		
		
		assertTrue(resStrings.contains("stinkekaesebockwurstdiebelsbratwurstbitkartoffelsalathollaender"));
		assertTrue(resStrings.contains("stinkekaesebockwurstdiebelsbratwurstbitgurkensalathollaender"));
		assertTrue(resStrings.contains("hollaenderbockwurstdiebelsbratwurstbitgurkensalatstinkekaese"));
		assertTrue(resStrings.contains("hollaenderbockwurstdiebelsbratwurstbitkartoffelsalatstinkekaese"));
		
	}
	
	
	
	
}
