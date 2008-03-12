package org.jamocha.jsr94.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Rete;
import org.jamocha.rete.wme.Deffact;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Slot;
import org.jamocha.rete.wme.Template;
import org.jamocha.rete.wme.tags.Tag;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 * This Template2JavaClassAdaptor works on the public attributes
 * of a class. It directly accesses these attributes when reading
 * and writing java-objects and when generating templates from a class.
 */
public class PublicAttributesTemplate2JavaClassAdaptor implements Template2JavaClassAdaptor{

	protected static PublicAttributesTemplate2JavaClassAdaptor instance = null;
	
	public static PublicAttributesTemplate2JavaClassAdaptor getAdaptor() {
		if (instance == null) {
			instance = new PublicAttributesTemplate2JavaClassAdaptor();
		}
		return instance;
	}
	
	protected PublicAttributesTemplate2JavaClassAdaptor() {
	}

	public Fact getFactFromObject(Object o, Rete engine) {
		Template t = engine.getCurrentFocus().getTemplate(o.getClass().getCanonicalName());
		List<Slot> valuesList = new LinkedList<Slot>();
		for (Field f : o.getClass().getFields()) {
			try {
				Slot s = t.getSlot(f.getName()).createSlot(engine);
				s.setValue( JamochaValue.newValueAutoType(f.get(o)) );
				valuesList.add(s);
			} catch (EvaluationException e) {
				return null;
			} catch (IllegalArgumentException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}
		}
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		Fact newFact = new Deffact(t,null,values);
		return newFact;
	}

	public Object getFieldValue(String field, Object o) {
		try {
			return o.getClass().getField(field).get(o);
		} catch (Exception e) {
			return null;
		}
	}

	public List<String> getFields(Class<? extends Object> c) {
		List<String> fields = new ArrayList<String>();
		for (Field f : c.getFields()) {
			fields.add(f.getName());
		}
		return fields;
	}
	
	protected void setField(Field fi, Object o, JamochaValue value) throws IllegalArgumentException, IllegalAccessException {
		Object v = value.getObjectValue();
		if ( (v instanceof Long) && ( (fi.getType() == int.class) || (fi.getType() == Integer.class) )  ) {
			v = new Integer( ((Long)v).intValue() );
		}
		if ( (v instanceof Double) && ( (fi.getType() == float.class) || (fi.getType() == Float.class) )  ) {
			v = new Float( ((Double)v).floatValue() );
		}
		fi.set(o, v);
	}

	public void storeToObject(Fact f, Object o, Rete engine) {
		Iterator<Tag> titr = f.getTemplate().getTags(TemplateFromJavaClassTag.class);
		Class<? extends Object> c = null;
		if (titr.hasNext()) {
			TemplateFromJavaClassTag t = (TemplateFromJavaClassTag)titr.next();
			c = t.getJavaClass();
			for (Field fi : c.getFields()) {
				String name = fi.getName();
				try {
					JamochaValue value = f.getSlotValue(name);
					setField(fi, o, value);
				} catch (Exception e) {
					System.out.println("foo");
					//TODO better exception handling
				}
			}
		} 
	}



	
}
