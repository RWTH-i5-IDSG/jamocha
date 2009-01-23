package org.jamocha.communication.jsr94.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.engine.Engine;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

public class FullEncapsulationJavaClassAdaptor implements JavaClassAdaptor {

	protected static FullEncapsulationJavaClassAdaptor instance = null;
	
	protected List<String> fields;

	public static FullEncapsulationJavaClassAdaptor getAdaptor() {
		if (instance == null)
			instance = new FullEncapsulationJavaClassAdaptor();
		return instance;
	}

	protected FullEncapsulationJavaClassAdaptor() {
		fields = new ArrayList<String>();
		fields.add("object");
	}
	
	public Fact getFactFromObject(Object o, Engine engine) throws Template2JavaClassAdaptorException {
		Template t = engine.getCurrentFocus().getTemplate(
				o.getClass().getCanonicalName());
		List<Slot> valuesList = new LinkedList<Slot>();
		try {
			Slot s = t.getSlot("object").createSlot(engine);
			s.setValue(JamochaValue.newObject(o));
			valuesList.add(s);
		} catch (EvaluationException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		Fact newFact = new Deffact(t, values);
		return newFact;
	}

	public Object getFieldValue(String field, Object o)	throws Template2JavaClassAdaptorException {
		if (field.equals("object")) {
			return o;
		} else return null;
	}

	public List<String> getFields(Class<? extends Object> c) throws Template2JavaClassAdaptorException {
		return fields;
	}

	public Object storeToObject(Fact f, Object o, Engine engine) throws Template2JavaClassAdaptorException {
		try {
			return f.getSlotValue("object").getObjectValue();
		} catch (EvaluationException e) {
			throw new Template2JavaClassAdaptorException(e);
		}
		
	}

}
