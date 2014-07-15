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

import static java.util.stream.Collectors.toList;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import org.jamocha.dn.Network;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.filter.Function;
import org.jamocha.filter.FunctionDictionary;
import org.jamocha.filter.Path;
import org.jamocha.filter.Predicate;
import org.jamocha.filter.fwa.ConstantLeaf;
import org.jamocha.filter.fwa.PathLeaf;
import org.jamocha.filter.impls.specials.Assert;
import org.jamocha.filter.impls.specials.Assert.TemplateContainer;
import org.jamocha.languages.clips.parser.ExistentialStack.ScopedExistentialStack;
import org.jamocha.languages.clips.parser.errors.ClipsNoSlotForThatNameError;
import org.jamocha.languages.clips.parser.errors.ClipsTemplateNotDefinedError;
import org.jamocha.languages.clips.parser.errors.ClipsVariableNotDeclaredError;
import org.jamocha.languages.clips.parser.generated.SFPActionList;
import org.jamocha.languages.clips.parser.generated.SFPAmpersandConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPAnyFunction;
import org.jamocha.languages.clips.parser.generated.SFPAssertFunc;
import org.jamocha.languages.clips.parser.generated.SFPAssignedPatternCE;
import org.jamocha.languages.clips.parser.generated.SFPBooleanType;
import org.jamocha.languages.clips.parser.generated.SFPColon;
import org.jamocha.languages.clips.parser.generated.SFPConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPConstant;
import org.jamocha.languages.clips.parser.generated.SFPConstructDescription;
import org.jamocha.languages.clips.parser.generated.SFPDateTimeType;
import org.jamocha.languages.clips.parser.generated.SFPDeffunctionConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDefruleConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDeftemplateConstruct;
import org.jamocha.languages.clips.parser.generated.SFPEquals;
import org.jamocha.languages.clips.parser.generated.SFPExistsCE;
import org.jamocha.languages.clips.parser.generated.SFPExpression;
import org.jamocha.languages.clips.parser.generated.SFPFalse;
import org.jamocha.languages.clips.parser.generated.SFPFloat;
import org.jamocha.languages.clips.parser.generated.SFPFloatType;
import org.jamocha.languages.clips.parser.generated.SFPForallCE;
import org.jamocha.languages.clips.parser.generated.SFPInteger;
import org.jamocha.languages.clips.parser.generated.SFPIntegerType;
import org.jamocha.languages.clips.parser.generated.SFPLHSSlot;
import org.jamocha.languages.clips.parser.generated.SFPLineConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPNegation;
import org.jamocha.languages.clips.parser.generated.SFPNotFunction;
import org.jamocha.languages.clips.parser.generated.SFPOrFunction;
import org.jamocha.languages.clips.parser.generated.SFPParser;
import org.jamocha.languages.clips.parser.generated.SFPParserTreeConstants;
import org.jamocha.languages.clips.parser.generated.SFPRHSPattern;
import org.jamocha.languages.clips.parser.generated.SFPRHSSlot;
import org.jamocha.languages.clips.parser.generated.SFPRetractFunc;
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
import org.jamocha.languages.clips.parser.generated.SFPTestCE;
import org.jamocha.languages.clips.parser.generated.SFPTrue;
import org.jamocha.languages.clips.parser.generated.SFPTypeAttribute;
import org.jamocha.languages.clips.parser.generated.SFPTypeSpecification;
import org.jamocha.languages.clips.parser.generated.SFPUnorderedLHSFactBody;
import org.jamocha.languages.clips.parser.generated.SimpleNode;
import org.jamocha.languages.common.AssertTemplateContainerBuilder;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.Constant;
import org.jamocha.languages.common.Expression;
import org.jamocha.languages.common.FunctionCall;
import org.jamocha.languages.common.NameClashError;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeCloser;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.SingleVariable;
import org.jamocha.languages.common.Warning;

