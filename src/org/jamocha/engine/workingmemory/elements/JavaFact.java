package org.jamocha.engine.workingmemory.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.EqualityIndex;
import org.jamocha.engine.TemporalValidity;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.FactTupleImpl;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.engine.workingmemory.elements.tags.TagIterator;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

public class JavaFact implements Fact {

	private Object o;
	
	private EqualityIndex eqIndex;
	
	private List<Tag> tags;
	
	private long timestamp;
	
	private long id;
	
	private Engine e;
	
	private Template t;
	
	public static String upperCaseFirstLetter(final String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	public JavaFact(Object o, Engine e) {
		this.o = o;
		this.e = e;
		tags = new ArrayList<Tag>();
		timestamp = System.nanoTime();
		id=-1;
		t=null;
		getTemplate();
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}

	public void clear() {
		o = null;
		tags.clear();
		tags = null;
	}

	public EqualityIndex equalityIndex() {
		if (eqIndex == null) eqIndex = new EqualityIndex(this);
		return eqIndex;
	}

	public long getCreationTimeStamp() {
		return timestamp;
	}

	public long getFactId() {
		return id;
	}

	public int getSlotId(String name) {
		int i=0;
		for(TemplateSlot ts : t.getAllSlots()) {
			if (ts.getName().equals(name)) return i;
			i++;
		}
		return -1;
	}

	public JamochaValue getSlotValue(int id) throws EvaluationException {
		Template tmpl = getTemplate();
		String name = tmpl.getSlot(id).getName();
		return getSlotValue(name);
	}

	public JamochaValue getSlotValue(String name) throws EvaluationException {
		Object res = null;
		try {
			res = getSlotValue2(name, "get");
		} catch (NoSuchMethodException e) {
			try {
				res = getSlotValue2(name, "is");
			} catch (NoSuchMethodException e1) {
				Logging.logger(this.getClass()).warn(e1);
			}
		}
		return JamochaValue.newValueAutoType(res);
	}
	
	protected Object getSlotValue2(String name, String prefix) throws EvaluationException, NoSuchMethodException {
		final String fname = upperCaseFirstLetter(name);
		try {
			final Method m = o.getClass().getMethod(prefix + fname);
			return m.invoke(o);
		} catch (IllegalArgumentException e) {
			Logging.logger(this.getClass()).warn(e);
		} catch (IllegalAccessException e) {
			Logging.logger(this.getClass()).warn(e);
		} catch (InvocationTargetException e) {
			Logging.logger(this.getClass()).warn(e);
		}
		return null;
	}

	public Iterator<Tag> getTags() {
		return tags.iterator();
	}

	public Iterator<Tag> getTags(Class<Tag> tagClass) {
		return new TagIterator(tagClass, tags);
	}

	public Template getTemplate() {
		if (t != null) return t; // caching
		t = e.findTemplate(o.getClass().getCanonicalName());
		if (t == null) {
			t = new JavaTemplate(o.getClass());
			try {
				e.addTemplate(t);
			} catch (EvaluationException e1) {
				Logging.logger(this.getClass()).warn(e1);
				Logging.logger(this.getClass()).warn("error while adding new template for '"+o.getClass().getCanonicalName()+"'");
			}
		}
		return t;
	}

	public TemporalValidity getTemporalValidity() {
		return null;
	}

	public boolean isSlotSilent(int idx) {
		return false;
	}

	public boolean isSlotSilent(String slotName) {
		return false;
	}

	public void setFactId(long id) {
		this.id = id;
	}

	public void setTemporalValidity(TemporalValidity val) {
	}

	protected void setSlotValue(String slotName, Object value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getter = o.getClass().getMethod("get"+upperCaseFirstLetter(slotName));
		Class<? extends Object> returnType = getter.getReturnType();
		Method setter = o.getClass().getMethod("set"+upperCaseFirstLetter(slotName), returnType);
		Logging.logger(this.getClass()).debug("Trying to set a "+value.getClass().getCanonicalName()+" with a setter, which receives "+returnType.getCanonicalName());
		if ( returnType.isAssignableFrom(int.class) || returnType.isAssignableFrom(Integer.class) ) {
			Logging.logger(this.getClass()).warn("Internally Jamocha uses LONG for integer numbers. Now we have to put a LONG value into a INT attribute! It is recommended to use LONG for the attribute '"+slotName+"'");
			value = (int)  ((long)  ((Long)value));
		}
		if ( returnType.isAssignableFrom(float.class) || returnType.isAssignableFrom(Float.class) ) {
			Logging.logger(this.getClass()).warn("Internally Jamocha uses DOUBLE for floating point numbers. Now we have to put a DOUBLE value into a FLOAT attribute! It is recommended to use DOUBLE for the attribute '"+slotName+"'");
			value = (float)  ((double)  ((Double)value));
		}
		setter.invoke(o, value);
	}
	
	public void updateSlots(Engine engine, SlotConfiguration[] slots) throws EvaluationException {
		for (SlotConfiguration slot : slots) {
			try {
				setSlotValue(slot.getSlotName(), slot.getSlotValues()[0].getValue(engine).getObjectValue()  );
			} catch (Exception e) {
				throw new EvaluationException(e);
			}
		}
	}

	public FactTuple getFactTuple() {
		return new FactTupleImpl(this);
	}

	public Fact getFirstFact() {
		return this;
	}

	public Fact getLastFact() {
		return this;
	}

	public boolean isStandaloneFact() {
		return true;
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

	public String getDump() {
		return "";
	}

	public Object getObject() {
		return o;
	}
	
	public String toString() {
		return format(ParserFactory.getFormatter(true));
	}

}
