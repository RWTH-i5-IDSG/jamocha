package org.jamocha.rete.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Fact;


public class AlphaMemory extends AbstractMemory implements Iterable<Fact>{

	protected Collection<Fact> facts = null;
	
	public AlphaMemory() {
		super();
		facts = new ArrayList<Fact>();
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
			result.append(t.toString());
			result.append("\n");
		}
		return result.toString();
	}
	
	protected String contentToString(int length) {
		StringBuffer result = new StringBuffer();
		int i = 0;
		for ( Fact t : facts ){
			if (i == length) {
				result.append("and ").append(facts.size()-length).append(" more");
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
	public int getSize() {
		return facts.size();
	}

	public boolean isEmpty(){
		return facts.size() == 0;
	}
	
	
	
	
}