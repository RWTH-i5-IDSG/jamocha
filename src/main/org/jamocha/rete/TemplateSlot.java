package org.jamocha.rete;

import org.jamocha.parser.ConstantExpression;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.JamochaType;

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

    private boolean dynamicDefault;

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
	    result.setValue(defaultExpression.getValue(engine));
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

    public boolean isDynamicDefault() {
	return dynamicDefault;
    }

    public void setStaticDefaultExpression(Expression expression) {
	dynamicDefault = false;
	defaultExpression = expression;
    }

    public void setDynamicDefaultExpression(Expression expression) {
	dynamicDefault = true;
	defaultExpression = expression;
    }

    public void setDefaultDerive() {
	dynamicDefault = false;
	defaultExpression = new ConstantExpression(JamochaType.getDefaultValue(getValueType()));
    }

}
