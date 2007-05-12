package org.jamocha.rete;

import java.util.Iterator;
import java.util.Vector;

import org.jamocha.rete.nodes.FactTuple;


public class BetaMemory implements Iterable<FactTuple>{
	
	protected Vector<FactTuple> factTuples = null;
	
	public BetaMemory() {
		super();
		factTuples = new Vector<FactTuple>();
	}
	
	public boolean contains(FactTuple forTest){
		return factTuples.contains(forTest);
	}
	
	public boolean remove(FactTuple forRemove){
		return factTuples.remove(forRemove);
	}
	
	public void clear(){
		factTuples.clear();
	}
	
	public void add(FactTuple newTuple) {
		factTuples.add(newTuple);
	}

	public Iterator<FactTuple> iterator() {
		return factTuples.iterator();
	}
	
	public String toPPString(){
		StringBuffer result = new StringBuffer();
		result.append("[");
		for ( FactTuple t : factTuples ){
			result.append(t.toPPString());
			result.append(" , ");
		}
		result.append("]");
		return result.toString();
	}
}