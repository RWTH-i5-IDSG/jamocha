package org.jamocha.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class BindingHelper {

	
	public class LittleRecord{
		Condition condition;
		int condNr;
		int slotNr;
		
		public String toString(){
			return "["+condition.toString()+","+condNr+","+slotNr+"]";
		}
	}
	
	private class Record{
		Vector<LittleRecord> conditions = null;
		Constraint constraint;
		
		public Record(){
			conditions = new Vector<LittleRecord>();
		}
	}
	
	HashMap<String, Record> storage;
	
	public BindingHelper() {
		storage = new HashMap<String, Record>();
	}
	
	
	public void register(BoundConstraint constraint, Condition c, int conditionNr, int slotNr){
		Record r = storage.get(constraint.getVariableName());
		if (r == null) {
			r = new Record();
			r.constraint = constraint;
			storage.put(constraint.getVariableName(), r);
		}
		
		LittleRecord lr = new LittleRecord();
		lr.condition = c;
		lr.condNr = conditionNr;
		lr.slotNr = slotNr;
		
		r.conditions.add(lr);
	}
	
	public Set<String> getAllNames() {
		return storage.keySet();
	}
	
	public Constraint getConstraint(String name){
		return storage.get(name).constraint;
	}
	
	public Iterator<LittleRecord> lookup(BoundConstraint constraint){
		Record r = storage.get(constraint.name);
		if (r == null) return null;
		return r.conditions.iterator();
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (String key :  storage.keySet()){
			result.append(key);
			result.append(": ");
			for (LittleRecord c : storage.get(key).conditions){
				result.append(c);
			}
			
			
		}
		return result.toString();
		
		
	}
	
	
}
