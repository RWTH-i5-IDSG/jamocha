package org.jamocha.sampleimplementations;

import java.util.Map;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.util.DeffactIterator;
import org.jamocha.rete.wme.Deffact;
import org.jamocha.rete.wme.Deftemplate;
import org.jamocha.rete.wme.Slot;
import org.jamocha.rete.wme.TemplateSlot;

public class DeffactFibonacciIterator extends DeffactIterator {

	int i, j, max;

	public DeffactFibonacciIterator(Map<String, String> config) {
		super(config);
		i = 0;
		j = 1;
		max = Integer.parseInt(config.get("max"));
	}

	public boolean hasNext() {
		return (i < max);
	}

	public Deffact next() {
		int temp = i;
		i = j;
		j = temp + j;

		TemplateSlot[] slots = new TemplateSlot[1];
		slots[0] = new TemplateSlot("number");
		Deftemplate t = new Deftemplate("fib", null, slots);
		Slot[] values = new Slot[1];
		values[0] = new Slot("number", JamochaValue.newLong(temp));
		Deffact fact = new Deffact(t, null, values);

		return fact;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

}
