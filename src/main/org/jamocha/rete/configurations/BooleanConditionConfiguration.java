package org.jamocha.rete.configurations;

public class BooleanConditionConfiguration extends ConditionConfiguration {

    public enum BoolOperator {
	    AND, OR, NOT, NONE
    }
    
    private ConditionConfiguration[] nestedCCs = null;
    
    private BoolOperator boolOperator = BoolOperator.NONE;

    public BoolOperator getBoolOperator() {
        return boolOperator;
    }

    public ConditionConfiguration[] getNestedCCs() {
        return nestedCCs;
    }

    public void setBoolOperator(BoolOperator boolOperator) {
        this.boolOperator = boolOperator;
    }

    public void setNestedCCs(ConditionConfiguration[] nestedCCs) {
        this.nestedCCs = nestedCCs;
    }
    
    
}
