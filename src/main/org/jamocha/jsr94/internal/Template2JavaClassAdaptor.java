package org.jamocha.jsr94.internal;

import java.util.List;

import org.jamocha.rete.Rete;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Template;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 * Classes implementing this interface help translating
 * between Jamocha-facts <-> Java-objects and
 * Jamocha-templates <-> Java-classes 
 */
public interface Template2JavaClassAdaptor {

	public List<String> getFields(Class<? extends Object> c) throws Template2JavaClassAdaptorException;
	
	public Object getFieldValue(String field, Object o) throws Template2JavaClassAdaptorException;
	
	public void storeToObject(Fact f, Object o, Rete engine) throws Template2JavaClassAdaptorException;
	
	public Fact getFactFromObject(Object o, Rete engine) throws Template2JavaClassAdaptorException;
	
}
