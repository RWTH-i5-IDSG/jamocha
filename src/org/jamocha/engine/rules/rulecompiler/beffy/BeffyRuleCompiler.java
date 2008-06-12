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
import org.jamocha.engine.RetractException;
import org.jamocha.engine.RuleCompiler;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rules.rulecompiler.CompileRuleException;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.RuleException;
import org.jamocha.rules.BoundConstraint;
import org.jamocha.rules.Condition;
import org.jamocha.rules.ConditionWithNested;
import org.jamocha.rules.Constraint;
import org.jamocha.rules.ExistsCondition;
import org.jamocha.rules.NotExistsCondition;
import org.jamocha.rules.ObjectCondition;
import org.jamocha.rules.OrCondition;
import org.jamocha.rules.Rule;


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
	 * here for just mapping the compiletime-structure
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
			rearrangeConditions();
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

		private void rearrangeConditions() {
			// TODO Auto-generated method stub
			
		}

		int getConditionIndex(Condition c) {
			return conditionIndices.get(c);
		}

		public Scope getRootScope() {
			return rootScope;
		}
		
		
	}
	
	/**
	 * normally, we only need one SingleBindingOccurence.
	 * But there are or-conditions, which invalidates this plan.
	 * We need to store more than one location for only one
	 * occurence inside an or-condition.
	 *
	 */
	protected class BindingOccurence {
		
		private List<SingleBindingOccurence> occs;
		
		public BindingOccurence(boolean ident, int condIdx, BoundConstraint bc) {
			this();
			addOccurence(new SingleBindingOccurence(condIdx,ident,bc));
		}
		
		public BindingOccurence() {
			occs = new ArrayList<SingleBindingOccurence>();
		}
		
		public void addOccurence(SingleBindingOccurence oc) {
			occs.add(oc);
		}
		
		public List<SingleBindingOccurence> getOccurences() {
			return Collections.unmodifiableList(occs);
		}
		
	}
	
	/**
	 * this class represents the occurence of a binding.
	 */
	protected class SingleBindingOccurence {
		
		private int conditionIndex;
		
		private boolean ident;
		
		private BoundConstraint constr;
		
		public SingleBindingOccurence(int condIdx, boolean ident, BoundConstraint bc) {
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
		
		private Map<String, List<SingleBindingOccurence> > bindingOccurences;
		
		private Map<String, BindingOccurence> pivotElements;
		
		private RuleCompilation ruleCompilation;
		
		public BindingTableau(RuleCompilation rule) throws CompileRuleException {
			this.ruleCompilation = rule;
			bindingOccurences = new HashMap<String, List<SingleBindingOccurence>>();
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
			Set<String> bindingsHere = scope.getUsedBindings();
			for (String bind : bindingsHere){
				List<SingleBindingOccurence> occurences = getOccurencesList(bind);
				
				BindingOccurence pivot = null;
				// first, we try to find a pivot element outside an or-condition
				SingleBindingOccurence spivot = null;
				for (SingleBindingOccurence occ : occurences) {
					if (!occ.ident) continue; // we only need occurences, which are not negated or anything like that
					
					// we check whether our occurence is inside an or-condition. we want to drop it for now, if so!
					BoundConstraint bc = occ.getConstraint();
					Condition c = bc.getParentCondition();
					assert(c instanceof ObjectCondition);
					boolean insideOr = false;
					while (c != null) {
						c = c.getParentCondition();
						if (c instanceof OrCondition) {
							insideOr = true;
							break;
						}
					}
					if (insideOr) continue;
					
					if (spivot == null) {
						spivot = occ; //we we dont have any pivot element until here, we take everything
					} else {
						// else, we have to check, whether the new one is better than the old one (better = lower index)
						if (occ.conditionIndex < spivot.conditionIndex) spivot = occ;
					}
				}
				if (spivot != null) {
					pivot = new BindingOccurence();
					pivot.addOccurence(spivot);
				}
				
				if (pivot == null) {
					// not much luck, we have to handle multiple occurences inside
					// an or-condition
					//brf();
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
			//TODO
			return 0;
		}

		private List<SingleBindingOccurence> getOccurencesList(String b) {
			List<SingleBindingOccurence> result = bindingOccurences.get(b);
			if (result == null) {
				result = new ArrayList<SingleBindingOccurence>();
				bindingOccurences.put(b, result);
			}
			return result;
		}
		
		private void addSingleBindingOccurence(Condition cond, String binding, BoundConstraint bc,  boolean ident) {
			List<SingleBindingOccurence> oclist = getOccurencesList(binding);
			SingleBindingOccurence occ = new SingleBindingOccurence(ruleCompilation.getConditionIndex(cond), ident, bc);
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

	public boolean addRule(Rule rule) throws AssertException, RuleException, EvaluationException, CompileRuleException {
		RuleCompilation ruleCompilation = new RuleCompilation(rule);
		
		
		return true;
	}

	public Binding getBinding(String varName, Rule r) {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectTypeNode getObjectTypeNode(Template template) {
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

	public void removeObjectTypeNode(ObjectTypeNode node)
			throws RetractException {
		// TODO Auto-generated method stub

	}

	public void setValidateRule(boolean validate) {
		// TODO Auto-generated method stub

	}

}
