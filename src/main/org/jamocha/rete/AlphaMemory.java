package org.jamocha.rete;

import java.util.Iterator;
import java.util.Vector;


public class AlphaMemory extends AbstractMemory implements Iterable<Fact>{

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
	
	public Iterator<Fact> iterator() {
		return facts.iterator();
	}

	@Override
	protected String contentToString() {
		StringBuffer result = new StringBuffer();
		for ( Fact t : facts ){
			result.append("   ");
			result.append(t.toPPString());
			result.append("\n");
		}
		return result.toString();
	}

	@Override
	public int getSize() {
		return facts.size();
	}

	public boolean isEmpty(){
		return facts.size() == 0;
	}
	
	
	
	
}