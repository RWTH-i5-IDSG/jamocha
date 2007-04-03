package org.jamocha.rete.util;


import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.Deffact;

public abstract class DeffactIterator implements Iterator<Deffact>{

	public abstract boolean hasNext();
	public abstract Deffact next();
	public abstract void remove();
	
	public DeffactIterator(Map<String,String> config){
	}
	
}
