package org.jamocha.rete;

import java.util.Iterator;
import java.util.Vector;

import org.jamocha.rete.nodes.FactTuple;

public class BetaMemory extends AbstractMemory implements Iterable<FactTuple> {

	protected Vector<FactTuple> factTuples = null;

	public BetaMemory() {
		super();
		factTuples = new Vector<FactTuple>();
	}

	public boolean contains(FactTuple forTest) {
		return factTuples.contains(forTest);
	}

	public boolean remove(FactTuple forRemove) {
		return factTuples.remove(forRemove);
	}

	public void clear() {
		factTuples.clear();
	}

	public void add(FactTuple newTuple) {
		factTuples.add(newTuple);
	}

	public Iterator<FactTuple> iterator() {
		return factTuples.iterator();
	}

	@Override
	protected String contentToString() {
		StringBuffer result = new StringBuffer();
		for (FactTuple t : factTuples) {
			result.append("   ");
			result.append(t.toPPString());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public int getSize() {
		return factTuples.size();
	}

	public Vector<FactTuple> getPrefixMatchingTuples(FactTuple input){
		Vector<FactTuple> result= new Vector<FactTuple>();
		for (FactTuple ourTuple : factTuples){
			if (ourTuple.isMySubTuple(input)) result.add(ourTuple);
		}
		return result;
	}
	
	public Vector<FactTuple> getPostfixMatchingTuples(Fact input){
		Vector<FactTuple> result= new Vector<FactTuple>();
		for (FactTuple ourTuple : factTuples){
			if (ourTuple.isMyLastFact(input)) result.add(ourTuple);
		}
		return result;
	}
}