/**
 * Needs consideration: how to treat
 * <ul>
 * <li></li>
 * </ul>
 * 
 * This class is final to prevent accidental derived classes. All inner classes shall share the same
 * outer instance giving them access to its attributes.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
@RequiredArgsConstructor
public final class SFPVisitorImpl implements SelectiveSFPVisitor {

	/**
	 * About scopes
	 * <ul>
	 * <li>variables are valid within a single defrule or deffunction</li>
	 * <li>rules, functions, and templates are valid within modules</li>
	 * <li>rules, functions, and template may use the same names</li>
	 * </ul>
	 */
	final ScopeStack scope = new ScopeStack();
	final HashMap<Symbol, Template> symbolTableTemplates = new HashMap<>();
	final HashMap<Symbol, Function<?>> symbolTableFunctions = new HashMap<>();
	final HashMap<Symbol, RuleProperties> symbolTableRules = new HashMap<>();
	final Queue<Warning> warnings = new LinkedList<>();

	final Network network;

	@Override
	public Object visit(final SFPStart node, final Object data) {
		assert node.jjtGetNumChildren() == 1;
		SelectiveSFPVisitor.sendVisitor(new SFPStartVisitor(), node.jjtGetChild(0), data);
		return data;
	}

	public static enum ExistentialState {
		NORMAL, EXISTENTIAL, NEGATED;
	}

	@Value
	public static class RuleProperties {
		final String description;
		final RuleCondition condition;
	}

	final static EnumSet<SlotType> Number = EnumSet.of(SlotType.LONG, SlotType.DOUBLE);
	final static EnumSet<SlotType> Constant = EnumSet.of(SlotType.NIL, SlotType.DATETIME,
			SlotType.SYMBOL, SlotType.STRING, SlotType.LONG, SlotType.DOUBLE, SlotType.BOOLEAN);

	class SFPSymbolVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(final SFPSymbol node, final Object data) {
			this.symbol =
					SFPVisitorImpl.this.scope.getOrCreateSymbol(node.jjtGetValue().toString());
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
		public Object visit(final SFPConstant node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			return node.jjtGetChild(0).jjtAccept(this, data);
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
									SlotType.DOUBLE, SlotType.BOOLEAN
							/* , SlotType.NUMBER */)), node.jjtGetChild(0), data).type;
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

	class SFPSingleVariableVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(final SFPSingleVariable node, final Object data) {
			assert node.jjtGetNumChildren() == 0;
			this.symbol =
					SFPVisitorImpl.this.scope.getOrCreateSymbol(node.jjtGetValue().toString());
			return data;
		}
	}

	static final SelectiveSFPVisitor negationVisitor = new SelectiveSFPVisitor() {
		@Override
		public Object visit(final SFPNegation node, final Object data) {
			assert node.jjtGetNumChildren() == 0;
			return data;
		}
	};
	static final Predicate not = FunctionDictionary.lookupPredicate(
			org.jamocha.filter.impls.predicates.Not.inClips, SlotType.BOOLEAN);
	static final Predicate and = FunctionDictionary.lookupPredicate(
			org.jamocha.filter.impls.predicates.And.inClips, SlotType.BOOLEAN, SlotType.BOOLEAN);
	static final Predicate or = FunctionDictionary.lookupPredicate(
			org.jamocha.filter.impls.predicates.Or.inClips, SlotType.BOOLEAN, SlotType.BOOLEAN);

	@FunctionalInterface
	static interface SFPConstraintVisitorSupplier<T extends SelectiveSFPVisitor> {
		T create(final SFPConditionalElementVisitor parent,
				final Consumer<ConditionalElement> constraintAdder, final Template template,
				final SlotAddress slot, final Optional<SingleVariable> constraintVariable);
	}

	@RequiredArgsConstructor
	class SFPConditionalElementVisitor implements SelectiveSFPVisitor {

		@AllArgsConstructor
		class SFPTermElementsVisitor implements SelectiveSFPVisitor {
			final SFPConditionalElementVisitor parent;
			final Consumer<ConditionalElement> constraintAdder;
			final Template template;
			final SlotAddress slot;
			final boolean negated;
			Optional<SingleVariable> constraintVariable;

			// Term(): ( [ Negation() ] ( Constant() | SingleVariable() | MultiVariable() | Colon()
			// | Equals() )

			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(), node, data).symbol;
				final List<SingleVariable> variablesForSymbol =
						parent.contextStack.getVariablesForSymbol(symbol);
				if (variablesForSymbol.isEmpty()) {
					throw new ClipsVariableNotDeclaredError(symbol, node);
				}
				final SingleVariable singleVariable =
						new SingleVariable(symbol, template, slot, false);
				final SingleVariable boundVariable = variablesForSymbol.get(0);
				// create equals test
				final Predicate equals =
						FunctionDictionary.lookupPredicate(
								org.jamocha.filter.impls.predicates.Equals.inClips,
								template.getSlotType(slot), boundVariable.getType());
				final TestConditionalElement eq =
						new TestConditionalElement(new FunctionCall(equals, Arrays.asList(
								boundVariable, singleVariable)));
				constraintAdder.accept(negate(eq));
				parent.contextStack.addSingleVariable(singleVariable);
				return data;
			}

			private ConditionalElement negate(final ConditionalElement child) {
				return (negated ? new NotFunctionConditionalElement(Arrays.asList(child)) : child);
			}

			@Override
			public Object visit(final SFPConstant node, final Object data) {
				assert 1 == node.jjtGetNumChildren();
				final SFPValueVisitor constantVisitor =
						SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node.jjtGetChild(0),
								data);
				if (!constraintVariable.isPresent()) {
					// create dummy variable
					final Symbol dummy = SFPVisitorImpl.this.scope.createDummy();
					final SingleVariable dummyVar =
							new SingleVariable(dummy, template, slot, false);
					parent.contextStack.addSingleVariable(dummyVar);
					constraintVariable = Optional.of(dummyVar);
				}
				// create equals test
				final Predicate equals =
						FunctionDictionary.lookupPredicate(
								org.jamocha.filter.impls.predicates.Equals.inClips,
								template.getSlotType(slot), constantVisitor.type);
				final TestConditionalElement eq =
						new TestConditionalElement(new FunctionCall(equals, Arrays.asList(
								constraintVariable.get(), new Constant(constantVisitor.type,
										constantVisitor.value))));
				constraintAdder.accept(negate(eq));
				return data;
			}

			@Override
			public Object visit(final SFPColon node, final Object data) {
				// predicate constraint
				assert 1 == node.jjtGetNumChildren();
				final FunctionCall functionCall =
						(FunctionCall) SelectiveSFPVisitor.sendVisitor(
								new SFPFunctionCallElementsVisitor(parent.contextStack),
								node.jjtGetChild(0), data).expression;
				assert SlotType.BOOLEAN == functionCall.getType();
				constraintAdder.accept(negate(new TestConditionalElement(functionCall)));
				return data;
			}

			@Override
			public Object visit(final SFPEquals node, final Object data) {
				// return-value constraint
				assert 1 == node.jjtGetNumChildren();
				if (!constraintVariable.isPresent()) {
					// create dummy variable
					final Symbol dummy = SFPVisitorImpl.this.scope.createDummy();
					final SingleVariable dummyVar =
							new SingleVariable(dummy, template, slot, false);
					parent.contextStack.addSingleVariable(dummyVar);
					constraintVariable = Optional.of(dummyVar);
				}
				// get function call following the = sign
				final FunctionCall functionCall =
						(FunctionCall) SelectiveSFPVisitor.sendVisitor(
								new SFPFunctionCallElementsVisitor(parent.contextStack),
								node.jjtGetChild(0), data).expression;
				// create equals test
				final Predicate equals =
						FunctionDictionary.lookupPredicate(
								org.jamocha.filter.impls.predicates.Equals.inClips,
								template.getSlotType(slot), functionCall.getType());
				final TestConditionalElement eq =
						new TestConditionalElement(new FunctionCall(equals, Arrays.asList(
								constraintVariable.get(), functionCall)));
				constraintAdder.accept(negate(eq));
				return data;
			}
		}

		class SFPTermVisitor extends SFPConstraintBase {
			public SFPTermVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final Optional<SingleVariable> constraintVariable) {
				super(parent, constraintAdder, template, slot, constraintVariable);
			}

			// Term(): ( [ Negation() ] ( Constant() | SingleVariable() | MultiVariable() | Colon()
			// | Equals() )
			@Override
			public Object visit(final SFPTerm node, final Object data) {
				assert Arrays.asList(1, 2).contains(Integer.valueOf(node.jjtGetNumChildren()));
				boolean negated = false;
				if (node.jjtGetNumChildren() == 2) {
					// visit to throw exception in case the child is not a negation
					SelectiveSFPVisitor.sendVisitor(negationVisitor, node.jjtGetChild(0), data);
					negated = true;
				}
				this.constraintVariable =
						SelectiveSFPVisitor.sendVisitor(new SFPTermElementsVisitor(parent,
								constraintAdder, template, slot, negated, constraintVariable), node
								.jjtGetChild(negated ? 1 : 0), data).constraintVariable;
				return data;
			}
		}

		class SFPAmpersandConnectedConstraintVisitor extends SFPConstraintBase {
			public SFPAmpersandConnectedConstraintVisitor(
					final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final Optional<SingleVariable> constraintVariable) {
				super(parent, constraintAdder, template, slot, constraintVariable);
			}

			// LineConnectedConstraint(): ( AmpersandConnectedConstraint() ( <LINE>
			// AmpersandConnectedConstraint() )* )

			// AmpersandConnectedConstraint(): ( Term() ( <AMPERSAND> Term() )* )
			@Override
			public Object visit(final SFPAmpersandConnectedConstraint node, final Object data) {
				return handleConnectedConstraint(node, data, SFPTermVisitor::new,
						AndFunctionConditionalElement::new);
			}
		}

		class SFPConnectedConstraintElementsVisitor extends SFPConstraintBase {
			public SFPConnectedConstraintElementsVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final Optional<SingleVariable> constraintVariable) {
				super(parent, constraintAdder, template, slot, constraintVariable);
			}

			// ConnectedConstraint(): ( ( SingleVariable() <AMPERSAND> LineConnectedConstraint() ) |
			// SingleVariable() | LineConnectedConstraint() )

			// LineConnectedConstraint(): ( AmpersandConnectedConstraint() ( <LINE>
			// AmpersandConnectedConstraint() )* )

			@Override
			public Object visit(final SFPLineConnectedConstraint node, final Object data) {
				return handleConnectedConstraint(node, data,
						SFPAmpersandConnectedConstraintVisitor::new,
						OrFunctionConditionalElement::new);
			}

			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(), node, data).symbol;
				final SingleVariable singleVariable =
						new SingleVariable(symbol, template, slot, false);
				parent.contextStack.addSingleVariable(singleVariable);
				this.constraintVariable = Optional.of(singleVariable);
				return data;
			}
		}

		class SFPConnectedConstraintVisitor extends SFPConstraintBase {
			public SFPConnectedConstraintVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot) {
				super(parent, constraintAdder, template, slot, Optional.empty());
			}

			// ConnectedConstraint(): ( ( SingleVariable() <AMPERSAND> LineConnectedConstraint() ) |
			// SingleVariable() | LineConnectedConstraint() )
			@Override
			public Object visit(final SFPConnectedConstraint node, final Object data) {
				final int numChildren = node.jjtGetNumChildren();
				assert Arrays.asList(1, 2).contains(numChildren);
				SelectiveSFPVisitor.stream(node, 0).forEach(
						n -> this.constraintVariable =
								SelectiveSFPVisitor
										.sendVisitor(new SFPConnectedConstraintElementsVisitor(
												parent, constraintAdder, template, slot,
												constraintVariable), n, data).constraintVariable);
				return data;
			}
		}

		@AllArgsConstructor
		class SFPConstraintBase implements SelectiveSFPVisitor {
			final SFPConditionalElementVisitor parent;
			final Consumer<ConditionalElement> constraintAdder;
			final Template template;
			final SlotAddress slot;
			Optional<SingleVariable> constraintVariable;

			Object handleConnectedConstraint(
					final SimpleNode node,
					final Object data,
					final SFPConstraintVisitorSupplier<? extends SFPConstraintBase> visitorSupplier,
					final java.util.function.Function<List<ConditionalElement>, ConditionalElement> connector) {
				assert node.jjtGetNumChildren() > 0;
				final boolean terminal = 1 == node.jjtGetNumChildren();
				if (terminal) {
					this.constraintVariable =
							SelectiveSFPVisitor.sendVisitor(visitorSupplier.create(parent,
									constraintAdder, template, slot, constraintVariable), node
									.jjtGetChild(0), data).constraintVariable;
				} else {
					final ArrayList<ConditionalElement> constraints = new ArrayList<>();
					final SFPConstraintBase visitor =
							visitorSupplier.create(parent, constraints::add, template, slot,
									constraintVariable);
					SelectiveSFPVisitor.stream(node, 0).forEach(
							n -> SelectiveSFPVisitor.sendVisitor(visitor, n, data));
					constraintAdder.accept(connector.apply(constraints));
					this.constraintVariable = visitor.constraintVariable;
				}
				return data;
			}
		}

		@RequiredArgsConstructor
		class SFPUnorderedLHSFactBodyElementsVisitor implements SelectiveSFPVisitor {
			final SFPConditionalElementVisitor parent;
			final Consumer<ConditionalElement> constraintAdder;
			final Template template;

			// currently:
			// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
			// LHSSlot(): ( Symbol() Constraint )
			// void Constraint(): ConnectedConstraint()
			@Override
			public Object visit(final SFPLHSSlot node, final Object data) {
				assert node.jjtGetNumChildren() == 2;
				final Symbol slotName =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(),
								node.jjtGetChild(0), data).symbol;
				SelectiveSFPVisitor.sendVisitor(new SFPConnectedConstraintVisitor(parent,
						constraintAdder, template, template.getSlotAddress(slotName.getImage())),
						node.jjtGetChild(1), data);
				return data;
			}
		}

		@RequiredArgsConstructor
		class SFPTemplatePatternCEElementsVisitor implements SelectiveSFPVisitor {
			final SFPConditionalElementVisitor parent;
			final Consumer<ConditionalElement> constraintAdder;
			final Template template;

			// currently:
			// TemplatePatternCE ( Symbol() (UnorderedLHSFactBody())+ )
			// UnorderedLHSFactBody(): <LBRACE> LHSSlot() <RBRACE>
			@Override
			public Object visit(final SFPUnorderedLHSFactBody node, final Object data) {
				assert node.jjtGetNumChildren() == 1;
				SelectiveSFPVisitor.sendVisitor(new SFPUnorderedLHSFactBodyElementsVisitor(parent,
						constraintAdder, template), node.jjtGetChild(0), data);
				return data;
			}
		}

		final ExistentialStack contextStack;
		final Symbol possibleFactVariable;
		boolean containsTemplateCE;
		ConditionalElement resultCE;

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
		// ConnectedConstraint(): ( ( SingleVariable() <AMPERSAND> LineConnectedConstraint() ) |
		// SingleVariable() | LineConnectedConstraint() )
		// AmpersandConnectedConstraint(): ( Term() ( <AMPERSAND> Term() )* )
		// LineConnectedConstraint(): ( AmpersandConnectedConstraint() ( <LINE>
		// AmpersandConnectedConstraint() )* )
		// < AMPERSAND: "&" >
		// < LINE: "|" >
		// Term(): ( [ Negation() ] ( LOOKAHEAD(3) Constant() | SingleVariable() | MultiVariable() |
		// Colon() | Equals() ) )
		// Negation(): <TILDE>
		// Colon(): <COLON> FunctionCall()
		// Equals(): <EQUALS> FunctionCall()
		// SingleVariable(): <SINGLEVAR>
		// < SINGLEVAR: ("?" <VARSYMBOL>) >

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
			contextStack.mark();
			// if we have the job to determine the type of a fact variable, create the instance now
			if (null != this.possibleFactVariable) {
				this.contextStack.addSingleVariable(new SingleVariable(possibleFactVariable,
						template, (SlotAddress) null, false));
			}
			final ArrayList<ConditionalElement> constraints = new ArrayList<>();
			SelectiveSFPVisitor.stream(node, 1).forEach(
					n -> SelectiveSFPVisitor.sendVisitor(new SFPTemplatePatternCEElementsVisitor(
							this, constraints::add, template), n, data));
			final int size = constraints.size();
			if (size == 1) {
				this.resultCE = constraints.get(0);
			} else if (size > 1) {
				this.resultCE = new AndFunctionConditionalElement(constraints);
			}
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
			final List<ConditionalElement> elements =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPConditionalElementVisitor(contextStack, null), n, data).resultCE)
							.filter(Objects::nonNull).collect(Collectors.toList());
			final int size = elements.size();
			if (size == 1) {
				this.resultCE = elements.get(0);
			} else if (size > 1) {
				this.resultCE = new AndFunctionConditionalElement(elements);
			}
			return data;
		}

		// <or-CE> ::= (or <conditional-element>+)
		// OrFunction(): <OR> (ConditionalElement())+
		@Override
		public Object visit(final SFPOrFunction node, final Object data) {
			assert node.jjtGetNumChildren() > 1;
			final List<ConditionalElement> elements =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPConditionalElementVisitor(contextStack, (Symbol) null),
									n, data).resultCE).filter(Objects::nonNull)
							.collect(Collectors.toList());
			final int size = elements.size();
			if (size == 1) {
				this.resultCE = elements.get(0);
			} else if (size > 1) {
				this.resultCE = new OrFunctionConditionalElement(elements);
			}
			return data;
		}

		// <not-CE> ::= (not <conditional-element>)
		// NotFunction(): <NOT> ConditionalElement()
		@Override
		public Object visit(final SFPNotFunction node, final Object data) {
			// not has multiple meanings: boolean operator, not exists operator
			assert node.jjtGetNumChildren() == 1;
			final ArrayList<SingleVariable> variables = new ArrayList<SingleVariable>();
			try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.scope);
					final ScopedExistentialStack scopedExistentialStack =
							new ScopedExistentialStack(contextStack, this,
									ExistentialState.NEGATED, variables)) {
				final SFPConditionalElementVisitor visitor =
						SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(
								contextStack, (Symbol) null), node.jjtGetChild(0), data);
				if (this.containsTemplateCE) {
					this.resultCE =
							new NegatedExistentialConditionalElement(Stream.of(visitor.resultCE)
									.filter(Objects::nonNull).collect(Collectors.toList()),
									variables);
				} else {
					assert variables.isEmpty();
					this.resultCE =
							new NotFunctionConditionalElement(Arrays.asList(visitor.resultCE));
				}
			}
			return data;
		}

		// <test-CE> ::= (test <function-call>)
		// TestCE() : <TEST> FunctionCall()
		@Override
		public Object visit(final SFPTestCE node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			final FunctionCall functionCall =
					(FunctionCall) SelectiveSFPVisitor.sendVisitor(
							new SFPFunctionCallElementsVisitor(contextStack), node.jjtGetChild(0),
							data).expression;
			this.resultCE = new TestConditionalElement(functionCall);
			return data;
		}

		// <exists-CE> ::= (exists <conditional-element>+)
		// ExistsCE() : <EXISTS> ( ConditionalElement() )+
		@Override
		public Object visit(final SFPExistsCE node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			final ArrayList<SingleVariable> variables = new ArrayList<SingleVariable>();
			try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.scope);
					final ScopedExistentialStack scopedExistentialStack =
							new ScopedExistentialStack(contextStack, this,
									ExistentialState.EXISTENTIAL, variables)) {
				final List<ConditionalElement> elements =
						SelectiveSFPVisitor
								.stream(node, 0)
								.map(n -> SelectiveSFPVisitor.sendVisitor(
										new SFPConditionalElementVisitor(contextStack, null), n,
										data).resultCE).filter(Objects::nonNull)
								.collect(Collectors.toList());
				assert this.containsTemplateCE;
				this.resultCE = new ExistentialConditionalElement(elements, variables);
			}
			return data;
		}

		// <forall-CE> ::= (forall <conditional-element> <conditional-element>+)
		// ForallCE() : <FORALL> ConditionalElement() ( LOOKAHEAD(2) ConditionalElement() )+
		@Override
		public Object visit(final SFPForallCE child, final Object data) {
			final SimpleNode node = (SimpleNode) child.jjtGetParent();
			int i = -1;
			for (int j = 0; j < node.jjtGetNumChildren(); ++j) {
				if (node.jjtGetChild(j) == child) {
					i = j;
					break;
				}
			}
			assert -1 != i;
			final SimpleNode outerNot = new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
			node.jjtAddChild(outerNot, i);
			final SimpleNode outerAnd = new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
			outerNot.jjtAddChild(outerAnd, 0);
			outerAnd.jjtAddChild(child.jjtGetChild(0), 0);
			final SimpleNode innerNot = new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
			outerAnd.jjtAddChild(innerNot, 1);
			if (child.jjtGetNumChildren() > 2) {
				final SimpleNode innerAnd =
						new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
				innerNot.jjtAddChild(innerAnd, 0);
				for (int j = 1; j < child.jjtGetNumChildren(); ++j) {
					innerAnd.jjtAddChild(child.jjtGetChild(j), j - 1);
				}
			} else {
				innerNot.jjtAddChild(child.jjtGetChild(1), 0);
			}
			return outerNot.jjtAccept(this, data);
		}

		// <assigned-pattern-CE> ::= <single-field-variable> <- <pattern-CE>
		// AssignedPatternCE(): ( SingleVariable() <ASSIGN> <LBRACE> TemplatePatternCE() <RBRACE> )
		@Override
		public Object visit(final SFPAssignedPatternCE node, final Object data) {
			assert node.jjtGetNumChildren() == 2;
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(),
							node.jjtGetChild(0), data).symbol;
			this.resultCE =
					SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(contextStack,
							symbol), node.jjtGetChild(1), data).resultCE;
			return data;
		}
	}

	class SFPDefruleConstructElementVisitor extends SFPConditionalElementVisitor {
		String comment;

		public SFPDefruleConstructElementVisitor(final ExistentialStack contextStack,
				final Symbol possibleFactVariable) {
			super(contextStack, possibleFactVariable);
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

		@Override
		public Object visit(final SFPActionList node, final Object data) {
			// assert node.jjtGetNumChildren() == 1;
			// TODO ActionList
			SFPVisitorImpl.this.warnings.add(new Warning("ActionList ignored!"));
			return data;
		}
	}

	// <expression> ::= <constant> | <variable> | <function-call>
	// Expression() : ( Constant() | Variable() | FunctionCall() )
	// void Variable() : (SingleVariable() | MultiVariable() | GlobalVariable() )
	// void FunctionCall() : ... see below
	@RequiredArgsConstructor
	class SFPExpressionVisitor implements SelectiveSFPVisitor {
		Expression expression;
		final RuleCondition ruleCondition;

		@Override
		public Object visit(final SFPExpression node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.expression =
					SelectiveSFPVisitor.sendVisitor(
							new SFPExpressionElementsVisitor(ruleCondition), node.jjtGetChild(0),
							data).expression;
			return data;
		}
	}

	class SFPExpressionElementsVisitor extends SFPFunctionCallElementsVisitor {
		SFPExpressionElementsVisitor(final RuleCondition ruleCondition) {
			super(ruleCondition);
		}

		@Override
		public Object visit(final SFPConstant node, final Object data) {
			final SFPValueVisitor visitor =
					SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data);
			this.expression = new Constant(visitor.type, visitor.value);
			return data;
		}

		@Override
		public Object visit(final SFPSingleVariable node, final Object data) {
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(), node, data).symbol;
			// TBD this works for rules, needs to be adjusted for everything else
			try {
				this.expression = ruleCondition.getVariablesForSymbol(symbol).get(0);
			} catch (IndexOutOfBoundsException e) {
				throw new ClipsVariableNotDeclaredError(symbol, node);
			}
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPFunctionCallElementsVisitor implements SelectiveSFPVisitor {
		Expression expression;
		final RuleCondition ruleCondition;

		@RequiredArgsConstructor
		class SFPRHSSlotElementsVisitor implements SelectiveSFPVisitor {
			final AssertTemplateContainerBuilder builder;
			final SlotAddress slotAddress;

			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				// TODO think about how to connect variables to paths, where to create the paths,
				// and how that relates to the symbols and single variables here
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(), node, data).symbol;
				builder.addValue(slotAddress, new PathLeaf(new Path(builder.getTemplate()),
						slotAddress));
				return data;
			}

			@Override
			public Object visit(final SFPConstant node, final Object data) {
				final SFPValueVisitor visitor =
						SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data);
				builder.addValue(slotAddress, new ConstantLeaf(visitor.value, visitor.type));
				return data;
			}

			// TBD FunctionCall
		}

		@RequiredArgsConstructor
		class SFPRHSPatternElementsVisitor implements SelectiveSFPVisitor {
			final AssertTemplateContainerBuilder builder;

			@Override
			public Object visit(final SFPRHSSlot node, final Object data) {
				assert node.jjtGetNumChildren() == 2;
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(),
								node.jjtGetChild(0), data).symbol;
				final Template template = builder.getTemplate();
				final SlotAddress slotAddress = template.getSlotAddress(symbol.getImage());
				if (null == slotAddress) {
					throw new ClipsNoSlotForThatNameError(symbol, node);
				}
				SelectiveSFPVisitor.sendVisitor(
						new SFPRHSSlotElementsVisitor(builder, slotAddress), node.jjtGetChild(1),
						data);
				return data;
			}
		}

		class SFPAssertFuncElementsVisitor implements SelectiveSFPVisitor {
			TemplateContainer templateContainer;

			@Override
			public Object visit(final SFPRHSPattern node, final Object data) {
				assert node.jjtGetNumChildren() > 1;
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(),
								node.jjtGetChild(0), data).symbol;
				final Template template = SFPVisitorImpl.this.symbolTableTemplates.get(symbol);
				if (null == template) {
					throw new ClipsTemplateNotDefinedError(symbol, node);
				}
				final AssertTemplateContainerBuilder builder =
						new AssertTemplateContainerBuilder(template);
				SelectiveSFPVisitor.stream(node, 1).forEach(
						n -> SelectiveSFPVisitor.sendVisitor(new SFPRHSPatternElementsVisitor(
								builder), n, data));
				this.templateContainer = builder.build();
				return data;
			}
		}

		// <function-call> ::= (<function-name> <expression>*)
		// void FunctionCall() : <LBRACE> ( AssertFunc() | Modify() | RetractFunc() |
		// FindFactByFactFunc() | IfElseFunc() | WhileFunc() | LoopForCountFunc() |
		// AnyFunction() | SwitchCaseFunc() ) <RBRACE>

		// AnyFunction() : ( Symbol() ( Expression() )* )
		// Expression() : ( Constant() | Variable() | FunctionCall() )
		// AssertFunc() : <ASSERT> ( RHSPattern() )+
		// Modify() : <MODIFY> ModifyPattern()
		// FindFactByFactFunc() : <FIND_FACT_BY_FACT> ( RHSPattern() )
		// RetractFunc() : <RETRACT> ((Expression())*)
		// IfElseFunc() : ( <IF> Expression() <THEN> ActionList() [ <ELSE> ActionList() ] )
		// WhileFunc() : (<WHILE> Expression() [<DO> ] ActionList() )
		// LoopForCountFunc() : (<LOOP_FOR_COUNT> (<LBRACE> (SingleVariable() Expression()
		// [Expression() ] ) <RBRACE> ) [<DO> ] ActionList() )
		// SwitchCaseFunc() : (<SWITCH> Expression() Expression ( LOOKAHEAD(2) CaseStatement()
		// )* [<LBRACE> SwitchDefaults() <RBRACE> ] )
		// CaseStatement() : (<LBRACE> <CASE> Expression() Expression <THEN> ActionList()
		// <RBRACE> )
		// SwitchDefaults() : (<LBRACE> <DEFAULT_ATR> ActionList() <RBRACE> )

		@Override
		public Object visit(final SFPAnyFunction node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			final List<Expression> arguments =
					SelectiveSFPVisitor
							.stream(node, 1)
							.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(
									ruleCondition), n, data).expression)
							.collect(Collectors.toList());
			final SlotType[] argTypes =
					toArray(arguments.stream().map(e -> e.getType()), SlotType[]::new);
			final Function<?> function = FunctionDictionary.lookup(symbol.getImage(), argTypes);
			this.expression = new FunctionCall(function, arguments);
			return data;
		}

		@Override
		public Object visit(final SFPAssertFunc node, final Object data) {
			assert node.jjtGetNumChildren() > 2;
			// each child corresponds to one fact assertion
			final List<TemplateContainer> templateContainers =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(
									new SFPAssertFuncElementsVisitor(), n, data).templateContainer)
							.collect(toList());
			this.expression =
					new FunctionCall(new Assert(network, templateContainers), Arrays.asList());
			return data;
		}

		@Override
		public Object visit(final SFPRetractFunc node, final Object data) {
			// TODO Auto-generated method stub

			return data;
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
			if (SFPVisitorImpl.this.symbolTableTemplates.containsKey(symbol)) {
				throw new NameClashError("Template " + symbol + " already defined!");
			}
			final SFPDeftemplateConstructElementsVisitor visitor =
					new SFPDeftemplateConstructElementsVisitor();
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				node.jjtGetChild(i).jjtAccept(visitor, data);
			}
			final String comment = visitor.comment;
			final Template template =
					SFPVisitorImpl.this.network.getMemoryFactory().newTemplate(comment,
							toArray(visitor.slotDefinitions, Slot[]::new));
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
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			if (SFPVisitorImpl.this.symbolTableRules.containsKey(symbol)) {
				throw new NameClashError("Rule " + symbol + " already defined!");
			}
			try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.scope)) {
				final ExistentialStack existentialStack = new ExistentialStack();
				String comment = null;
				final ArrayList<ConditionalElement> ces = new ArrayList<>();
				for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
					final SFPDefruleConstructElementVisitor visitor =
							SelectiveSFPVisitor.sendVisitor(new SFPDefruleConstructElementVisitor(
									existentialStack, (Symbol) null), node.jjtGetChild(i), data);
					if (null != visitor.comment) {
						assert null == comment;
						comment = visitor.comment;
					} else if (null != visitor.resultCE) {
						ces.add(visitor.resultCE);
					} else {
						// TBD action list
					}
				}
				existentialStack
						.getVariables()
						.keySet()
						.stream()
						.collect(Collectors.groupingBy(Symbol::getImage))
						.entrySet()
						.stream()
						.filter(e -> e.getValue().size() > 1
								&& !e.getKey().equals(ScopeStack.dummySymbolImage))
						.forEach(
								e -> SFPVisitorImpl.this.warnings
										.add(new Warning(
												"Two different symbols were created for the same variable name leading to different variables, namely "
														+ e.getKey())));
				if (!existentialStack.templateCEContained) {
					ces.add(0, new InitialFactConditionalElement());
				}
				existentialStack.addConditionalElements(ces);
				SFPVisitorImpl.this.symbolTableRules.put(symbol, new RuleProperties(comment,
						existentialStack));
			}
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
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0),
							data).symbol;
			// handle comment, function group, variable list, action list
			SFPVisitorImpl.this.symbolTableFunctions.put(symbol, null);
			// return data;
			// for now: throw
			return SFPVisitorImpl.this.visit(node, data);
		}

		@Override
		public Object visit(final SFPExpression node, final Object data) {
			SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor((RuleCondition) null), node,
					data);
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
		final Network network = new Network();
		final SFPVisitorImpl visitor = new SFPVisitorImpl(network);
		try {
			while (true) {
				final SFPStart n = p.Start();
				if (n == null)
					System.exit(0);
				if (verbose)
					SelectiveSFPVisitor.dumpToStdOut(n);
				final Object a = n.jjtAccept(visitor, "Parsing successful!");
				System.out.println(a);
				visitor.warnings.forEach(w -> System.out.println("Warning: " + w.getMessage()));
				visitor.warnings.clear();
				System.out.print("SFP> ");
			}
		} catch (final Throwable e) {
			System.err.println("ERROR[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
			if (verbose)
				e.printStackTrace();
		}
	}
}
