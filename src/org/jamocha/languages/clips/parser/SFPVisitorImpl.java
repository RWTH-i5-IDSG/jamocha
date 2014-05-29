/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.languages.clips.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.Template;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPBooleanType;
import org.jamocha.languages.clips.parser.generated.SFPConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPConstructDescription;
import org.jamocha.languages.clips.parser.generated.SFPDateTimeType;
import org.jamocha.languages.clips.parser.generated.SFPDeffunctionConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDefruleConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDeftemplateConstruct;
import org.jamocha.languages.clips.parser.generated.SFPExistsCE;
import org.jamocha.languages.clips.parser.generated.SFPExpression;
import org.jamocha.languages.clips.parser.generated.SFPFalse;
import org.jamocha.languages.clips.parser.generated.SFPFloat;
import org.jamocha.languages.clips.parser.generated.SFPFloatType;
import org.jamocha.languages.clips.parser.generated.SFPInteger;
import org.jamocha.languages.clips.parser.generated.SFPIntegerType;
import org.jamocha.languages.clips.parser.generated.SFPLHSSlot;
import org.jamocha.languages.clips.parser.generated.SFPNotFunction;
import org.jamocha.languages.clips.parser.generated.SFPOrFunction;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPParserTreeConstants;
import org.jamocha.languages.clips.parser.generated.SFPSingleSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPSingleVariable;
import org.jamocha.languages.clips.parser.generated.SFPSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.clips.parser.generated.SFPString;
import org.jamocha.languages.clips.parser.generated.SFPStringType;
import org.jamocha.languages.clips.parser.generated.SFPSymbol;
import org.jamocha.languages.clips.parser.generated.SFPSymbolType;
import org.jamocha.languages.clips.parser.generated.SFPTemplatePatternCE;
import org.jamocha.languages.clips.parser.generated.SFPTerm;
import org.jamocha.languages.clips.parser.generated.SFPTrue;
import org.jamocha.languages.clips.parser.generated.SFPTypeAttribute;
import org.jamocha.languages.clips.parser.generated.SFPTypeSpecification;
import org.jamocha.languages.clips.parser.generated.SFPUnorderedLHSFactBody;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;

