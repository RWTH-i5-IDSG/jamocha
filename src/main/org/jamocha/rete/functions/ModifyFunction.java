/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.IllegalTypeException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * ModifyFunction is equivalent to CLIPS modify function.
 */
public class ModifyFunction implements Function, Serializable {

    /**
         * 
         */
    private static final long serialVersionUID = 1L;

    public static final String MODIFY = "modify";

    protected Fact[] triggerFacts = null;

    /**
         * 
         */
    public ModifyFunction() {
	super();
    }

    public void setTriggerFacts(Fact[] facts) {
	this.triggerFacts = facts;
    }

    public JamochaType getReturnType() {
	return JamochaType.BOOLEAN;
    }

    public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException {
	JamochaValue result = JamochaValue.FALSE;
	if (engine != null && params != null && params.length >= 2 && params[0].isObjectBinding()) {
	    BoundParam bp = (BoundParam) params[0];
	    Fact fact = bp.getFact();
	    try {
		// first retract the fact
		engine.retractFact(fact);
		Slot[] newSlots = new Slot[params.length - 1];
		for (int i = 0; i < newSlots.length; i++) {
		    JamochaValue jamValue = params[i + 1].getValue(engine);
		    if (jamValue.is(JamochaType.SLOT)) {
			newSlots[i] = jamValue.getSlotValue();
		    } else {
			throw new IllegalTypeException(JamochaType.SLOTS, jamValue.getType());
		    }
		}
		fact.updateSlots(engine, newSlots);

		// if (fact.hasBinding()) {
		// fact.resolveValues(engine, this.triggerFacts);
		// fact = fact.cloneFact(engine);
		// }
		
		// now assert the fact using the same fact-id
		engine.assertFact(fact);
		result = JamochaValue.TRUE;
	    } catch (RetractException e) {
		engine.writeMessage(e.getMessage());
	    } catch (AssertException e) {
		engine.writeMessage(e.getMessage());
	    }
	}

	return result;
    }

    /*
         * (non-Javadoc)
         * 
         * @see woolfel.engine.rete.Function#getName()
         */
    public String getName() {
	return MODIFY;
    }

    public String toPPString(Parameter[] params, int indents) {
	if (params != null && params.length > 0) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("(modify ");
	    buf.append(params[0].getExpressionString()).append(" ");
	    for (int idx = 1; idx < params.length; idx++) {
		// the parameter should be a deffact
		buf.append("(").append(params[idx].getExpressionString()).append(")");
	    }
	    buf.append(" )");
	    return buf.toString();
	} else {
	    return "(modify [binding] [deffact])\n" + "Function description:\n"
		    + "\tAllows the user to modify template facts on the fact-list.";
	}
    }
}
