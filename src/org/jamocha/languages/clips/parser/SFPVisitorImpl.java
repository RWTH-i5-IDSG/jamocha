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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.fwa.PredicateWithArguments;
import org.jamocha.filter.fwa.PredicateWithArgumentsComposite;
import org.jamocha.languages.clips.parser.generated.Node;
import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPBooleanType;
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
import org.jamocha.languages.clips.parser.generated.SFPNotFunction;
import org.jamocha.languages.clips.parser.generated.SFPOrFunction;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPSingleSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.clips.parser.generated.SFPString;
import org.jamocha.languages.clips.parser.generated.SFPStringType;
import org.jamocha.languages.clips.parser.generated.SFPSymbol;
import org.jamocha.languages.clips.parser.generated.SFPSymbolType;
import org.jamocha.languages.clips.parser.generated.SFPTemplatePatternCE;
import org.jamocha.languages.clips.parser.generated.SFPTrue;
import org.jamocha.languages.clips.parser.generated.SFPTypeAttribute;
import org.jamocha.languages.clips.parser.generated.SFPTypeSpecification;
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

	class ConditionalElement {
	}

	class SFPConditionalElementVisitor implements SelectiveSFPVisitor {
		PredicateWithArguments conditionalElement;

		// <conditional-element> ::= <pattern-CE> | <assigned-pattern-CE> | <not-CE> | <and-CE> |
		// <or-CE> | <logical-CE> | <test-CE> | <exists-CE> | <forall-CE>

		// void ConditionalElement() ( ( <LBRACE> ( TemplatePatternCE()| BooleanFunction() |
		// LogicalCE() | TestCE() | ExistsCE() | ForallCE() ) <RBRACE> ) | AssignedPatternCE() )

		// <template-pattern-CE> ::= (<deftemplate-name> <LHS-slot>*)
		// TemplatePatternCE(): ( Symbol() ( (UnorderedLHSFactBody())+ | OrderedLHSFactBody() ) )
		@Override
		public Object visit(SFPTemplatePatternCE node, Object data) {
			// TBD TemplatePatternCE
			final Symbol symbol =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			final Template template = SFPVisitorImpl.this.symbolTableTemplates.get(symbol);
			final int slotIndex = template.getIndexByName("");

			return data;
		}

		// <logical-CE> ::= (logical <conditional-element>+)
		// LogicalCE() : <LOGICAL> ( ConditionalElement() )+
		// TBD LogicalCE

		@Override
		public Object visit(SFPAndFunction node, Object data) {
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] conditionalElements =
					stream(node, 0)
							.map(n -> sendVisitor(new SFPConditionalElementVisitor(), n, data).conditionalElement)
							.filter(c -> null != c).toArray(PredicateWithArguments[]::new);
			final Predicate and =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.And.inClips,
							SlotType.nCopies(SlotType.BOOLEAN, conditionalElements.length));
			this.conditionalElement = new PredicateWithArgumentsComposite(and, conditionalElements);
			return data;
		}

		@Override
		public Object visit(SFPOrFunction node, Object data) {
			assert node.jjtGetNumChildren() > 1;
			final PredicateWithArguments[] conditionalElements =
					stream(node, 0)
							.map(n -> sendVisitor(new SFPConditionalElementVisitor(), n, data).conditionalElement)
							.filter(c -> null != c).toArray(PredicateWithArguments[]::new);
			final Predicate or =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.Or.inClips,
							SlotType.nCopies(SlotType.BOOLEAN, conditionalElements.length));
			this.conditionalElement = new PredicateWithArgumentsComposite(or, conditionalElements);
			return data;
		}

		@Override
		public Object visit(SFPNotFunction node, Object data) {
			assert node.jjtGetNumChildren() == 1;
			final PredicateWithArguments conditionalElement =
					sendVisitor(new SFPConditionalElementVisitor(), node.jjtGetChild(0), data).conditionalElement;
			final Predicate not =
					FunctionDictionary.lookupPredicate(
							org.jamocha.filter.impls.predicates.Or.inClips, SlotType.BOOLEAN);
			this.conditionalElement = new PredicateWithArgumentsComposite(not, conditionalElement);
			return data;
		}

		// <test-CE> ::= (test <function-call>)
		// TestCE() : <TEST> FunctionCall()
		// TBD TestCE

		// <exists-CE> ::= (exists <conditional-element>+)
		// ExistsCE() : <EXISTS> ( ConditionalElement() )+
		// TBD ExistsCE

		// <forall-CE> ::= (forall <conditional-element> <conditional-element>+)
		// ForallCE() : <FORALL> ConditionalElement() ( LOOKAHEAD(2) ConditionalElement() )+
		// TBD ForallCE

		// <assigned-pattern-CE> ::= <single-field-variable> <- <pattern-CE>
		// AssignedPatternCE(): ( SingleVariable() <ASSIGN> <LBRACE> TemplatePatternCE() <RBRACE> )
		// TBD AssignedPatternCE
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
			final Symbol symbol =
					sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			final SFPDefruleConstructElementVisitor visitor =
					new SFPDefruleConstructElementVisitor();
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				node.jjtGetChild(i).jjtAccept(visitor, data);
			}
			final String comment = visitor.comment;
			// final Rule rule = new Rule(visitor.slotDefinitions.toArray(new
			// Slot[visitor.slotDefinitions.size()]));
			// SFPVisitorImpl.this.symbolTableRules.put(symbol, this.template);

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