/**
 * Needs consideration: how to treat
 * <ul>
 * <li>rules without any LHS CEs</li>
 * <li></li>
 * </ul>
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class SFPVisitorImpl implements SelectiveSFPVisitor {

	/**
	 * About scopes
	 * <ul>
	 * <li>variables are valid within a single defrule or deffunction</li>
	 * <li>rules, functions, and templates are valid within modules</li>
	 * <li>rules, functions, and template may use the same names</li>
	 * </ul>
	 */
	final ScopeStack scope = new ScopeStack();
	HashMap<Symbol, SingleVariableProperties> symbolTableVariables;
	final HashMap<Symbol, Template> symbolTableTemplates = new HashMap<>();
	final HashMap<Symbol, Function<?>> symbolTableFunctions = new HashMap<>();
	final HashMap<Symbol, RuleProperties> symbolTableRules = new HashMap<>();

	@Override
	public Object visit(final SFPStart node, final Object data) {
		assert node.jjtGetNumChildren() == 1;
		SelectiveSFPVisitor.sendVisitor(new SFPStartVisitor(), node.jjtGetChild(0), data);
		return data;
	}

	@Value
	static class RuleProperties {
		final String description;
		final PathFilter condition;
	}

	static enum SingeVariableExistentialState {
		NORMAL, EXISENTIAL, NEGATED;
	}

	@RequiredArgsConstructor
	static class SingleVariableProperties {
		final Template template;
		final SlotAddress slotAddress;
		SingeVariableExistentialState state;

		SlotType getType() {
			return slotAddress.getSlotType(template);
		}
	}

	final static EnumSet<SlotType> Number = EnumSet.of(SlotType.LONG, SlotType.DOUBLE);
	final static EnumSet<SlotType> Constant = EnumSet.of(SlotType.NIL, SlotType.DATETIME,
			SlotType.SYMBOL, SlotType.STRING, SlotType.LONG, SlotType.DOUBLE, SlotType.BOOLEAN);

	class SFPSymbolVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(final SFPSymbol node, final Object data) {
			this.symbol = SFPVisitorImpl.this.scope.getOrCreate(node.jjtGetValue().toString());
			return data;
		}
	}

	class SFPStringVisitor implements SelectiveSFPVisitor {
		String string;

		@Override
		public Object visit(final SFPString node, final Object data) {
			this.string = node.jjtGetValue().toString();
			return data;
		}
	}

	class SFPValueVisitor implements SelectiveSFPVisitor {
		SlotType type;
		Object value;
		final EnumSet<SlotType> allowed;

		public SFPValueVisitor(final SlotType firstAllowed, final SlotType... restAllowed) {
			this.allowed = EnumSet.of(firstAllowed, restAllowed);
		}

		public SFPValueVisitor(final SlotType allowed) {
			this.allowed = EnumSet.of(allowed);
		}

		public SFPValueVisitor(final EnumSet<SlotType> allowed) {
			this.allowed = allowed;
		}

		public SFPValueVisitor() {
			this.allowed = EnumSet.allOf(SlotType.class);
		}

		@Override
		public Object visit(final SFPFloat node, final Object data) {
			if (!this.allowed.contains(SlotType.DOUBLE))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.DOUBLE;
			this.value = Double.parseDouble(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(final SFPInteger node, final Object data) {
			if (!this.allowed.contains(SlotType.LONG))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.LONG;
			this.value = Long.parseLong(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(final SFPSymbol node, final Object data) {
			if (!this.allowed.contains(SlotType.SYMBOL))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.SYMBOL;
			this.value = SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node, data).symbol;
			return data;
		}

		@Override
		public Object visit(final SFPTrue node, final Object data) {
			if (!this.allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			this.value = true;
			return data;
		}

		@Override
		public Object visit(final SFPFalse node, final Object data) {
			if (!this.allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			this.value = false;
			return data;
		}

		@Override
		public Object visit(final SFPString node, final Object data) {
			if (!this.allowed.contains(SlotType.STRING))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.STRING;
			this.value = SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node, data).string;
			return data;
		}
		// TBD Nil, DateTime
	}

	class SFPTypeVisitor implements SelectiveSFPVisitor {
		SlotType type;
		final EnumSet<SlotType> allowed;

		public SFPTypeVisitor(final SlotType firstAllowed, final SlotType... restAllowed) {
			this.allowed = EnumSet.of(firstAllowed, restAllowed);
		}

		public SFPTypeVisitor(final SlotType allowed) {
			this.allowed = EnumSet.of(allowed);
		}

		public SFPTypeVisitor(final EnumSet<SlotType> allowed) {
			this.allowed = allowed;
		}

		public SFPTypeVisitor() {
			this.allowed = EnumSet.allOf(SlotType.class);
		}

		@Override
		public Object visit(final SFPFloatType node, final Object data) {
			if (!this.allowed.contains(SlotType.DOUBLE))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.DOUBLE;
			return data;
		}

		@Override
		public Object visit(final SFPIntegerType node, final Object data) {
			if (!this.allowed.contains(SlotType.LONG))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.LONG;
			return data;
		}

		@Override
		public Object visit(final SFPSymbolType node, final Object data) {
			if (!this.allowed.contains(SlotType.SYMBOL))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.SYMBOL;
			return data;
		}

		@Override
		public Object visit(final SFPBooleanType node, final Object data) {
			if (!this.allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			return data;
		}

		@Override
		public Object visit(final SFPStringType node, final Object data) {
			if (!this.allowed.contains(SlotType.STRING))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.STRING;
			return data;
		}

		@Override
		public Object visit(final SFPDateTimeType node, final Object data) {
			if (!this.allowed.contains(SlotType.DATETIME))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.DATETIME;
			return data;
		}
		// TBD LEXEME = STRING | SYMBOL, NUMBER = INTEGER | FLOAT
	}

	class SFPTypeSpecificationVisitor implements SelectiveSFPVisitor {
		SlotType type;

		// <type-specification> ::= <allowed-type>+ | ?VARIABLE
		// void: ( ( AllowedType() )+ | VariableType() )

		// <allowed-type> ::= SYMBOL | STRING | LEXEME | INTEGER | FLOAT | NUMBER |
		// EXTERNAL-ADDRESS | FACT-ADDRESS | DATETIME
		// void: ( SymbolType() | StringType() | DateTimeType() | LexemeType() | IntegerType() |
		// FloatType() | NumberType() | BooleanType() )

		@Override
		public Object visit(final SFPTypeSpecification node, final Object data) {
			if (node.jjtGetNumChildren() != 1) {
				throw SelectiveSFPVisitor
						.dumpAndThrowMe(node, IllegalArgumentException::new,
								"Restriction of template fields to multiple types is not supported at the moment!");
			}
			// TBD LEXEME = STRING | SYMBOL, NUMBER = INTEGER | FLOAT
			this.type =
					SelectiveSFPVisitor.sendVisitor(
							new SFPTypeVisitor(EnumSet.of(/* SlotType.LEXEME, */SlotType.SYMBOL,
									SlotType.STRING, SlotType.DATETIME, SlotType.LONG,
									SlotType.DOUBLE/* , SlotType.NUMBER */)), node.jjtGetChild(0),
							data).type;
			return data;
		}
		// TBD VariableType
	}

	class SFPTemplateAttributeVisitor implements SelectiveSFPVisitor {
		SlotType slotType;

		// <template-attribute> ::= <default-attribute> | <constraint-attribute>
		// <LBRACE> ( DefaultAttribute() | DynamicAttribute() | ConstraintAttribute() ) <RBRACE>

		// <constraint-attribute> ::= <type-attribute> | <allowed-constant-attribute> |
		// <range-attribute> | <cardinality-attribute>
		// ( TypeAttribute() | AllowedConstantAttribute() | RangeAttribute() |
		// CardinalityAttribute() )

		// <type-attribute> ::= (type <type-specification>)
		// <TYPE> TypeSpecification()
		@Override
		public Object visit(final SFPTypeAttribute node, final Object data) {
			if (node.jjtGetNumChildren() != 1) {
				throw SelectiveSFPVisitor
						.dumpAndThrowMe(node, IllegalArgumentException::new,
								"Restriction of template fields to multiple types is not supported at the moment!");
			}
			this.slotType =
					SelectiveSFPVisitor.sendVisitor(new SFPTypeSpecificationVisitor(),
							node.jjtGetChild(0), data).type;
			return data;
		}
	}

	class SFPSingleSlotDefinitionVisitor implements SelectiveSFPVisitor {
		Slot slot;

		// <single-slot-definition> ::= ( slot <slot-name> <template-attribute>*)
		// <SLOT> ( Symbol() ( TemplateAttribute() )* )
		@Override
		public Object visit(final SFPSingleSlotDefinition node, final Object data) {
			if (node.jjtGetNumChildren() != 2) {
				throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new,
						"For now, slot definitions consist of a name and a type restriction!");
			}
			final Symbol name =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			final SlotType type =
					SelectiveSFPVisitor.sendVisitor(new SFPTemplateAttributeVisitor(),
							node.jjtGetChild(1), data).slotType;
			this.slot = new Slot(type, name.getImage());
			return data;
		}
	}

	class SFPDeftemplateConstructElementsVisitor implements SelectiveSFPVisitor {
		String comment;
		final LinkedList<Slot> slotDefinitions = new LinkedList<>();

		// <comment> ::= <string>
		@Override
		public Object visit(final SFPConstructDescription node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment =
					SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0),
							data).string;
			return data;
		}

		// <slot-definition> ::= <single-slot-definition> | <multislot-definition>
		@Override
		public Object visit(final SFPSlotDefinition node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			// TBD add support for multislot-definition
			this.slotDefinitions.add(SelectiveSFPVisitor.sendVisitor(
					new SFPSingleSlotDefinitionVisitor(), node.jjtGetChild(0), data).slot);
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPConditionalElementVisitor implements SelectiveSFPVisitor {

		class SFPTermElementsVisitor implements SelectiveSFPVisitor {
			Symbol symbol;

			// currently:
			// ConnectedConstraint(): Term()
			// Term(): SingleVariable()
			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				assert node.jjtGetNumChildren() == 0;
				this.symbol = SFPVisitorImpl.this.scope.getOrCreate(node.jjtGetValue().toString());
				return data;
			}
		}

		class SFPConnectedConstraintElementsVisitor implements SelectiveSFPVisitor {
			Symbol varName;

			// currently:
			// ConnectedConstraint(): Term()
			// Term(): SingleVariable()
			@Override
			public Object visit(final SFPTerm node, final Object data) {
				assert node.jjtGetNumChildren() == 1;
				this.varName =
						SelectiveSFPVisitor.sendVisitor(new SFPTermElementsVisitor(),
								node.jjtGetChild(0), data).symbol;
				return data;
			}
		}

		class SFPConstraintVisitor implements SelectiveSFPVisitor {
			Symbol varName;

			// currently:
			// void Constraint(): ConnectedConstraint()
			// ConnectedConstraint(): Term()
			// Term(): SingleVariable()
			@Override
			public Object visit(final SFPConnectedConstraint node, final Object data) {
				assert node.jjtGetNumChildren() == 1;
				this.varName =
						SelectiveSFPVisitor.sendVisitor(
								new SFPConnectedConstraintElementsVisitor(), node.jjtGetChild(0),
								data).varName;
				return data;
			}
		}

		@RequiredArgsConstructor
		class SFPUnorderedLHSFactBodyElementsVisitor implements SelectiveSFPVisitor {
			final Template template;
			final Set<Symbol> variablesDefined = new HashSet<>();

			// currently:
			// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
			// LHSSlot(): ( Symbol() Constraint )
			// void Constraint(): ConnectedConstraint()
			// ConnectedConstraint(): Term()
			// Term(): SingleVariable()
			@Override
			public Object visit(final SFPLHSSlot node, final Object data) {
				assert node.jjtGetNumChildren() == 2;
				final Symbol slotName =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(),
								node.jjtGetChild(0), data).symbol;
				final Symbol varName =
						SelectiveSFPVisitor.sendVisitor(new SFPConstraintVisitor(),
								node.jjtGetChild(1), data).varName;
				if (!SFPVisitorImpl.this.symbolTableVariables.containsKey(varName)) {
					// Variable name FIXME
					SFPVisitorImpl.this.symbolTableVariables.put(
							varName,
							new SingleVariableProperties(template, template.getSlotAddress(slotName
									.getImage())));
					this.variablesDefined.add(varName);
				} else {

				}
				return data;
			}
		}

		@RequiredArgsConstructor
		class SFPTemplatePatternCEElementsVisitor implements SelectiveSFPVisitor {
			final Template template;

			// currently:
			// TemplatePatternCE ( Symbol() (UnorderedLHSFactBody())+ )
			// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
			@Override
			public Object visit(final SFPUnorderedLHSFactBody node, final Object data) {
				assert node.jjtGetNumChildren() == 1;
				SelectiveSFPVisitor.sendVisitor(
						new SFPUnorderedLHSFactBodyElementsVisitor(template), node.jjtGetChild(0),
						data);
				return data;
			}
		}

		PredicateWithArguments predicateWithArguments;
		final Set<Symbol> variablesDefined;

		// <conditional-element> ::= <pattern-CE> | <assigned-pattern-CE> | <not-CE> | <and-CE> |
		// <or-CE> | <logical-CE> | <test-CE> | <exists-CE> | <forall-CE>

		// void ConditionalElement() ( ( <LBRACE> ( TemplatePatternCE()| BooleanFunction() |
		// LogicalCE() | TestCE() | ExistsCE() | ForallCE() ) <RBRACE> ) | AssignedPatternCE() )

		// <assigned-pattern-CE> ::= <single-field-variable> <- <pattern-CE>
		// <pattern-CE> ::= <ordered-pattern-CE> | <template-pattern-CE> | <object-pattern-CE>
		// <ordered-pattern-CE> ::= (<symbol> <constraint>*)
		// <template-pattern-CE> ::= (<deftemplate-name> <LHS-slot>*)
		// <object-pattern-CE> ::= (object <attribute-constraint>*)

		// <LHS-slot> ::= <single-field-LHS-slot> | <multifield-LHS-slot>
		// <single-field-LHS-slot> ::= (<slot-name> <constraint>)
		// <constraint> ::= ? | $? | <connected-constraint>
		// <connected-constraint> ::= <single-constraint> | <single-constraint> &
		// <connected-constraint> | <single-constraint> | <connected-constraint>
		// <single-constraint> ::= <term> | ~<term>
		// <term> ::= <constant> | <single-field-variable> | <multifield-variable> |
		// :<function-call> | =<function-call>

		// TemplatePatternCE(): ( Symbol() ( (UnorderedLHSFactBody())+ | OrderedLHSFactBody() ) )
		// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
		// OrderedLHSFactBody(): ( Constraint() )*
		// LHSSlot(): ( Symbol() Constraint() )
		// void Constraint(): SingleFieldWildcard() | MultiFieldWildcard() | ConnectedConstraint()
		// SingleFieldWildcard() : <SFWILDCARD>
		// < SFWILDCARD: "?" >
		// ConnectedConstraint(): ( Term() [ AmpersandConnectedConstraint() |
		// LineConnectedConstraint() ] )
		// AmpersandConnectedConstraint(): ( <AMPERSAND> Term() [ LineConnectedConstraint() |
		// AmpersandConnectedConstraint() ] )
		// LineConnectedConstraint(): ( <LINE> Term() [ LineConnectedConstraint() |
		// AmpersandConnectedConstraint() ] )
		// < AMPERSAND: "&" >
		// < LINE: "|" >
		// Term(): ( [ Negation() ] ( LOOKAHEAD(3) Constant() | SingleVariable() | MultiVariable() |
		// Colon() | Equals() ) )
		// Negation(): <TILDE>
		// Colon(): <COLON> FunctionCall()
		// Equals(): <EQUALS> FunctionCall()
		// SingleVariable(): <SINGLEVAR>
		// < SINGLEVAR: ("?" <VARSYMBOL>) >

		// we currently allow:
		// TemplatePatternCE ( Symbol() (UnorderedLHSFactBody())+ )
		// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
		// LHSSlot(): ( Symbol() Constraint )
		// void Constraint(): ConnectedConstraint()
		// ConnectedConstraint(): Term()
		// Term(): SingleVariable()

		@Override
		public Object visit(final SFPTemplatePatternCE node, final Object data) {
			assert node.jjtGetNumChildren() > 1;
			final Symbol templateName =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			// the effect of this node should be the creation of one or more slot variables
			final Template template = SFPVisitorImpl.this.symbolTableTemplates.get(templateName);
			if (null == template) {
				throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new,
						"No template with name " + templateName + " defined yet!");
			}
			final Set<Symbol> variablesDefinedInChildren =
					SelectiveSFPVisitor
							.stream(node, 1)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPUnorderedLHSFactBodyElementsVisitor(template), n, data).variablesDefined)
							.collect(HashSet::new, HashSet::addAll, HashSet::addAll);
			this.variablesDefined.addAll(variablesDefinedInChildren);
			return data;
		}

		// <logical-CE> ::= (logical <conditional-element>+)
		// LogicalCE() : <LOGICAL> ( ConditionalElement() )+
		// TBD LogicalCE

		// <and-CE> ::= (and <conditional-element>+)
		// AndFunction(): <AND> (ConditionalElement())+
		@Override
		public Object visit(final SFPAndFunction node, final Object data) {
			// as `(exists (TemplatePatternCE)+)` is equivalent to
			// `(not (not (and (TemplatePatternCE)+)))`, and needs to support TemplatePatternCE
			// children, too. These will not be added to the Predicate <and>
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] predicatesWithArguments =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPConditionalElementVisitor(this.variablesDefined), n,
									data).predicateWithArguments)
							.toArray(PredicateWithArguments[]::new);
			final int size = predicatesWithArguments.length;
			if (size > 0) {
				if (size == 1) {
					this.predicateWithArguments = predicatesWithArguments[0];
				} else {
					final Predicate and =
							FunctionDictionary.lookupPredicate(
									org.jamocha.filter.impls.predicates.And.inClips,
									SlotType.nCopies(SlotType.BOOLEAN, size));
					this.predicateWithArguments =
							new PredicateWithArgumentsComposite(and, predicatesWithArguments);
				}
			}
			return data;
		}

		// <or-CE> ::= (or <conditional-element>+)
		// OrFunction(): <OR> (ConditionalElement())+
		@Override
		public Object visit(final SFPOrFunction node, final Object data) {
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] predicatesWithArguments =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPConditionalElementVisitor(this.variablesDefined), n,
									data))
							.map(vis -> {
								this.variablesDefined.addAll(vis.variablesDefined);
								return vis.predicateWithArguments;
							})
							.map(pwa -> {
								if (null == pwa)
									throw SelectiveSFPVisitor
											.dumpAndThrowMe(node, IllegalArgumentException::new,
													"<or> only supports child nodes resulting in PredicateWithArgument!");
								return pwa;
							}).toArray(PredicateWithArguments[]::new);
			final Predicate or =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.Or.inClips,
							SlotType.nCopies(SlotType.BOOLEAN, predicatesWithArguments.length));
			this.predicateWithArguments =
					new PredicateWithArgumentsComposite(or, predicatesWithArguments);
			return data;
		}

		// <not-CE> ::= (not <conditional-element>)
		// NotFunction(): <NOT> ConditionalElement()
		@Override
		public Object visit(final SFPNotFunction node, final Object data) {
			// not has multiple meanings: boolean operator, not exists operator
			assert node.jjtGetNumChildren() == 1;
			final SFPConditionalElementVisitor visitor =
					SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(
							this.variablesDefined), node.jjtGetChild(0), data);
			final int id = node.getId();
			if (id == SFPParserTreeConstants.JJTNOTFUNCTION
					|| id == SFPParserTreeConstants.JJTFORALLCE
					|| id == SFPParserTreeConstants.JJTEXISTSCE
					)
				if (node.jjtGetParent().getId() == SFPParserTreeConstants.JJTDEFRULECONSTRUCT) {
					// top-level not-CE meaning this is a not-exists CE
					for (final Symbol var : this.variablesDefined) {
						final SingleVariableProperties singleVariableProperties =
								SFPVisitorImpl.this.symbolTableVariables.get(var);
						switch (singleVariableProperties.state) {
						case NORMAL:
						case EXISENTIAL:
							singleVariableProperties.state = SingeVariableExistentialState.NEGATED;
							break;
						case NEGATED:
							singleVariableProperties.state =
									SingeVariableExistentialState.EXISENTIAL;
							break;
						}
					}
				} else if (null != visitor.predicateWithArguments) {
					// test-ce (not (boolean expression))
					final Predicate not =
							FunctionDictionary.lookupPredicate(
									org.jamocha.filter.impls.predicates.Or.inClips,
									SlotType.BOOLEAN);
					this.predicateWithArguments =
							new PredicateWithArgumentsComposite(not, visitor.predicateWithArguments);
				} else {
					throw SelectiveSFPVisitor.dumpAndThrowMe(node,
							UnsupportedOperationException::new,
							"<not> can only be followed by a test-ce or a template-ce");
				}
			return data;
		}

		// <test-CE> ::= (test <function-call>)
		// TestCE() : <TEST> FunctionCall()
		// TODO TestCE

		// <exists-CE> ::= (exists <conditional-element>+)
		// ExistsCE() : <EXISTS> ( ConditionalElement() )+
		@Override
		public Object visit(final SFPExistsCE node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			SelectiveSFPVisitor.stream(node, 0).map(
					n -> SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(
							this.variablesDefined), n, data));
			return data;
		}

		// <forall-CE> ::= (forall <conditional-element> <conditional-element>+)
		// ForallCE() : <FORALL> ConditionalElement() ( LOOKAHEAD(2) ConditionalElement() )+
		// TBD ForallCE

		// <assigned-pattern-CE> ::= <single-field-variable> <- <pattern-CE>
		// AssignedPatternCE(): ( SingleVariable() <ASSIGN> <LBRACE> TemplatePatternCE() <RBRACE> )
		// TODO AssignedPatternCE
	}

	class SFPDefruleConstructElementVisitor extends SFPConditionalElementVisitor {
		String comment;

		public SFPDefruleConstructElementVisitor() {
			super(new HashSet<>());
		}

		// TBD ActionList, Declaration

		// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
		// <conditional-element>* => <expression>*)
		// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
		// ConditionalElement() )* ) <ARROW> ActionList()
		@Override
		public Object visit(final SFPConstructDescription node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment =
					SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0),
							data).string;
			return data;
		}
	}

	class SFPExpressionVisitor implements SelectiveSFPVisitor {
		@Override
		public Object visit(final SFPExpression node, final Object data) {
			return SFPVisitorImpl.this.visit(node, data);
		}
	}

	class SFPStartVisitor implements SelectiveSFPVisitor {
		// Start() : Construct() | Expression()
		// void Construct() : <LBRACE> ( DeftemplateConstruct() | DefglobalConstruct()
		// | DefruleConstruct() | DeffunctionConstruct() | DefmoduleConstruct() ) <RBRACE>

		// <comment> ::= <string>

		@Override
		public Object visit(final SFPDeftemplateConstruct node, final Object data) {
			// <deftemplate-construct> ::= (deftemplate <deftemplate-name> [<comment>]
			// <slot-definition>*)

			// <DEFTEMPLATE> Symbol() [ ConstructDescription() ] ( SlotDefinition() )*
			assert node.jjtGetNumChildren() > 0;
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			final SFPDeftemplateConstructElementsVisitor visitor =
					new SFPDeftemplateConstructElementsVisitor();
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				node.jjtGetChild(i).jjtAccept(visitor, data);
			}
			final String comment = visitor.comment;
			final Template template =
					new Template(
							comment,
							visitor.slotDefinitions.toArray(new Slot[visitor.slotDefinitions.size()]));
			SFPVisitorImpl.this.symbolTableTemplates.put(symbol, template);
			return data;
		}

		@Override
		public Object visit(final SFPDefruleConstruct node, final Object data) {
			// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
			// <conditional-element>* => <expression>*)
			// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
			// ConditionalElement() )* ) <ARROW> ActionList()
			assert node.jjtGetNumChildren() > 1;
			SFPVisitorImpl.this.symbolTableVariables = new HashMap<>();
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			String comment = null;
			final ArrayList<PathFilterElement> pfes = new ArrayList<>();
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				final SFPDefruleConstructElementVisitor visitor =
						new SFPDefruleConstructElementVisitor();
				node.jjtGetChild(i).jjtAccept(visitor, data);
				if (null != visitor.comment) {
					assert null == comment;
					comment = visitor.comment;
				} else if (null != visitor.predicateWithArguments) {
					pfes.add(new PathFilterElement(visitor.predicateWithArguments));
				} else {
					// TBD action list
				}
			}
			SFPVisitorImpl.this.symbolTableRules.put(symbol, new RuleProperties(comment,
					new PathFilter(pfes.toArray(new PathFilterElement[pfes.size()]))));
			SFPVisitorImpl.this.symbolTableVariables = null;
			return data;
		}

		@Override
		public Object visit(SFPDeffunctionConstruct node, Object data) {
			// <deffunction-construct> ::= (deffunction <name> [(functiongroup <groupname>)]
			// [<comment>] (<regular-parameter>* [<wildcard-parameter>]) <expression>*)
			// DeffunctionConstruct(): ( <DEFFUNCTION> Symbol() [ ConstructDescription() ] (<LBRACE>
			// [ FunctionGroup() <RBRACE> <LBRACE> ] ( SingleVariable() )* ( MultiVariable() )*
			// <RBRACE> ) ActionList() )
			assert node.jjtGetNumChildren() > 1;
			SFPVisitorImpl.this.symbolTableVariables = new HashMap<>();
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			// handle comment, function group, variable list, action list
			SFPVisitorImpl.this.symbolTableFunctions.put(symbol, null);
			SFPVisitorImpl.this.symbolTableVariables = null;
			// return data;
			// for now: throw
			return SFPVisitorImpl.this.visit(node, data);
		}

		@Override
		public Object visit(final SFPExpression node, final Object data) {
			SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(), node, data);
			return data;
		}
	}

	public static void main(final String[] args) {
		final boolean verbose = (args != null && args.length == 1 && "verbose".equals(args[0]));
		if (!verbose)
			System.out
					.println("Note: For verbose output type \u005c"java Main verbose\u005c".\u005cn");
		System.out.print("SFP> ");
		final SFPParser p = new SFPParser(System.in);
		try {
			while (true) {
				final SFPStart n = p.Start();
				if (n == null)
					System.exit(0);
				if (verbose)
					SelectiveSFPVisitor.dumpToStdOut(n);
				final Object a = n.jjtAccept(new SFPVisitorImpl(), "");
				System.out.println(a);
			}
		} catch (final Exception e) {
			System.err.println("ERROR[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
			if (verbose)
				e.printStackTrace();
		}
	}
}
