/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.engine.rules.rulecompiler.beffy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.communication.events.CompilerListener;
import org.jamocha.communication.logging.Logging;
import org.jamocha.communication.logging.Logging.JamochaLogger;
import org.jamocha.engine.AssertException;
import org.jamocha.engine.Binding;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.AndCondition;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.Rule;
import org.jamocha.rules.TestCondition;


/**
 * @author Josef Alexander Hahn
 * We have the old SlimFast-RuleCompiler, which is buggy and not that
 * feature-rich. Furthermore, it sometimes compiles wrong rete-nets :(
 * 
 * So, we urgently need a successor... Due to the fact that there are
 * unsolved technical questions for writing a perfect rule compiler, the
 * plan is to write one, which handles its task as good as my knowledge
 * allows it. This will hopefully lead to this BeffyRuleCompiler (beffy
 * as nice abbreviation for "Best EFFort").
 * 
 * As you can see at many places in the code: This rule compiler is 
 * _NOT_ optimized for memory-usage, for the speed of rule-compiling
 * or the size size of the resulting rete network. At many places,
 * my code makes redundant things. In exchange, the code is more readable,
 * more understandable and more powerful, since with efficiency in mind,
 * it wouldn't be possible for me to implement all the features. 
 * 
 */
public class BeffyRuleCompiler implements RuleCompiler {

	
	/**
	 * this class represents one binding-scope in our rule. While
	 * our org.jamocha.engine.scope.Scope class is a runtime-scope
	 * (with values for the bindings) we need a simpler construct
	 * here for just mapping the compiletime-structure.
	 * One scope is simply a set of conditions. Each scope has a parent
	 * scope and some child scopes. 
	 */
	protected class Scope {
		
		private List<Condition> conditions;
		
		private List<Scope> childs;
		
		private Scope parent;
		
		private void setParent(Scope s) {
			parent = s;
		}
		
		public Scope(List<Condition> c) {
			this.conditions = c;
			childs = new ArrayList<Scope>();
		}
		
		public Scope() {
			this(new ArrayList<Condition>());
		}
		
		public void addCondition(Condition c) {
			conditions.add(c);
		}
		
		public List<Condition> getConditions() {
			return Collections.unmodifiableList(conditions);
		}

		public List<Scope> getChilds() {
			return Collections.unmodifiableList(childs);
		}
		
		public void addChild(Scope s) {
			childs.add(s);
			s.setParent(this);
		}
		
		public Scope getParent() {
			return parent;
		}
		
		public Set<String> getUsedBindings() {
			Set<String> result = new HashSet<String>();
			for (Condition c : getConditions())	{
				for (Constraint constr : c.getFlatConstraints()) {
					if (constr instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) constr;
						result.add(bc.getConstraintName());
					}
				}
			}
			return result;
		}
		
