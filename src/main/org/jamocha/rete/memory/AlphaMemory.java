package org.jamocha.rete.memory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jamocha.rete.Fact;


public class AlphaMemory extends AbstractMemory implements Iterable<Fact>{

	protected Map<Long,Fact> facts = null;
	
	public AlphaMemory() {
		super();
		facts = new LinkedHashMap<Long,Fact>();
	}
	
	
	public Fact remove(Fact forRemove){
		return facts.remove(forRemove.getFactId());
	}
	
	public void clear(){
		facts.clear();
	}
	
	public void add(Fact newTuple) {
		facts.put(newTuple.getFactId(), newTuple);
	}
	
	public Iterator<Fact> iterator() {
		return facts.values().iterator();
	}

	@Override
	protected String contentToString() {
		StringBuffer result = new StringBuffer();
		for ( Fact t : facts.values() ){
			result.append("   ");
			result.append(t.toString());
			result.append("\n");
		}
		return result.toString();
	}
	
	protected String contentToString(int length) {
		StringBuffer result = new StringBuffer();
		int i = 0;
		for ( Fact t : facts.values() ){
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