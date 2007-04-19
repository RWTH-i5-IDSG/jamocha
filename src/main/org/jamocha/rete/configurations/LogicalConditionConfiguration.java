package org.jamocha.rete.configurations;

public class LogicalConditionConfiguration extends ConditionConfiguration {

    public enum LogicalOperator {
	    AND, OR, NOT, EXISTS, FORALL, TEST, LOGICAL, NONE
    }
    
    private ConditionConfiguration[] nestedCCs = null;
    
    private LogicalOperator logicalOperator = LogicalOperator.NONE;

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public ConditionConfiguration[] getNestedCCs() {
        return nestedCCs;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public void setNestedCCs(ConditionConfiguration[] nestedCCs) {
        this.nestedCCs = nestedCCs;
    }
    
    
}
