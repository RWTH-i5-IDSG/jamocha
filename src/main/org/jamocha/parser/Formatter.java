package org.jamocha.parser;

import org.jamocha.rete.Function;
import org.jamocha.rule.Rule;

public interface Formatter {
    
    public String formatExpression(Expression expression);
    
    public String formatFunction(Function function);
    
    public String formatRule(Rule rule);

}