		public Set<String> getIntroducedBindings() {
			Set<String> result = getUsedBindings();
			Scope parent = this.parent;
			while (parent != null) {
				Set<String> introducedEarlier = parent.getUsedBindings();
				result.removeAll(introducedEarlier);
				parent = parent.parent;
			}
			return result;
		}
		
	}
	
	/**
	 * this class holds some information about the rule, like
	 * mapping between join-nodes <> Conditions <> Condition-Indices
	 * and some more information.
	 */
	protected class RuleCompilation {
		
		private Rule rule;
		
		private Map<Condition,Integer> conditionIndices;
		
		private Scope rootScope;
		
		private BindingTableau bindingTableau;
		
		public RuleCompilation(Rule r) throws CompileRuleException {
			rule = r;
			computeConditionIndices();
			bindingTableau = new BindingTableau(this);
		}

		/**
		 * here all condition indices are computed. this includes
		 * a mapping Condition->index, Scopes and some more
		 */
		private int computeConditionIndices(List<Condition> list, int start, Scope s) {
			int i = start;
			for (Condition c : list) {
				s.addCondition(c);
				addConditionIndex(c, i);
				i++;
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested)c;
					Scope next = s;
					if (cwn instanceof NotExistsCondition || cwn instanceof ExistsCondition) {
						Scope s2 = new Scope();
						s.addChild(s2);
						next = s2;
					}
					i = computeConditionIndices(cwn.getNestedConditions(), i, next);
				}
			}
			return i;
		}
		
		private void addConditionIndex(Condition c, int i) {
			conditionIndices.put(c, i);
		}



		private void computeConditionIndices() {
			rootScope = new Scope();
			conditionIndices = new HashMap<Condition, Integer>();
			computeConditionIndices(rule.getConditions(), 0, rootScope);
		}


		int getConditionIndex(Condition c) {
			return conditionIndices.get(c);
		}

		public Scope getRootScope() {
			return rootScope;
		}
		
		
	}
	
	/**
	 * this class represents the occurence of a binding.
	 */
	protected class BindingOccurence {
		
		private int conditionIndex;
		
		private boolean ident;
		
		private BoundConstraint constr;
		
		public BindingOccurence(int condIdx, boolean ident, BoundConstraint bc) {
			this.conditionIndex = condIdx;
			this.ident = ident;
			this.constr = bc;
		}
		
		public String toString() {
			return "["+conditionIndex+"id:"+ident+"]";
		}
		
		public int getConditionIndex() {
			return conditionIndex;
		}
		
		public boolean isIdent() {
			return ident;
		}
		
		public BoundConstraint getConstraint() {
			return constr;
		}
		
	}

	/**
	 * this tableau holds the information, where to find the "pivot"-element
	 * for a binding (means: a non-negated, most-top-level-scoped occurence,
	 * which can be used as reference in all comparison operations later on
	 */
	protected class BindingTableau {
		
		private Map<String, List<BindingOccurence> > bindingOccurences;
		
		private Map<String, BindingOccurence> pivotElements;
		
		private RuleCompilation ruleCompilation;
		
		public BindingTableau(RuleCompilation rule) throws CompileRuleException {
			this.ruleCompilation = rule;
			bindingOccurences = new HashMap<String, List<BindingOccurence>>();
			computeBindingOccurences();
			computePivots();
		}
		
		private void computeBindingOccurences() {
			computeBindingOccurences(ruleCompilation.rule.getConditions());
		}

		private void computeBindingOccurences(List<Condition> conditions) {
			for (Condition c : conditions) {
				for (Constraint con : c.getFlatConstraints()) {
					if (con instanceof BoundConstraint) {
						BoundConstraint bc = (BoundConstraint) con;
						String bindingName = bc.getConstraintName();
						boolean ident;
						if (c instanceof ObjectCondition) {
							// in an object condition, an occurence is "ident", iff it is not negated
							ident = !bc.isNegated();
						} else {
							// in all other condition types (test, and so on) it can't be ident
							ident = false;
						}
						addSingleBindingOccurence(c, bindingName, bc, ident);
					}
				}
				if (c instanceof ConditionWithNested) {
					ConditionWithNested cwn = (ConditionWithNested) c;
					computeBindingOccurences(cwn.getNestedConditions());
				}
			}
		}

		private void computePivots() throws CompileRuleException {
			pivotElements = new HashMap<String, BindingOccurence>();
			log("i begin with computing pivot elements");
			computePivots(ruleCompilation.getRootScope());
			log("i finished computing pivot elements");
		}
		
		private void computePivots(Scope scope) throws CompileRuleException {
			//compute all pivot elements here
			Set<String> bindingsHere = scope.getIntroducedBindings();
			for (String bind : bindingsHere){
				List<BindingOccurence> occurences = getOccurencesList(bind);
				
				BindingOccurence pivot = null;
				// first, we try to find a pivot element outside an or-condition
				BindingOccurence spivot = null;
				for (BindingOccurence occ : occurences) {
					if (!occ.ident) continue; // we only need occurences, which are not negated or anything like that
					
					// we check whether our occurence is inside an or-condition. we want to drop it for now, if so!
					BoundConstraint bc = occ.getConstraint();
					Condition c = bc.getParentCondition();
					assert(c instanceof ObjectCondition);
					
					if (spivot == null) {
						//if we dont have any pivot element until here, we take everything
						spivot = occ; 
					} else {
						// else, we have to check, whether the new one is better than the old one (better = lower index)
						if (occ.conditionIndex < spivot.conditionIndex) spivot = occ;
					}
				}
				
				if (pivot == null) throw new CompileRuleException("Sorry, but the rule compiler was" +
																  "not able to determine a pivot element" +
																  " for the binding ?"+bind+". This can be" +
																  " our fault or is because of cyclic definition" +
																  " in your rule. You can try to fix that by changing" +
																  " your bindings!");
				pivotElements.put(bind, pivot);
				log("Determined pivot element for ?"+bind+" at "+pivot);
			}
			
			
			//delegate computation for all child scopes to a new call
			for (Scope child: scope.getChilds()) {
				computePivots(child);
			}
		}

		public int getPivotCondition(String binding) {
			return pivotElements.get(binding).getConditionIndex();
		}

		private List<BindingOccurence> getOccurencesList(String b) {
			List<BindingOccurence> result = bindingOccurences.get(b);
			if (result == null) {
				result = new ArrayList<BindingOccurence>();
				bindingOccurences.put(b, result);
			}
			return result;
		}
		
		private void addSingleBindingOccurence(Condition cond, String binding, BoundConstraint bc,  boolean ident) {
			List<BindingOccurence> oclist = getOccurencesList(binding);
			BindingOccurence occ = new BindingOccurence(ruleCompilation.getConditionIndex(cond), ident, bc);
			oclist.add(occ);
		}
		
	}
	
	private JamochaLogger l;
	
	public BeffyRuleCompiler(Engine engine, RootNode root, ReteNet net) {
		l = Logging.logger(this.getClass());
	}
	
	private void log(String message) {
		l.debug(message);
	}

	public void addListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}

	public void addObjectTypeNode(Template template) {
		// TODO Auto-generated method stub

	}
	
	protected void compileSubRule(Rule r) throws CompileRuleException {
		RuleCompilation ruleCompilation = new RuleCompilation(r);
		// we assume, that our precompiler created a rule, which has only one
		// and-condition at top-level
		Condition topLevel = r.getConditions().get(0);
		compile(ruleCompilation, topLevel);
	}

	private boolean compileObjectCondition(RuleCompilation ruleComp, ObjectCondition cond) {
		return false;
	}

	private boolean compileTestCondition(RuleCompilation ruleComp, TestCondition cond) {
		return false;
	}

	private boolean compileAndCondition(RuleCompilation ruleComp, AndCondition cond) throws CompileRuleException {
		/* This is some kind of entry point in our real compilation effort.
		 * At this point, we assume, that all nested CEs are object-conditions, test conditions
		 * and not-exists-conditions. we have no further nested and-condition here (since this 
		 * would not play any role inside an and condition) and no or-conditions (since they are
		 * replaced by a number of subrules).
		 */
		List<Condition> conds= cond.getNestedConditions();
		boolean weMadeProgress = true;
		while (weMadeProgress && !conds.isEmpty() ) {
			weMadeProgress = false;
			Iterator<Condition> iterC = conds.iterator();
			while (iterC.hasNext()) {
				Condition c = iterC.next();
				if (compile(ruleComp,c)) {
					weMadeProgress = true;
					iterC.remove();
				}
			}
		}
		return (conds.isEmpty());
	}

	private boolean compileNotExistsCondition(RuleCompilation ruleComp, NotExistsCondition cond) {
		return false;
	}

	private boolean compile(RuleCompilation ruleCompilation, Condition condition) throws CompileRuleException {
		if (condition instanceof ObjectCondition) {
			return compileObjectCondition( ruleCompilation, (ObjectCondition) condition);
		} else if (condition instanceof TestCondition) {
			return compileTestCondition( ruleCompilation, (TestCondition) condition);
		} else if (condition instanceof AndCondition) {
			return compileAndCondition( ruleCompilation, (AndCondition) condition);
		} else if (condition instanceof NotExistsCondition) {
			return compileNotExistsCondition( ruleCompilation, (NotExistsCondition) condition);
		} else {
			throw new CompileRuleException("unimplemented condition type "+condition.getClass().getSimpleName()+" found");
		}
	}

	protected List<Rule> precompile(Rule rule) {
		List<Rule> result = new ArrayList<Rule>();
		
		AndCondition topLevelAnd = new AndCondition();
		for(Condition c:rule.getConditions()) topLevelAnd.addNestedCondition(c);
		rule.getConditions().clear();
		rule.getConditions().add(topLevelAnd);
		
		return result;
	}
	
	public boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException, CompileRuleException {
		
		List<Rule> subRules = precompile(rule);
		
		for (Rule r : subRules)	compileSubRule(r);
		
		return true;
	}

	public Binding getBinding(String varName, Rule r) {
		// TODO Auto-generated method stub
		return null;
	}


	public TerminalNode getTerminalNode(Rule rule) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getValidateRule() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(CompilerListener listener) {
		// TODO Auto-generated method stub

	}


	public void setValidateRule(boolean validate) {
		// TODO Auto-generated method stub

	}

}
