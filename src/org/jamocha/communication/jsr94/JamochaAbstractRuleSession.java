package org.jamocha.communication.jsr94;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.communication.jsr94.internal.FullEncapsulationJavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.communication.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.engine.Engine;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;

public abstract class JamochaAbstractRuleSession {

	protected void addRules(JamochaRuleExecutionSet res2)
			throws EvaluationException, RuleException {
		Expression[] exprs = res2.getExpressions();
		for (Expression e : exprs)
			e.getValue(getEngine());
	}

	
	protected abstract Engine getEngine();
	
}
