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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Value;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.memory.javaimpl.Template;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.PathFilter.PathFilterElement;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.clips.parser.generated.Node;
import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPBooleanType;
import org.jamocha.languages.clips.parser.generated.SFPConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPConstructDescription;
import org.jamocha.languages.clips.parser.generated.SFPDateTimeType;
import org.jamocha.languages.clips.parser.generated.SFPDefruleConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDeftemplateConstruct;
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

public class SFPVisitorImpl implements SelectiveSFPVisitor {

	final ScopeStack scope = new ScopeStack();
	final HashMap<Symbol, Template> symbolTableTemplates = new HashMap<>();
	final HashMap<Symbol, Function<?>> symbolTableFunctions = new HashMap<>();

	@Override
	public Object visit(SFPStart node, Object data) {
		assert node.jjtGetNumChildren() == 1;
		sendVisitor(new SFPStartVisitor(), node.jjtGetChild(0), data);
		return data;
	}

	public <V extends SelectiveSFPVisitor, N extends Node> V sendVisitor(final V visitor,
			final N node, final Object data) {
		node.jjtAccept(visitor, data);
		return visitor;
	}

	public Stream<Node> stream(final Node node, final int startIndex) {
		return IntStream.range(startIndex, node.jjtGetNumChildren()).mapToObj(
				i -> node.jjtGetChild(i));
	}

	final static EnumSet<SlotType> Number = EnumSet.of(SlotType.LONG, SlotType.DOUBLE);
	final static EnumSet<SlotType> Constant = EnumSet.of(SlotType.NIL, SlotType.DATETIME,
			SlotType.SYMBOL, SlotType.STRING, SlotType.LONG, SlotType.DOUBLE, SlotType.BOOLEAN);

