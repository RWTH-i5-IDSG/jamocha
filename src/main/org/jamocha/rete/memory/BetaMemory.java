package org.jamocha.rete.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Fact;
import org.jamocha.rete.nodes.FactTuple;

public class BetaMemory extends AbstractMemory implements Iterable<FactTuple> {

	protected Collection<FactTuple> factTuples = null;

	public BetaMemory() {
		super();
		factTuples = new ArrayList<FactTuple>();
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

	
	protected String contentToString(int length) {
		StringBuffer result = new StringBuffer();
		int i = 0;
		for (FactTuple t : factTuples) {
			if (i == length) {
				result.append("and ").append(factTuples.size()-length).append(" more...");
				break;
			}
			result.append("   ");
			result.append(t.toString());
			result.append("\n");
			i++;
		}
		return result.toString();
	}

	
	
	@Override
	protected String contentToString() {
		StringBuffer result = new StringBuffer();
		for (FactTuple t : factTuples) {
			result.append("   ");
			result.append(t.toString());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public int getSize() {
		return factTuples.size();
	}

	public List<FactTuple> getPrefixMatchingTuples(FactTuple input){
		ArrayList<FactTuple> result= new ArrayList<FactTuple>();
		for (FactTuple ourTuple : factTuples){
			if (ourTuple.isMySubTuple(input)) result.add(ourTuple);
		}
		return result;
	}
	
	public List<FactTuple> getPostfixMatchingTuples(Fact input){
		ArrayList<FactTuple> result= new ArrayList<FactTuple>();
		for (FactTuple ourTuple : factTuples){
			if (ourTuple.isMyLastFact(input)) result.add(ourTuple);
		}
		return result;
	}
}