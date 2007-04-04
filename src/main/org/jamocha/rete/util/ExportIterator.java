package org.jamocha.rete.util;

import java.util.Iterator;

import org.jamocha.rete.Deffact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.RetractException;

public class ExportIterator implements Iterator<Deffact> {

	int len,act;
	long[] facts;
	Rete engine;
	public ExportIterator(Rete engine, long[] factIds){
		len = factIds.length;
		act = 0;
		this.engine = engine;
		facts = factIds;
	}
	
	public boolean hasNext() {
		return (act<len);
	}

	public Deffact next() {
		return (Deffact)engine.getFactById(facts[act++]);
	}

	public void remove() {
		try {
			engine.retractById(facts[act-1]);
		} catch (RetractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
