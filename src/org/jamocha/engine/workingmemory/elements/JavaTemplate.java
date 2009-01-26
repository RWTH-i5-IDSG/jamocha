package org.jamocha.engine.workingmemory.elements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.engine.workingmemory.elements.tags.TagIterator;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;

public class JavaTemplate implements Template {

	private String name;
	
	private TemplateSlot[] slots;
	
	private Map<String,TemplateSlot> name2ts;
	
	private List<Tag> tags;
	
	private Class<? extends Object> javaClass;
	
	public Class<? extends Object> getJavaClass() {
		return javaClass;
	}

	protected static String lowerCaseFirstLetter(final String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
  
  	public static List<String> getFieldsFromBean(final Class<? extends Object> c) {
		final List<String> fields = new ArrayList<String>();
		for (final Method getter : c.getMethods())
			if (getter.getName().startsWith("get")
					|| getter.getName().startsWith("is")) {
				// collect some information
				String potetialAttrName;
				if (getter.getName().startsWith("get"))
					potetialAttrName = getter.getName().substring(3);
				else
					potetialAttrName = getter.getName().substring(2);
				if (getter.getParameterTypes().length > 0)
					continue;
				final Class<? extends Object> attrType = getter.getReturnType();
				// search for a setter
				Method setter;
				try {
					setter = c.getMethod("set" + potetialAttrName, attrType);
				} catch (final SecurityException e) {
					// TODO better exception handling
					continue;
				} catch (final NoSuchMethodException e) {
					// TODO better exception handling
					continue;
				}
				assert setter != null;
				
				fields.add(lowerCaseFirstLetter(potetialAttrName));
			}
		return fields;
	}
	
	public static TemplateSlot[] getTemplateSlotsFromClass(Class<? extends Object> c) {
		final List<TemplateSlot> slots = new ArrayList<TemplateSlot>();
		int id = 0;
		for (final String slotName : getFieldsFromBean(c)) {
			// TODO handle data type
			final TemplateSlot tslot = new TemplateSlot(slotName);
			tslot.setId(id++);
			Logging.logger(JavaTemplate.class).debug("in class '" + c.getCanonicalName()
					+ "' found attribute '" + slotName + "'");
			slots.add(tslot);
		}
		TemplateSlot[] slotsArray = new TemplateSlot[slots.size()];
		return slots.toArray(slotsArray);
	}
	
	public JavaTemplate(Class<? extends Object> c) {
		slots = getTemplateSlotsFromClass(c);
		name = c.getCanonicalName();
		javaClass = c;
		tags = new ArrayList<Tag>();
		name2ts = new HashMap<String,TemplateSlot>();
		for (TemplateSlot ts : slots) name2ts.put(ts.getName(),ts);
	}
	
	public void addTag(Tag t) {
		tags.add(t);
	}

	public Module checkUserDefinedModuleName(Engine engine) {
		// no user defined module => put it into the current focus
		return null;
	}

	public void evaluateStaticDefaults(Engine engine) throws EvaluationException {
	}

	public TemplateSlot[] getAllSlots() {
		return slots;
	}

	public String getName() {
		return name;
	}

	public int getNumberOfSlots() {
		return slots.length;
	}


	public TemplateSlot getSlot(String name) {
		return name2ts.get(name);
	}

	public TemplateSlot getSlot(int column) {
		return slots[column];
	}

	public Iterator<Tag> getTags() {
		return tags.iterator();
	}

	public Iterator<Tag> getTags(Class<? extends Tag> tagClass) {
		return new TagIterator(tagClass, tags);
	}

	public boolean inUse() {
		return true;
	}

	public String toPPString() {
		return "[Template from java class '"+name+"']";
	}

	public String getDump() {
		//TODO think about dumping in general and about that
		return "";
	}

	public String format(Formatter visitor) {
		return visitor.visit(this);
	}

}
