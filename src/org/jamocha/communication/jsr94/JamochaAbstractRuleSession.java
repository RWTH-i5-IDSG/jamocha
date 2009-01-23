package org.jamocha.communication.jsr94;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptor;
import org.jamocha.communication.jsr94.internal.Template2JavaClassAdaptorException;
import org.jamocha.communication.jsr94.internal.TemplateFromJavaClassTag;
import org.jamocha.engine.Engine;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.engine.workingmemory.elements.tags.Tag;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.Expression;
import org.jamocha.parser.RuleException;

public abstract class JamochaAbstractRuleSession {

	private final Map<Class, Template2JavaClassAdaptor> javaClassAdaptor;
	
	protected Template2JavaClassAdaptor getJavaClassAdaptor(Class c) {
		Template2JavaClassAdaptor res = javaClassAdaptor.get(c);
		if (res == null) {
			String templName = c.getCanonicalName();
			Template t = getEngine().findTemplate(templName);
			if (t == null)
				return null;
			Iterator<Tag> tags = t.getTags(TemplateFromJavaClassTag.class);
			if (tags.hasNext()) {
				TemplateFromJavaClassTag tfjct = (TemplateFromJavaClassTag) tags
						.next();
				Template2JavaClassAdaptor adaptor = tfjct.getAdaptor();
				javaClassAdaptor.put(c, adaptor);
				return adaptor;
			}
			return null;
		}
		return res;
	}
	
	public JamochaAbstractRuleSession() {
		javaClassAdaptor = new HashMap<Class, Template2JavaClassAdaptor>();
	}
	

	protected Object fact2Object(Fact f) {
		Iterator<Tag> itr = f.getFirstFact().getTemplate().getTags(TemplateFromJavaClassTag.class);
		if (itr.hasNext()) {
			TemplateFromJavaClassTag ttag = (TemplateFromJavaClassTag) itr.next();
			Class cl = ttag.getJavaClass();
			Object o = null;
			try {
				o = cl.newInstance();
			} catch (Exception e) {
				// TODO exception handling
			}
			try {
				ttag.getAdaptor().storeToObject(f.getFirstFact(), o, getEngine());
				return o;
			} catch (Template2JavaClassAdaptorException e) {
				return f;
			}
		}
		return null;
	}

	protected void addRules(JamochaRuleExecutionSet res2)
			throws EvaluationException, RuleException {
		Expression[] exprs = res2.getExpressions();
		for (Expression e : exprs)
			e.getValue(getEngine());
	}

	
	protected abstract Engine getEngine();
	
}