	class SFPSymbolVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(SFPSymbol node, Object data) {
			this.symbol = scope.getOrCreate(node.jjtGetValue().toString());
			return data;
		};
	}

	class SFPStringVisitor implements SelectiveSFPVisitor {
		String string;

		@Override
		public Object visit(SFPSymbol node, Object data) {
			this.string = node.jjtGetValue().toString();
			return data;
		};
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
		public Object visit(SFPFloat node, Object data) {
			if (!allowed.contains(SlotType.DOUBLE))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.DOUBLE;
			this.value = Double.parseDouble(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(SFPInteger node, Object data) {
			if (!allowed.contains(SlotType.LONG))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.LONG;
			this.value = Long.parseLong(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(SFPSymbol node, Object data) {
			if (!allowed.contains(SlotType.SYMBOL))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.SYMBOL;
			this.value = sendVisitor(new SFPSymbolVisitor(), node, data).symbol;
			return data;
		}

		@Override
		public Object visit(SFPTrue node, Object data) {
			if (!allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			this.value = true;
			return data;
		}

		@Override
		public Object visit(SFPFalse node, Object data) {
			if (!allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			this.value = false;
			return data;
		}

		@Override
		public Object visit(SFPString node, Object data) {
			if (!allowed.contains(SlotType.STRING))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.STRING;
			this.value = sendVisitor(new SFPStringVisitor(), node, data).string;
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
		public Object visit(SFPFloatType node, Object data) {
			if (!allowed.contains(SlotType.DOUBLE))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.DOUBLE;
			return data;
		}

		@Override
		public Object visit(SFPIntegerType node, Object data) {
			if (!allowed.contains(SlotType.LONG))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.LONG;
			return data;
		}

		@Override
		public Object visit(SFPSymbolType node, Object data) {
			if (!allowed.contains(SlotType.SYMBOL))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.SYMBOL;
			return data;
		}

		@Override
		public Object visit(SFPBooleanType node, Object data) {
			if (!allowed.contains(SlotType.BOOLEAN))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.BOOLEAN;
			return data;
		}

		@Override
		public Object visit(SFPStringType node, Object data) {
			if (!allowed.contains(SlotType.STRING))
				return SFPVisitorImpl.this.visit(node, data);
			this.type = SlotType.STRING;
			return data;
		}

		@Override
		public Object visit(SFPDateTimeType node, Object data) {
			if (!allowed.contains(SlotType.DATETIME))
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
		public Object visit(SFPTypeSpecification node, Object data) {
			if (node.jjtGetNumChildren() != 1)
				throw new IllegalArgumentException(
						"Restriction of template fields to multiple types is not supported at the moment!");
			assert node.jjtGetNumChildren() == 1;
			// TBD LEXEME = STRING | SYMBOL, NUMBER = INTEGER | FLOAT
			this.type =
					sendVisitor(
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
		public Object visit(SFPTypeAttribute node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.slotType =
					sendVisitor(new SFPTypeSpecificationVisitor(), node.jjtGetChild(0), data).type;
			return data;
		}
	}

	class SFPSingleSlotDefinitionVisitor implements SelectiveSFPVisitor {
		Slot slot;

		// <single-slot-definition> ::= ( slot <slot-name> <template-attribute>*)
		// <SLOT> ( Symbol() ( TemplateAttribute() )* )
		@Override
		public Object visit(SFPSingleSlotDefinition node, Object data) {
			assert node.jjtGetNumChildren() > 0;
			if (node.jjtGetNumChildren() != 2)
				throw new IllegalArgumentException(
						"For now, slot definitions consist of a name and a type restriction!");
			final Symbol name =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			final SlotType type =
					sendVisitor(new SFPTemplateAttributeVisitor(), node.jjtGetChild(1), data).slotType;
			this.slot = new Slot(type, name.getImage());
			return data;
		}
	}

	class SFPDeftemplateConstructElementsVisitor implements SelectiveSFPVisitor {
		String comment;
		final LinkedList<Slot> slotDefinitions = new LinkedList<>();

		// <comment> ::= <string>
		@Override
		public Object visit(SFPConstructDescription node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment = sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0), data).string;
			return data;
		};

		// <slot-definition> ::= <single-slot-definition> | <multislot-definition>
		@Override
		public Object visit(SFPSlotDefinition node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			// TBD add support for multislot-definition
			this.slotDefinitions.add(sendVisitor(new SFPSingleSlotDefinitionVisitor(),
					node.jjtGetChild(0), data).slot);
			return data;
		};
	}

	class SFPTermElementsVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		// currently:
		// ConnectedConstraint(): Term()
		// Term(): SingleVariable()
		@Override
		public Object visit(SFPSingleVariable node, Object data) {
			assert node.jjtGetNumChildren() == 0;
			this.symbol = scope.getOrCreate(node.jjtGetValue().toString());
			return data;
		}
	}

	class SFPConnectedConstraintElementsVisitor implements SelectiveSFPVisitor {
		Symbol varName;

		// currently:
		// ConnectedConstraint(): Term()
		// Term(): SingleVariable()
		@Override
		public Object visit(SFPTerm node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.varName =
					sendVisitor(new SFPTermElementsVisitor(), node.jjtGetChild(0), data).symbol;
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
		public Object visit(SFPConnectedConstraint node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.varName =
					sendVisitor(new SFPConnectedConstraintElementsVisitor(), node.jjtGetChild(0),
							data).varName;
			return data;
		}
	}

	@Value
	static class SlotAndVariable {
		Symbol slotName, varName;
	}

	class SFPUnorderedLHSFactBodyElementsVisitor implements SelectiveSFPVisitor {
		SlotAndVariable slotAndVariable;

		// currently:
		// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
		// LHSSlot(): ( Symbol() Constraint )
		// void Constraint(): ConnectedConstraint()
		// ConnectedConstraint(): Term()
		// Term(): SingleVariable()
		@Override
		public Object visit(SFPLHSSlot node, Object data) {
			assert node.jjtGetNumChildren() == 2;
			final Symbol slotName =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			final Symbol varName =
					sendVisitor(new SFPConstraintVisitor(), node.jjtGetChild(1), data).varName;
			this.slotAndVariable = new SlotAndVariable(slotName, varName);
			return data;
		}
	}

	class SFPTemplatePatternCEElementsVisitor implements SelectiveSFPVisitor {
		SlotAndVariable slotAndVariable;

		// currently:
		// TemplatePatternCE ( Symbol() (UnorderedLHSFactBody())+ )
		// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
		@Override
		public Object visit(SFPUnorderedLHSFactBody node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.slotAndVariable =
					sendVisitor(new SFPUnorderedLHSFactBodyElementsVisitor(), node.jjtGetChild(0),
							data).slotAndVariable;
			return data;
		}
	}

	@Value
	static class TemplateAndSlotAndVariable {
		Template template;
		Symbol slotName, varName;
	}

	class SFPConditionalElementVisitor implements SelectiveSFPVisitor {
		PredicateWithArguments predicateWithArguments;
		List<TemplateAndSlotAndVariable> tasavs;

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
		public Object visit(SFPTemplatePatternCE node, Object data) {
			assert node.jjtGetNumChildren() > 1;
			final Symbol templateName =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			// the effect of this node should be the creation of one or more slot variables
			final Template template = SFPVisitorImpl.this.symbolTableTemplates.get(templateName);
			if (null == template) {
				throw new IllegalArgumentException("No template with name " + templateName
						+ " defined yet!");
			}
			tasavs =
					stream(node, 1)
							.map(n -> sendVisitor(new SFPUnorderedLHSFactBodyElementsVisitor(), n,
									data).slotAndVariable)
							.map(sav -> new TemplateAndSlotAndVariable(template, sav.slotName,
									sav.varName)).collect(Collectors.toList());
			return data;
		}

		// <logical-CE> ::= (logical <conditional-element>+)
		// LogicalCE() : <LOGICAL> ( ConditionalElement() )+
		// TBD LogicalCE

		@Override
		public Object visit(SFPAndFunction node, Object data) {
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] predicatesWithArguments =
					stream(node, 0)
							.map(n -> sendVisitor(new SFPConditionalElementVisitor(), n, data).predicateWithArguments)
							.filter(c -> null != c).toArray(PredicateWithArguments[]::new);
			final Predicate and =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.And.inClips,
							SlotType.nCopies(SlotType.BOOLEAN, predicatesWithArguments.length));
			this.predicateWithArguments =
					new PredicateWithArgumentsComposite(and, predicatesWithArguments);
			return data;
		}

		@Override
		public Object visit(SFPOrFunction node, Object data) {
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] predicatesWithArguments =
					stream(node, 0)
							.map(n -> sendVisitor(new SFPConditionalElementVisitor(), n, data).predicateWithArguments)
							.filter(c -> null != c).toArray(PredicateWithArguments[]::new);
			final Predicate or =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.Or.inClips,
							SlotType.nCopies(SlotType.BOOLEAN, predicatesWithArguments.length));
			this.predicateWithArguments =
					new PredicateWithArgumentsComposite(or, predicatesWithArguments);
			return data;
		}

		@Override
		public Object visit(SFPNotFunction node, Object data) {
			// not has multiple meanings: boolean operator, not exists operator
			assert node.jjtGetNumChildren() == 1;
			final SFPConditionalElementVisitor visitor =
					sendVisitor(new SFPConditionalElementVisitor(), node.jjtGetChild(0), data);
			if (null != visitor.predicateWithArguments) {
				// test-ce (not (boolean expression))
				final Predicate not =
						FunctionDictionary.lookupPredicate(
								org.jamocha.filter.impls.predicates.Or.inClips, SlotType.BOOLEAN);
				this.predicateWithArguments =
						new PredicateWithArgumentsComposite(not, visitor.predicateWithArguments);
			} else if (null != visitor.tasavs) {
				// template-ce (not exists)
				// TODO somehow, we need to encode that all Paths resulting from
				// tasavs should be negated existential
				this.tasavs = visitor.tasavs;
			} else {
				throw new UnsupportedOperationException();
			}
			return data;
		}

		// <test-CE> ::= (test <function-call>)
		// TestCE() : <TEST> FunctionCall()
		// TODO TestCE

		// <exists-CE> ::= (exists <conditional-element>+)
		// ExistsCE() : <EXISTS> ( ConditionalElement() )+
		// TBD ExistsCE

		// <forall-CE> ::= (forall <conditional-element> <conditional-element>+)
		// ForallCE() : <FORALL> ConditionalElement() ( LOOKAHEAD(2) ConditionalElement() )+
		// TBD ForallCE

		// <assigned-pattern-CE> ::= <single-field-variable> <- <pattern-CE>
		// AssignedPatternCE(): ( SingleVariable() <ASSIGN> <LBRACE> TemplatePatternCE() <RBRACE> )
		// TODO AssignedPatternCE
	}

	class SFPDefruleConstructElementVisitor extends SFPConditionalElementVisitor {
		String comment;

		// TBD ActionList, Declaration

		// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
		// <conditional-element>* => <expression>*)
		// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
		// ConditionalElement() )* ) <ARROW> ActionList()
		@Override
		public Object visit(SFPConstructDescription node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment = sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0), data).string;
			return data;
		}
	}

	class SFPExpressionVisitor implements SelectiveSFPVisitor {
		@Override
		public Object visit(SFPExpression node, Object data) {
			return data;
		}
	}

	class SFPStartVisitor implements SelectiveSFPVisitor {
		// Start() : Construct() | Expression()
		// void Construct() : <LBRACE> ( DeftemplateConstruct() | DefglobalConstruct()
		// | DefruleConstruct() | DeffunctionConstruct() | DefmoduleConstruct() ) <RBRACE>

		// <comment> ::= <string>

		@Override
		public Object visit(SFPDeftemplateConstruct node, Object data) {
			// <deftemplate-construct> ::= (deftemplate <deftemplate-name> [<comment>]
			// <slot-definition>*)

			// <DEFTEMPLATE> Symbol() [ ConstructDescription() ] ( SlotDefinition() )*
			assert node.jjtGetNumChildren() > 0;
			final Symbol symbol =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
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
		};

		@Override
		public Object visit(SFPDefruleConstruct node, Object data) {
			// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
			// <conditional-element>* => <expression>*)
			// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
			// ConditionalElement() )* ) <ARROW> ActionList()
			assert node.jjtGetNumChildren() > 1;
			SFPVisitorImpl.this.scope.pushScope();
			final Symbol symbol =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			String comment = null;
			final ArrayList<PathFilterElement> pfes = new ArrayList<>();
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				final SFPDefruleConstructElementVisitor visitor =
						new SFPDefruleConstructElementVisitor();
				node.jjtGetChild(i).jjtAccept(visitor, data);
				if (null != visitor.comment) {
					assert null == comment;
					comment = visitor.comment;
				} else if (null != visitor.tasavs) {
					// visitor.tasavs

				} else if (null != visitor.predicateWithArguments) {
					pfes.add(new PathFilterElement(visitor.predicateWithArguments));
				}
			}

			// final Rule rule = new Rule(visitor.slotDefinitions.toArray(new
			// Slot[visitor.slotDefinitions.size()]));
			// SFPVisitorImpl.this.symbolTableRules.put(symbol, this.template);
			SFPVisitorImpl.this.scope.popScope();
			return data;
		};

		@Override
		public Object visit(SFPExpression node, Object data) {
			sendVisitor(new SFPExpressionVisitor(), node, data);
			return data;
		};
	}

	public static void main(String[] args) {
		boolean verbose = (args != null && args.length == 1 && "verbose".equals(args[0]));
		if (!verbose)
			System.out
					.println("Note: For verbose output type \u005c"java Main verbose\u005c".\u005cn");
		System.out.print("SFP> ");
		SFPParser p = new SFPParser(System.in);
		try {
			while (true) {
				SFPStart n = p.Start();
				if (n == null)
					System.exit(0);
				n.dump(" ");
				Object a = n.jjtAccept(new SFPVisitorImpl(), "");
				System.out.println(a);
			}
		} catch (Exception e) {
			System.err.println("ERROR[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
			// if (verbose)
			e.printStackTrace();
		}
	}
}
