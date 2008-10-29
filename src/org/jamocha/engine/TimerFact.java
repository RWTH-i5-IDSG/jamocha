package org.jamocha.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.configurations.SlotConfiguration;
import org.jamocha.engine.modules.Module;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.nodes.FactTupleImpl;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Slot;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.formatter.Formatter;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.parser.ParserFactory;

public class TimerFact implements Fact {

	public class TimerTemplate implements Template {

		private Collection<Tag> tags;
		
		private TemplateSlot time;
		
		private TemplateSlot[] slots;
		
		public TimerTemplate() {
			tags = new LinkedList<Tag>();
			time = new TemplateSlot("time");
			slots = new TemplateSlot[1];
			slots[0]=time;
		}
		
		public void addTag(Tag t) {
			tags.add(t);
		}

		public TemplateSlot[] getAllSlots() {
			return slots;
		}

		public String getClassName() {
			return "point-in-time";
		}

		public String getName() {
			return "point-in-time";
		}

		public int getNumberOfSlots() {
			return 1;
		}

		public Template getParent() {
			return null;
		}

		public TemplateSlot getSlot(String name) {
			return (name.equals("time")) ? time : null;
		}

		public TemplateSlot getSlot(int column) {
			return (column==0) ? time : null;
		}

		public Iterator<Tag> getTags() {
			return tags.iterator();
		}

		public Iterator<Tag> getTags(Class<? extends Tag> tagClass) {
			Collection<Tag> spec= new LinkedList<Tag>();
			for (Tag t:tags){
				if (tagClass.isInstance(t)) spec.add(t);
			}
			return spec.iterator();
		}

		public boolean getWatch() {
			return false;
		}

		public boolean inUse() {
			return true;
		}

		public void setParent(Template parent) {
		}

		public void setWatch(boolean watch) {
		}

		public String toPPString() {
			return "";
		}

		public String getDump() {
			return null;
		}

		public String format(final Formatter visitor) {
			return visitor.visit(this);
		}

		public void evaluateStaticDefaults(Engine engine) {
		}

		public Module checkUserDefinedModuleName(Engine engine) {
			return null;
		}
		
	}
	
	private class TimerFactThread extends Thread {
		
		public void run() {	
			while (true) {
				try {
					engine.getNet().retractFact(TimerFact.this);
					engine.getNet().assertFact(TimerFact.this);
					this.sleep(1000);
				} catch (Exception e) {
					Logging.logger(this.getClass()).fatal(e);
				}
			}
		}
		
	}
	
	private long factId;
	
	private GregorianCalendar gcal;
	
	private TimerTemplate template;
	
	private TimerFactThread thread;
	
	private Engine engine;
	
	public TimerFact(Engine e) throws EvaluationException {
		template = new TimerTemplate();
		engine = e;
		gcal = new GregorianCalendar();
		e.addTemplate(template);
		e.assertFact(this);
	}
	
	public void addTag(Tag t) {
	}

	public void clear() {
	}
	
	public String toString() {
		return format(ParserFactory.getFormatter(true));
	}

	public EqualityIndex equalityIndex() {
		return null;
	}

	public long getCreationTimeStamp() {
		return 0;
	}

	public long getFactId() {
		return factId;
	}

	public int getSlotId(String name) {
		if (name.equals("time")) return 0;
		return -1;
	}

	public JamochaValue getSlotValue(int id) throws EvaluationException {
		gcal.setTime(new Date());
		return JamochaValue.newLong(gcal.getTimeInMillis());
	}

	public JamochaValue getSlotValue(String name) throws EvaluationException {
		return getSlotValue(0);
	}

	public Iterator<Tag> getTags() {
		return Collections.EMPTY_LIST.iterator();
	}

	public Iterator<Tag> getTags(Class<Tag> tagClass) {
		return Collections.EMPTY_LIST.iterator();
	}

	public Template getTemplate() {
		return template;
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
		factId=id;
	}

	public void setTemporalValidity(TemporalValidity val) {
	}

	public void updateSlots(Engine engine, Slot[] slots) {
	}

	public void updateSlots(Engine engine, SlotConfiguration[] slots) throws EvaluationException {
	}

	public FactTuple getFactTuple() {
		final FactTuple tuple = new FactTupleImpl(this);
		return tuple;
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


	public String format(final Formatter visitor) {
		return visitor.visit(this);
	}

	public String getDump() {
		return "";
	}

	public void start() {
		thread = new TimerFactThread();
		thread.start();
	}

	
	
}
