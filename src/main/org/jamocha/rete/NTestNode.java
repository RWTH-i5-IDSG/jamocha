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
package org.jamocha.rete;

import java.util.Map;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.nodes.BaseJoin;


/**
 * @author Peter Lin
 *
 * TestNode extends BaseJoin. TestNode is used to evaluate functions.
 * It may use values or bindings as parameters for the functions. The
 * left input is where the facts would enter. The right input is a
 * dummy input, since no facts actually enter.
 */
public class NTestNode extends BaseJoin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * TestNode can only have 1 top level function
	 */
	protected Function func = null;

	/**
	 * the parameters to pass to the function
	 */
	protected Parameter[] params = null;

	/**
	 * by default the string is null, until the first time
	 * toPPString is called.
	 */
	private String ppstring = null;
	
	/**
	 * @param id
	 */
	public NTestNode(int id, Function func, Parameter[] parameters) {
		super(id);
		this.func = func;
		this.params = parameters;
	}

	/**
	 * Assert will first pass the facts to the parameters. Once the
	 * parameters are set, it should call execute to get the result.
	 */
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		if (!leftmem.containsKey(linx)) {
            this.setParameters(linx.getFacts());
            JamochaValue value;
            try {
                value = this.func.executeFunction(engine, this.params);
                if (value.implicitCast(JamochaType.BOOLEAN).getBooleanValue()) {
                    BetaMemory bmem = new BetaMemoryImpl(linx);
                    leftmem.put(bmem.getIndex(), bmem);
                }
            } catch (EvaluationException e) {
                throw new AssertException(e);
            }
			// only propogate if left memories count is zero
			if (leftmem.size() == 0) {
				propogateAssert(linx, engine, mem);
			}
		}
	}

	/**
	 * Since the assertRight is a dummy, it doesn't do anything.
	 */
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem) {
	}

	/**
	 * 
	 */
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
			throws RetractException {
		Map leftmem = (Map) mem.getBetaLeftMemory(this);
		int prev = leftmem.size();
		if (leftmem.containsKey(linx)) {
			// the memory contains the key, so we retract and propogate
			leftmem.remove(linx);
		}
		if (prev != 0 && leftmem.size() == 0) {
			propogateRetract(linx, engine, mem);
		}
	}

	/**
	 * for TestNode, setbindings does not apply
	 */
	public void setBindings(Binding[] binds) {
	}

	/**
	 * retract right is a dummy, so it does nothing.
	 */
	public void retractRight(Fact rfact, Rete engine, WorkingMemory mem) {
	}

	/**
	 * clear the memory
	 */
	public void clear(WorkingMemory mem) {
		((Map) mem.getBetaLeftMemory(this)).clear();
	}

	protected void setParameters(Fact[] facts) {
		for (int idx = 0; idx < this.params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				((BoundParam) params[idx]).setFact(facts);
			}
		}
	}

	/**
	 * Still need to implement the method to return string
	 * format of the node
	 */
	public String toString() {
		return "(test (" + this.func.getName() + ") )";
	}

	/**
	 * 
	 */
	public String toPPString() {
        if (ppstring == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("NTestNode-" + this.nodeID + "> (not ("
                    + this.func.getName());
            for (int idx = 0; idx < this.params.length; idx++) {
		buf.append(" " + params[idx].getExpressionString());

            }
            buf.append(") )");
            ppstring = buf.toString();
        }
        return ppstring;
	}

}
