package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;

public class TemplateSlot extends Slot {

    /**
         * 
         */
    private static final long serialVersionUID = 1L;

    /**
         * node count is used to keep track of how many nodes use the given
         * slot. This is done for statistical purposes, which serve 3 main
         * functions. 1. provide a way to calculate the relative importance of a
         * slot with regard to the entire RETE network 2. provide a way to
         * optimize runtime execution 3. provide valuable information for engine
         * management
         */
    private int nodeCount = 1;

    private boolean multiSlot = false;

    private boolean staticDefault;

    private Expression defaultExpression;

    public TemplateSlot() {
	super();
    }

    public TemplateSlot(String name) {
	super(name);
    }

    public boolean isMultiSlot() {
	return multiSlot;
    }

    public void setMultiSlot(boolean multiSlot) {
	this.multiSlot = multiSlot;
    }

    public Expression getDefaultExpression() {
	return defaultExpression;
    }

    /**
         * In some cases, a deftemplate can be define with a default value.
         * 
         * @param value
         */
    public void setDefaultExpression(Expression defaultExpression) {
	this.defaultExpression = defaultExpression;
    }

    public Slot createSlot(Rete engine) throws EvaluationException {
	Slot result = new Slot(getName());
	result.setValueType(getValueType());
	if (defaultExpression != null) {
	    JamochaValue value = defaultExpression.getValue(engine);
	    if (value.getType().equals(JamochaType.LIST) && !isMultiSlot()) {
		if (value.getListCount() > 0) {
		    value = value.getListValue(0);
		} else {
		    value = JamochaValue.NIL;
		}
	    }
	    result.setValue(value);
	}
	return result;
    }

    /**
         * return the number of nodes the given slot participates in. It may not
         * be a complete count. In some cases, it may only count the direct
         * successors of ObjectTypeNode
         * 
         * @return
         */
    public int getNodeCount() {
	return this.nodeCount;
    }

    /**
         * Increment the node count
         */
    public void incrementNodeCount() {
	this.nodeCount++;
    }

    /**
         * decrement the node count
         * 
         */
    public void decrementNodeCount() {
	--this.nodeCount;
    }

    public boolean isStaticDefault() {
	return staticDefault;
    }

    public void setStaticDefaultExpression(Expression expression) {
	staticDefault = true;
	defaultExpression = expression;
    }

    public void setDynamicDefaultExpression(Expression expression) {
	staticDefault = false;
	defaultExpression = expression;
    }

    public void setDefaultDerive() {
	staticDefault = true;
	defaultExpression = JamochaType.getDefaultValue(getValueType());
    }

}
