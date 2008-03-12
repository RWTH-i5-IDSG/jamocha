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
package org.jamocha.rule.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.configurations.SlotConfiguration;
import org.jamocha.rete.wme.Defclass;
import org.jamocha.rete.wme.Deftemplate;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Slot;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

/**
 * @author Peter Lin
 * 
 * The class will generate the trigger facts for a single rule. The purpose of this is to make
 * it easier to test a rule. Since a rule knows what conditions it needs, it makes sense to
 * generate the trigger facts instead of doing it manually.
 */
public class GenerateFacts {
	public GenerateFacts() {
		super();
	}
	
	public static ArrayList<Object> generateFacts(Rule rule, Rete engine) {
		ArrayList<Object> facts = new ArrayList<Object>();
		if (rule != null) {
			Condition[] conditions = rule.getConditions();
			for (int idx=0; idx < conditions.length; idx++) {
				Condition c = conditions[idx];
				if (c instanceof ObjectCondition) {
					ObjectCondition oc = (ObjectCondition)c;
					Deftemplate tpl = (Deftemplate)engine.findTemplate(oc.getTemplateName());
					if (tpl.getClassName() != null) {
						Object data = generateJavaFacts(oc,tpl,engine);
						facts.add(data);
					} else {
						Object data = generateDeffact(oc,tpl,engine);
						facts.add(data);
					}
				} else if (c instanceof TestCondition) {
					
				}
			}
		}
		return facts;
	}
	
	/**
	 * The method uses Defclass, Class, Deftemplate and Rete to create a new
	 * instance of the java object. Once the instance is created, the method
	 * uses Defclass to look up the write method and calls it with the
	 * appropriate value.
	 * @param cond
	 * @param templ
	 * @param engine
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object generateJavaFacts(ObjectCondition cond, Deftemplate templ, Rete engine) {
		try {
			Class theclz = Class.forName(templ.getClassName());
			Defclass dfc = engine.findDefclass(theclz);
			Object data = theclz.newInstance();
			for (Constraint cn : cond.getConstraints()) {
				if (cn instanceof LiteralConstraint) {
					java.lang.reflect.Method meth = dfc.getWriteMethod(cn.getName());
					meth.invoke(data, new Object[]{cn.getValue()});
				}
			}
			// for now the method doesn't inspect the bindings
			// later on it needs to be added
			
			return data;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param cond
	 * @param templ
	 * @param engine
	 * @return
	 */
	public static Object generateDeffact(ObjectCondition cond, Deftemplate templ, Rete engine) {
		SlotConfiguration[] list = new SlotConfiguration[cond.getConstraints().size()];
		Constraint cn;
		for (int i = 0; i< cond.getConstraints().size() ; i++){
			cn = cond.getConstraints().get(i);
			if (cn instanceof LiteralConstraint) {
				list[i] = new SlotConfiguration(cn.getName(),i, new Parameter[]{cn.getValue()} );
			} else if (cn instanceof PredicateConstraint) {
				
			} else if (cn instanceof BoundConstraint) {
				// for now we do the simple thing and just set
				// any bound slots to 1
				list[i] = new SlotConfiguration(cn.getName(),i, new Parameter[]{JamochaValue.newLong(Integer.valueOf(1))} );
			}
		}
		Fact f = null;
		try {
		    f = templ.createFact(list, engine);
		} catch (EvaluationException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		return f;
	}
    
    public static String parseModuleName(Rule rule, Rete engine) {
        if (rule.getName().indexOf("::") > 0) {
            String text = rule.getName();
            String[] sp = text.split("::");
            return sp[0].toUpperCase();
        }
        return null;
    }
}
