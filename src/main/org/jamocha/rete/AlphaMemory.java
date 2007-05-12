package org.jamocha.rete;

import java.util.Iterator;
import java.util.Vector;


public class AlphaMemory implements Iterable<Fact>{

	protected Vector<Fact> facts = null;
	
	public AlphaMemory() {
		super();
		facts = new Vector<Fact>();
	}
	
	public boolean contains(Fact forTest){
		return facts.contains(forTest);
	}
	
	public boolean remove(Fact forRemove){
		return facts.remove(forRemove);
	}
	
	public void clear(){
		facts.clear();
	}
	
	public void add(Fact newTuple) {
		facts.add(newTuple);
	}


	public String toPPString(){
		StringBuffer result = new StringBuffer();
		result.append("[");
		for ( Fact t : facts ){
			result.append(t.toPPString());
			result.append(" , ");
		}
		result.append("]");
		return result.toString();
	}
	
	
	public Iterator<Fact> iterator() {
		return facts.iterator();
	}
	
	
	
	
}