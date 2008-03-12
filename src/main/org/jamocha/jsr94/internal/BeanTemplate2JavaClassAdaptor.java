package org.jamocha.jsr94.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * This Template2JavaClassAdaptor works on Java-Beans, but its
 * somewhat limited:
 * It will only "see" attributes, which have a getter (without parameters)
 * and a setter (with exactly one parameter with the right type). This
 * is important, since we must be able to read and write to each attribute.
 * Maybe, you've so-called "immutable" classes, with only getters and a
 * constructor for setting the attributes. This is to nearly impossible to
 * automatically evaluate via the reflections api. So, in that case, you've
 * to provide setters e.g. via an adaptor-class...
 */
public class BeanTemplate2JavaClassAdaptor implements Template2JavaClassAdaptor{

	protected static BeanTemplate2JavaClassAdaptor instance = null;
	
	public static BeanTemplate2JavaClassAdaptor getAdaptor() {
		if (instance == null) {
			instance = new BeanTemplate2JavaClassAdaptor();
		}
		return instance;
	}
	
	protected BeanTemplate2JavaClassAdaptor() {
	}

	//TODO document interface!
	//TODO implement cloning from facts and templates with tags
	
	public Fact getFactFromObject(Object o, Rete engine) throws Template2JavaClassAdaptorException {
		//TODO one can implement that much more efficient
		Template t = engine.getCurrentFocus().getTemplate(o.getClass().getCanonicalName());
		List<Slot> valuesList = new LinkedList<Slot>();
		for (String attr : getFields(o.getClass())) {
			try {
				Slot s = t.getSlot(attr).createSlot(engine);
				s.setValue( JamochaValue.newValueAutoType(getFieldValue(attr, o)) );
				valuesList.add(s);
			} catch (Exception e) {
				throw new Template2JavaClassAdaptorException("error while generating a fact from an object",e);
			}
		}
		Slot[] values = new Slot[valuesList.size()];
		values = valuesList.toArray(values);
		Fact newFact = new Deffact(t,null,values);
		return newFact;
	}

	protected String upperCaseFirstLetter(String s) {
		if (s.length()==0) return s;
		return s.substring(0,1).toUpperCase() + s.substring(1);
	}

	protected String lowerCaseFirstLetter(String s) {
		if (s.length()==0) return s;
		return s.substring(0,1).toLowerCase() + s.substring(1);
	}
	
	protected Object getFieldValue(String field, Object o, String prefix) throws Template2JavaClassAdaptorException {
		String fname = upperCaseFirstLetter (field);
		try {
			Method m = o.getClass().getMethod(prefix+fname);
			return m.invoke(o);
		} catch (Exception e) {
			throw new Template2JavaClassAdaptorException("error while getting the value from field '"+field+"' in class "+o.getClass().getCanonicalName(),e);
		}
	}
	
	public Object getFieldValue(String field, Object o) throws Template2JavaClassAdaptorException {
		try {
			return getFieldValue(field, o, "get");
		} catch (Template2JavaClassAdaptorException e) {
			return getFieldValue(field, o, "is");
		}
	}


	public List<String> getFields(Class<? extends Object> c) {
		List<String> fields = new ArrayList<String>();
		for (Method getter : c.getMethods()) {
			if (getter.getName().startsWith("get") || getter.getName().startsWith("is")) {
				// collect some information
				String potetialAttrName;
				if (getter.getName().startsWith("get")) {
					potetialAttrName = getter.getName().substring(3);
				} else {
					potetialAttrName = getter.getName().substring(2);
				}
				if (getter.getParameterTypes().length >0) continue;
				Class attrType = getter.getReturnType();
				// search for a setter
				Method setter;
				try {
					setter = c.getMethod("set"+potetialAttrName, attrType);
				} catch (SecurityException e) {
					// TODO better exception handling
					continue;
				} catch (NoSuchMethodException e) {
					// TODO better exception handling
					continue;
				}
				assert (setter != null);
				fields.add(lowerCaseFirstLetter(potetialAttrName));
			}
		}
		return fields;
	}
	
	protected Method getSetter(Class<? extends Object> c, String attr) throws Template2JavaClassAdaptorException {
		//TODO we should cache the results
		for (Method o : c.getMethods()){
			String mname = o.getName();
			if (!mname.startsWith("set")) continue;
			if (!mname.substring(3).equals(upperCaseFirstLetter(attr))) continue;
			if (o.getParameterTypes().length != 1) continue;
			return o;
		}
		throw new Template2JavaClassAdaptorException("no setter found for attribute '"+attr+"' in class '"+c.getCanonicalName()+"'");
	}
	
	protected void setField(String attr, Object o, JamochaValue value) throws IllegalArgumentException, IllegalAccessException, Template2JavaClassAdaptorException, InvocationTargetException {
		Object v = value.getObjectValue();
		Method setter = getSetter(o.getClass(),attr);
		Class<? extends Object> fieldType = setter.getParameterTypes()[0];
		if ( (v instanceof Long) && ( (fieldType == int.class) || (fieldType == Integer.class) )  ) {
			v = new Integer( ((Long)v).intValue() );
		}
		if ( (v instanceof Double) && ( (fieldType == float.class) || (fieldType == Float.class) )  ) {
			v = new Float( ((Double)v).floatValue() );
		}
		setter.invoke(o, v);
	}

	public void storeToObject(Fact f, Object o, Rete engine) throws Template2JavaClassAdaptorException {
		Iterator<Tag> titr = f.getTemplate().getTags(TemplateFromJavaClassTag.class);
		Class<? extends Object> c = null;
		if (titr.hasNext()) {
			TemplateFromJavaClassTag t = (TemplateFromJavaClassTag)titr.next();
			c = t.getJavaClass();
			for (String attr : getFields(o.getClass())) {
				try {
					JamochaValue value = f.getSlotValue(attr);
					setField(attr, o, value);
				} catch (Exception e) {
					throw new Template2JavaClassAdaptorException("error while storing fact to object",e);
				}
			}
		} 
	}



	
}
