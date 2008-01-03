package org.jamocha.rete.nodes;

import org.jamocha.rete.Fact;
import org.jamocha.rete.memory.WorkingMemoryElement;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de>
 * A sequence of facts.
 */
public interface FactTuple extends WorkingMemoryElement, Iterable<Fact>{

	/**
	 * returns the number of facts in the sequence 
	 */
	int length();
	
	/**
	 * returns the facts themselves as array
	 */
	Fact[] getFacts();
	
	/**
	 * gets the fact at a given position 
	 */
	Fact getFact(int index);
	
	/**
	 * returns the new FactTuple, which emerges from the old one by
	 * appending a given fact 
	 */
	FactTuple appendFact(Fact fact);
	
	/**
	 * determines, whether 'smallerOne' is a prefix tuple of myself
	 */
	boolean isMySubTuple(FactTuple smallerOne);
	
	/**
	 * determines, whether 'f' is the last fact of myself 
	 */
	boolean isMyLastFact(Fact f);
	
}
