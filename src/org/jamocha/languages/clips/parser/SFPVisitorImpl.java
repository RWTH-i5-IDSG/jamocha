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

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.jamocha.util.ToArray.toArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ParserToNetwork;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.filter.SymbolCollector;
import org.jamocha.function.fwa.Assert;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.GenericWithArgumentsComposite;
import org.jamocha.function.fwa.GlobalVariableLeaf;
import org.jamocha.function.fwa.Modify;
import org.jamocha.function.fwa.PredicateWithArguments;
import org.jamocha.function.fwa.Retract;
import org.jamocha.function.fwa.SymbolLeaf;
import org.jamocha.function.impls.predicates.Equals;
import org.jamocha.languages.clips.parser.ExistentialStack.ScopedExistentialStack;
import org.jamocha.languages.clips.parser.errors.ClipsNameClashError;
import org.jamocha.languages.clips.parser.errors.ClipsNoSlotForThatNameError;
import org.jamocha.languages.clips.parser.errors.ClipsSideEffectsDisallowedHereError;
import org.jamocha.languages.clips.parser.errors.ClipsTemplateNotDefinedError;
import org.jamocha.languages.clips.parser.errors.ClipsTypeMismatchError;
import org.jamocha.languages.clips.parser.errors.ClipsVariableNotDeclaredError;
import org.jamocha.languages.clips.parser.generated.Node;
import org.jamocha.languages.clips.parser.generated.SFPActionList;
import org.jamocha.languages.clips.parser.generated.SFPAllowedConstantAttribute;
import org.jamocha.languages.clips.parser.generated.SFPAmpersandConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPAndFunction;
import org.jamocha.languages.clips.parser.generated.SFPAnyFunction;
import org.jamocha.languages.clips.parser.generated.SFPAssertFunc;
import org.jamocha.languages.clips.parser.generated.SFPAssignedPatternCE;
import org.jamocha.languages.clips.parser.generated.SFPAttributes;
import org.jamocha.languages.clips.parser.generated.SFPBooleanType;
import org.jamocha.languages.clips.parser.generated.SFPCardinalityAttribute;
import org.jamocha.languages.clips.parser.generated.SFPColon;
import org.jamocha.languages.clips.parser.generated.SFPConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPConstant;
import org.jamocha.languages.clips.parser.generated.SFPConstructDescription;
import org.jamocha.languages.clips.parser.generated.SFPDateTime;
import org.jamocha.languages.clips.parser.generated.SFPDateTimeType;
import org.jamocha.languages.clips.parser.generated.SFPDeclaration;
import org.jamocha.languages.clips.parser.generated.SFPDefaultAttribute;
import org.jamocha.languages.clips.parser.generated.SFPDefaultAttributes;
import org.jamocha.languages.clips.parser.generated.SFPDeffunctionConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDefglobalConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDefruleBody;
import org.jamocha.languages.clips.parser.generated.SFPDefruleConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDefrulesConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDeftemplateConstruct;
import org.jamocha.languages.clips.parser.generated.SFPDeriveAttribute;
import org.jamocha.languages.clips.parser.generated.SFPDynamicAttribute;
import org.jamocha.languages.clips.parser.generated.SFPEquals;
import org.jamocha.languages.clips.parser.generated.SFPEqualsFunction;
import org.jamocha.languages.clips.parser.generated.SFPExistsCE;
import org.jamocha.languages.clips.parser.generated.SFPExpression;
import org.jamocha.languages.clips.parser.generated.SFPFactAddressType;
import org.jamocha.languages.clips.parser.generated.SFPFalse;
import org.jamocha.languages.clips.parser.generated.SFPFindFactByFactFunc;
import org.jamocha.languages.clips.parser.generated.SFPFloat;
import org.jamocha.languages.clips.parser.generated.SFPFloatList;
import org.jamocha.languages.clips.parser.generated.SFPFloatType;
import org.jamocha.languages.clips.parser.generated.SFPForallCE;
import org.jamocha.languages.clips.parser.generated.SFPGlobalAssignment;
import org.jamocha.languages.clips.parser.generated.SFPGlobalVariable;
import org.jamocha.languages.clips.parser.generated.SFPIfElseFunc;
import org.jamocha.languages.clips.parser.generated.SFPInteger;
import org.jamocha.languages.clips.parser.generated.SFPIntegerList;
import org.jamocha.languages.clips.parser.generated.SFPIntegerType;
import org.jamocha.languages.clips.parser.generated.SFPLHSSlot;
import org.jamocha.languages.clips.parser.generated.SFPLineConnectedConstraint;
import org.jamocha.languages.clips.parser.generated.SFPLoopForCountFunc;
import org.jamocha.languages.clips.parser.generated.SFPModify;
import org.jamocha.languages.clips.parser.generated.SFPNegation;
import org.jamocha.languages.clips.parser.generated.SFPNil;
import org.jamocha.languages.clips.parser.generated.SFPNoneAttribute;
import org.jamocha.languages.clips.parser.generated.SFPNotFunction;
import org.jamocha.languages.clips.parser.generated.SFPOrFunction;
import org.jamocha.languages.clips.parser.generated.SFPOrderedLHSFactBody;
import org.jamocha.languages.clips.parser.generated.SFPParserTreeConstants;
import org.jamocha.languages.clips.parser.generated.SFPRHSPattern;
import org.jamocha.languages.clips.parser.generated.SFPRHSSlot;
import org.jamocha.languages.clips.parser.generated.SFPRangeAttribute;
import org.jamocha.languages.clips.parser.generated.SFPRangeSpecification;
import org.jamocha.languages.clips.parser.generated.SFPRetractFunc;
import org.jamocha.languages.clips.parser.generated.SFPSalience;
import org.jamocha.languages.clips.parser.generated.SFPSingleSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPSingleVariable;
import org.jamocha.languages.clips.parser.generated.SFPSlotDefinition;
import org.jamocha.languages.clips.parser.generated.SFPStart;
import org.jamocha.languages.clips.parser.generated.SFPString;
import org.jamocha.languages.clips.parser.generated.SFPStringList;
import org.jamocha.languages.clips.parser.generated.SFPStringType;
import org.jamocha.languages.clips.parser.generated.SFPSwitchCaseFunc;
import org.jamocha.languages.clips.parser.generated.SFPSymbol;
import org.jamocha.languages.clips.parser.generated.SFPSymbolList;
import org.jamocha.languages.clips.parser.generated.SFPSymbolType;
import org.jamocha.languages.clips.parser.generated.SFPTemplatePatternCE;
import org.jamocha.languages.clips.parser.generated.SFPTerm;
import org.jamocha.languages.clips.parser.generated.SFPTestCE;
import org.jamocha.languages.clips.parser.generated.SFPTrue;
import org.jamocha.languages.clips.parser.generated.SFPTypeAttribute;
import org.jamocha.languages.clips.parser.generated.SFPTypeSpecification;
import org.jamocha.languages.clips.parser.generated.SFPUnorderedLHSFactBody;
import org.jamocha.languages.clips.parser.generated.SFPVariableType;
import org.jamocha.languages.clips.parser.generated.SFPWhileFunc;
import org.jamocha.languages.clips.parser.generated.SimpleNode;
import org.jamocha.languages.common.AssertTemplateContainerBuilder;
import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TemplatePatternConditionalElement;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.RuleCondition;
import org.jamocha.languages.common.ScopeCloser;
import org.jamocha.languages.common.ScopeStack.Symbol;
import org.jamocha.languages.common.ScopeStack.VariableSymbol;
import org.jamocha.languages.common.SingleFactVariable;
import org.jamocha.languages.common.SlotBuilder;
import org.jamocha.languages.common.Warning;

/**
 * This class is final to prevent accidental derived classes. All inner classes shall share the same
 * outer instance giving them access to its attributes.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Getter
public final class SFPVisitorImpl implements SelectiveSFPVisitor {

	/**
	 * About scopes
	 * <ul>
	 * <li>variables are valid within a single defrule or deffunction</li>
	 * <li>rules, functions, and templates are valid within modules</li>
	 * <li>rules, functions, and template may use the same names</li>
	 * </ul>
	 */
	final Queue<Warning> warnings = new LinkedList<>();

	final ParserToNetwork parserToNetwork;
	final SideEffectFunctionToNetwork sideEffectFunctionToNetwork;

	public SFPVisitorImpl(final ParserToNetwork parserToNetwork,
			final SideEffectFunctionToNetwork sideEffectFunctionToNetwork) {
		this.parserToNetwork = parserToNetwork;
		this.sideEffectFunctionToNetwork = sideEffectFunctionToNetwork;
	}

	@Override
	public Object visit(final SFPStart node, final Object data) {
		assert node.jjtGetNumChildren() == 1;
		return SelectiveSFPVisitor.sendVisitor(new SFPStartVisitor(), node.jjtGetChild(0), data).value;
	}

	@Override
	public Object visit(final SFPRHSPattern node, final Object data) {
		assert node.jjtGetNumChildren() > 0;
		final SymbolToFunctionWithArguments mapper = SymbolToFunctionWithArguments.bySymbol();
		final boolean sideEffectsAllowed = true;
		final Symbol symbol = SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
		final Template template = parserToNetwork.getTemplate(symbol.getImage());
		if (null == template) {
			throw new ClipsTemplateNotDefinedError(symbol, node);
		}
		final AssertTemplateContainerBuilder builder = new AssertTemplateContainerBuilder(template);
		SelectiveSFPVisitor.stream(node, 1).forEach(
				n -> {
					final SFPRHSPatternElementsVisitor visitor =
							SelectiveSFPVisitor.sendVisitor(new SFPRHSPatternElementsVisitor((RuleCondition) null,
									mapper, sideEffectsAllowed), n, data);
					;
					final SlotAddress slotAddress = template.getSlotAddress(visitor.slotName);
					if (null == slotAddress) {
						throw new ClipsNoSlotForThatNameError(symbol.getImage(), node);
					}
					builder.addValue(slotAddress, visitor.value);
				});
		final TemplateContainer<SymbolLeaf> templateContainer = builder.build();
		templateContainer.evaluate();
		return data;
	}

	public static enum ExistentialState {
		NORMAL, EXISTENTIAL, NEGATED;
	}

	final static EnumSet<SlotType> Number = EnumSet.of(SlotType.LONG, SlotType.DOUBLE);
	final static EnumSet<SlotType> Constant = EnumSet.of(SlotType.NIL, SlotType.DATETIME, SlotType.SYMBOL,
			SlotType.STRING, SlotType.LONG, SlotType.DOUBLE, SlotType.BOOLEAN);
	final static EnumSet<SlotType> FactIdentifierTypes = EnumSet.of(SlotType.LONG, SlotType.FACTADDRESS);

	class SFPSymbolVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(final SFPSymbol node, final Object data) {
			this.symbol =
					SFPVisitorImpl.this.parserToNetwork.getScope().getOrCreateSymbol(node.jjtGetValue().toString());
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
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.DOUBLE;
			this.value = Double.parseDouble(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(final SFPInteger node, final Object data) {
			if (!this.allowed.contains(SlotType.LONG))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.LONG;
			this.value = Long.parseLong(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(final SFPSymbol node, final Object data) {
			if (!this.allowed.contains(SlotType.SYMBOL))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.SYMBOL;
			this.value = SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node, data).symbol;
			return data;
		}

		@Override
		public Object visit(final SFPTrue node, final Object data) {
			if (!this.allowed.contains(SlotType.BOOLEAN))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.BOOLEAN;
			this.value = true;
			return data;
		}

		@Override
		public Object visit(final SFPFalse node, final Object data) {
			if (!this.allowed.contains(SlotType.BOOLEAN))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.BOOLEAN;
			this.value = false;
			return data;
		}

		@Override
		public Object visit(final SFPString node, final Object data) {
			if (!this.allowed.contains(SlotType.STRING))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.STRING;
			this.value = SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node, data).string;
			return data;
		}

		@Override
		public Object visit(final SFPDateTime node, final Object data) {
			if (!this.allowed.contains(SlotType.DATETIME))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.DATETIME;
			this.value = SlotType.convert(node.jjtGetValue().toString());
			return data;
		}

		@Override
		public Object visit(final SFPNil node, final Object data) {
			if (!this.allowed.contains(SlotType.NIL))
				throw new ClipsTypeMismatchError(null, node);
			this.type = SlotType.SYMBOL;
			this.value = null;
			return data;
		}

		public ConstantLeaf<SymbolLeaf> toFWA() {
			return new ConstantLeaf<SymbolLeaf>(value, type);
		}

		// unsupported Nil
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

		@Override
		public Object visit(final SFPFactAddressType node, final Object data) {
			if (!this.allowed.contains(SlotType.FACTADDRESS)) {
				return SFPVisitorImpl.this.visit(node, data);
			}
			this.type = SlotType.FACTADDRESS;
			return data;
		}

		// unsupported: LEXEME = STRING | SYMBOL, NUMBER = INTEGER | FLOAT
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
				throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new,
						"Restriction of template fields to multiple types is not supported at the moment!");
			}
			// unsupported: LEXEME = STRING | SYMBOL, NUMBER = INTEGER | FLOAT
			this.type =
					SelectiveSFPVisitor.sendVisitor(
							new SFPTypeVisitor(EnumSet.of(/* SlotType.LEXEME, */SlotType.SYMBOL, SlotType.STRING,
									SlotType.DATETIME, SlotType.LONG, SlotType.DOUBLE, SlotType.BOOLEAN,
									SlotType.FACTADDRESS
							/* , SlotType.NUMBER */)), node.jjtGetChild(0), data).type;
			return data;
		}
		// unsupported VariableType
	}

	@RequiredArgsConstructor
	class SFPTemplateAttributeVisitor implements SelectiveSFPVisitor {
		final RuleCondition rc;
		final SlotBuilder slotBuilder;

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
				throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new,
						"Restriction of template fields to multiple types is not supported at the moment!");
			}
			this.slotBuilder.setType(SelectiveSFPVisitor.sendVisitor(new SFPTypeSpecificationVisitor(),
					node.jjtGetChild(0), data).type);
			return data;
		}

		class SFPAllowedConstantAttributeElementsVisitor implements SelectiveSFPVisitor {
			private Object handle(final SimpleNode node, final Object data, final SlotType type) {
				assert 0 > node.jjtGetNumChildren();
				if (1 == node.jjtGetNumChildren()
						&& SFPParserTreeConstants.JJTVARIABLETYPE == node.jjtGetChild(0).getId()) {
					return data;
				}
				SFPTemplateAttributeVisitor.this.slotBuilder.setAllowedConstaintsConstraint(
						type,
						SelectiveSFPVisitor.stream(node, 0)
								.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(type), n, data).value)
								.collect(toList()));
				return data;
			}

			@Override
			public Object visit(final SFPSymbolList node, final Object data) {
				return handle(node, data, SlotType.SYMBOL);
			}

			@Override
			public Object visit(final SFPStringList node, final Object data) {
				return handle(node, data, SlotType.STRING);
			}

			@Override
			public Object visit(final SFPIntegerList node, final Object data) {
				return handle(node, data, SlotType.LONG);
			}

			@Override
			public Object visit(final SFPFloatList node, final Object data) {
				return handle(node, data, SlotType.DOUBLE);
			}
		}

		// AllowedConstantAttribute() : (SymbolList() | StringList() | LexemeList() | IntegerList()
		// | FloatList() | NumberList() | ValueList() )
		// SymbolList() : ( ( <SYMBOL> )+ | VariableType() )
		// StringList() : ( ( <STRING> )+ | VariableType() )
		// ...
		// ValueList() : ( ( Constant() )+ | VariableType() )
		@Override
		public Object visit(final SFPAllowedConstantAttribute node, final Object data) {
			assert 1 == node.jjtGetNumChildren();
			SelectiveSFPVisitor
					.sendVisitor(new SFPAllowedConstantAttributeElementsVisitor(), node.jjtGetChild(0), data);
			return data;
		}

		class SFPRangeAttributeElementsVisitor implements SelectiveSFPVisitor {
			ConstantLeaf<SymbolLeaf> number;

			@Override
			public Object visit(final SFPRangeSpecification node, final Object data) {
				this.number =
						SelectiveSFPVisitor.sendVisitor(new SFPRangeSpecificationElementsVisitor(),
								node.jjtGetChild(0), data).number;
				return data;
			}
		}

		class SFPRangeSpecificationElementsVisitor implements SelectiveSFPVisitor {
			ConstantLeaf<SymbolLeaf> number;

			@Override
			public Object visit(final SFPFloat node, final Object data) {
				this.number = SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data).toFWA();
				return data;
			}

			@Override
			public Object visit(final SFPInteger node, final Object data) {
				this.number = SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data).toFWA();
				return data;
			}

			@Override
			public Object visit(final SFPVariableType node, final Object data) {
				this.number = null;
				return data;
			}
		}

		// RangeAttribute() : ( <RANGE> RangeSpecification() RangeSpecification() )
		// RangeSpecification() : ( Number() | VariableType() )
		@Override
		public Object visit(final SFPRangeAttribute node, final Object data) {
			assert 2 == node.jjtGetNumChildren();
			final ConstantLeaf<SymbolLeaf> from =
					SelectiveSFPVisitor.sendVisitor(new SFPRangeAttributeElementsVisitor(), node.jjtGetChild(0), data).number;
			final ConstantLeaf<SymbolLeaf> to =
					SelectiveSFPVisitor.sendVisitor(new SFPRangeAttributeElementsVisitor(), node.jjtGetChild(1), data).number;
			SFPTemplateAttributeVisitor.this.slotBuilder.setRangeConstraint(from, to);
			return data;
		}

		// CardinalityAttribute() : <CARDINALITY> CardinalitySpecification()
		// CardinalitySpecification() )
		// CardinalitySpecification() : ( Integer() | VariableType() )
		@Override
		public Object visit(final SFPCardinalityAttribute node, final Object data) {
			// TBD cardinality
			return SelectiveSFPVisitor.super.visit(node, data);
		}

		@RequiredArgsConstructor
		class SFPAttributesVisitor implements SelectiveSFPVisitor {
			final Consumer<FunctionWithArguments<SymbolLeaf>> defaultConsumer;

			@Override
			public Object visit(final SFPAttributes node, final Object data) {
				if (1 != node.jjtGetNumChildren()) {
					throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new,
							"multi slots are not supported at the moment!");
				}
				defaultConsumer.accept(SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(rc,
						SymbolToFunctionWithArguments.bySymbol(), false), node.jjtGetChild(0), data).expression);
				return data;
			}
		}

		class SFPDefaultAttributeElementsVisitor implements SelectiveSFPVisitor {

			@Override
			public Object visit(final SFPDefaultAttributes node, final Object data) {
				assert 1 == node.jjtGetNumChildren();
				SelectiveSFPVisitor.sendVisitor(new SFPAttributesVisitor(
						SFPTemplateAttributeVisitor.this.slotBuilder::setStaticDefault), node.jjtGetChild(0), data);
				return data;
			}

			@Override
			public Object visit(final SFPDeriveAttribute node, final Object data) {
				assert 0 == node.jjtGetNumChildren();
				SFPTemplateAttributeVisitor.this.slotBuilder.setDeriveDefault();
				return data;
			}

			@Override
			public Object visit(final SFPNoneAttribute node, final Object data) {
				assert 0 == node.jjtGetNumChildren();
				SFPTemplateAttributeVisitor.this.slotBuilder.setNoDefault();
				return data;
			}
		}

		// DefaultAttribute() : <DEFAULT_ATR> ( DeriveAttribute() | NoneAttribute() |
		// DefaultAttributes() )
		// DefaultAttributes() : Attributes()
		// Attributes() : ( Expression() )*
		// DeriveAttribute() : <ATR_DERIVE>
		// NoneAttribute() : <ATR_NONE>
		@Override
		public Object visit(final SFPDefaultAttribute node, final Object data) {
			assert 1 == node.jjtGetNumChildren();
			SelectiveSFPVisitor.sendVisitor(new SFPDefaultAttributeElementsVisitor(), node.jjtGetChild(0), data);
			return data;
		}

		// default-dynamic
		// DynamicAttribute() : <DYNAMIC_ATR> ( Attributes() )
		@Override
		public Object visit(final SFPDynamicAttribute node, final Object data) {
			assert 1 == node.jjtGetNumChildren();
			SelectiveSFPVisitor.sendVisitor(new SFPAttributesVisitor(
					SFPTemplateAttributeVisitor.this.slotBuilder::setDynamicDefault), node.jjtGetChild(0), data);
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPSingleSlotDefinitionVisitor implements SelectiveSFPVisitor {
		final RuleCondition rc;
		Slot slot;

		// <single-slot-definition> ::= ( slot <slot-name> <template-attribute>*)
		// <SLOT> ( Symbol() ( TemplateAttribute() )* )
		@Override
		public Object visit(final SFPSingleSlotDefinition node, final Object data) {
			final Symbol name =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			final SlotBuilder slotBuilder = new SlotBuilder(name.getImage());
			SelectiveSFPVisitor.stream(node, 1).forEach(
					n -> SelectiveSFPVisitor.sendVisitor(new SFPTemplateAttributeVisitor(rc, slotBuilder), n, data));
			this.slot = slotBuilder.build(SFPVisitorImpl.this.parserToNetwork.getDefaultValues());
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPDeftemplateConstructElementsVisitor implements SelectiveSFPVisitor {
		final RuleCondition rc;
		String comment;
		final LinkedList<Slot> slotDefinitions = new LinkedList<>();

		// <comment> ::= <string>
		@Override
		public Object visit(final SFPConstructDescription node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment = SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0), data).string;
			return data;
		}

		// <slot-definition> ::= <single-slot-definition> | <multislot-definition>
		@Override
		public Object visit(final SFPSlotDefinition node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			// unsupported: multislot-definition
			this.slotDefinitions.add(SelectiveSFPVisitor.sendVisitor(new SFPSingleSlotDefinitionVisitor(rc),
					node.jjtGetChild(0), data).slot);
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPSingleVariableVisitor implements SelectiveSFPVisitor {
		final RuleCondition rc;
		VariableSymbol symbol;

		@Override
		public Object visit(final SFPSingleVariable node, final Object data) {
			assert node.jjtGetNumChildren() == 0;
			this.symbol =
					SFPVisitorImpl.this.parserToNetwork.getScope().getOrCreateVariableSymbol(
							node.jjtGetValue().toString(), rc);
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

	@FunctionalInterface
	static interface SFPConstraintVisitorSupplier<T extends SelectiveSFPVisitor> {
		T create(final SFPConditionalElementVisitor parent, final Consumer<ConditionalElement> constraintAdder,
				final Template template, final SlotAddress slot, final boolean bindingsAllowed,
				final Optional<VariableSymbol> constraintVariable);
	}

	static final Consumer<Object> nullConsumer = (x) -> {
	};

	@RequiredArgsConstructor
	class SFPConditionalElementVisitor implements SelectiveSFPVisitor {

		@AllArgsConstructor
		class SFPTermElementsVisitor implements SelectiveSFPVisitor {
			final SFPConditionalElementVisitor parent;
			final Consumer<ConditionalElement> constraintAdder;
			final Template template;
			final SlotAddress slot;
			final boolean negated;
			final boolean bindingsAllowed;
			Optional<VariableSymbol> constraintVariable;

			// Term(): ( [ Negation() ] ( Constant() | SingleVariable() | MultiVariable() | Colon()
			// | Equals() )

			private VariableSymbol createConstraintVariable() {
				if (!constraintVariable.isPresent()) {
					// create dummy variable
					constraintVariable =
							Optional.of(SFPVisitorImpl.this.parserToNetwork.getScope().createDummySlotVariable(
									parent.factVariable, slot, parent.contextStack, nullConsumer));
				}
				return constraintVariable.get();
			}

			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				final VariableSymbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(parent.contextStack), node, data).symbol;
				if (negated) {
					createConstraintVariable().getEqual().addNegatedEdge(symbol.getEqual());
				} else {
					if (bindingsAllowed) {
						parent.factVariable.newSingleSlotVariable(slot, symbol);
					} else {
						final VariableSymbol csv = createConstraintVariable();
						constraintAdder.accept(new TestConditionalElement(GenericWithArgumentsComposite
								.newPredicateInstance(Equals.inClips, new SymbolLeaf(csv), new SymbolLeaf(symbol))));
					}
				}
				return data;
			}

			private ConditionalElement negate(final ConditionalElement child) {
				return (negated ? new NotFunctionConditionalElement(Arrays.asList(child)) : child);
			}

			@Override
			public Object visit(final SFPConstant node, final Object data) {
				assert 1 == node.jjtGetNumChildren();
				final SFPValueVisitor constantVisitor =
						SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node.jjtGetChild(0), data);
				final VariableSymbol csv = createConstraintVariable();
				// create equals test
				constraintAdder.accept(negate(new TestConditionalElement(GenericWithArgumentsComposite
						.newPredicateInstance(Equals.inClips, new SymbolLeaf(csv), new ConstantLeaf<>(
								constantVisitor.value, constantVisitor.type)))));
				return data;
			}

			@Override
			public Object visit(final SFPColon node, final Object data) {
				// predicate constraint
				assert 1 == node.jjtGetNumChildren();
				final FunctionWithArguments<SymbolLeaf> functionCall =
						SelectiveSFPVisitor.sendVisitor(new SFPFunctionCallElementsVisitor(parent.contextStack,
								SymbolToFunctionWithArguments.bySymbol(), false), node.jjtGetChild(0), data).expression;
				assert SlotType.BOOLEAN == functionCall.getReturnType();
				constraintAdder.accept(negate(new TestConditionalElement(
						(PredicateWithArguments<SymbolLeaf>) functionCall)));
				return data;
			}

			@Override
			public Object visit(final SFPEquals node, final Object data) {
				// return-value constraint
				assert 1 == node.jjtGetNumChildren();
				final VariableSymbol csv = createConstraintVariable();
				// get function call following the = sign
				final FunctionWithArguments<SymbolLeaf> functionCall =
						SelectiveSFPVisitor.sendVisitor(new SFPFunctionCallElementsVisitor(parent.contextStack,
								SymbolToFunctionWithArguments.bySymbol(), false), node.jjtGetChild(0), data).expression;
				// create equals test
				constraintAdder.accept(negate(new TestConditionalElement(GenericWithArgumentsComposite
						.newPredicateInstance(Equals.inClips, new SymbolLeaf(csv), functionCall))));
				return data;
			}
		}

		class SFPTermVisitor extends SFPConstraintBase {
			public SFPTermVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final boolean bindingsAllowed,
					final Optional<VariableSymbol> constraintVariable) {
				super(parent, constraintAdder, template, slot, bindingsAllowed, constraintVariable);
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
						SelectiveSFPVisitor.sendVisitor(new SFPTermElementsVisitor(parent, constraintAdder, template,
								slot, negated, bindingsAllowed, constraintVariable), node.jjtGetChild(negated ? 1 : 0),
								data).constraintVariable;
				return data;
			}
		}

		class SFPAmpersandConnectedConstraintVisitor extends SFPConstraintBase {
			public SFPAmpersandConnectedConstraintVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final boolean bindingsAllowed,
					final Optional<VariableSymbol> constraintVariable) {
				super(parent, constraintAdder, template, slot, bindingsAllowed, constraintVariable);
			}

			// LineConnectedConstraint(): ( AmpersandConnectedConstraint() ( <LINE>
			// AmpersandConnectedConstraint() )* )

			// AmpersandConnectedConstraint(): ( Term() ( <AMPERSAND> Term() )* )
			@Override
			public Object visit(final SFPAmpersandConnectedConstraint node, final Object data) {
				return handleConnectedConstraint(node, data,
						new SFPConstraintVisitorSupplier<SFPVisitorImpl.SFPConditionalElementVisitor.SFPTermVisitor>() {
							@Override
							public SFPTermVisitor create(final SFPConditionalElementVisitor parent,
									final Consumer<ConditionalElement> constraintAdder, final Template template,
									final SlotAddress slot, final boolean bindingsAllowed,
									final Optional<VariableSymbol> constraintVariable) {
								return new SFPTermVisitor(parent, constraintAdder, template, slot, bindingsAllowed,
										constraintVariable);
							}
						}, AndFunctionConditionalElement::new);
			}
		}

		class SFPConnectedConstraintElementsVisitor extends SFPConstraintBase {
			public SFPConnectedConstraintElementsVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final boolean bindingsAllowed,
					final Optional<VariableSymbol> constraintVariable) {
				super(parent, constraintAdder, template, slot, bindingsAllowed, constraintVariable);
			}

			// ConnectedConstraint(): ( ( SingleVariable() <AMPERSAND> LineConnectedConstraint() ) |
			// SingleVariable() | LineConnectedConstraint() )

			// LineConnectedConstraint(): ( AmpersandConnectedConstraint() ( <LINE>
			// AmpersandConnectedConstraint() )* )

			@Override
			public Object visit(final SFPLineConnectedConstraint node, final Object data) {
				return handleConnectedConstraint(
						node,
						data,
						new SFPConstraintVisitorSupplier<SFPVisitorImpl.SFPConditionalElementVisitor.SFPAmpersandConnectedConstraintVisitor>() {
							@Override
							public SFPAmpersandConnectedConstraintVisitor create(
									final SFPConditionalElementVisitor parent,
									final Consumer<ConditionalElement> constraintAdder, final Template template,
									final SlotAddress slot, final boolean bindingsAllowed,
									final Optional<VariableSymbol> constraintVariable) {
								return new SFPAmpersandConnectedConstraintVisitor(parent, constraintAdder, template,
										slot, bindingsAllowed && node.jjtGetNumChildren() == 1, constraintVariable);
							}
						}, OrFunctionConditionalElement::new);
			}

			@Override
			public Object visit(final SFPSingleVariable node, final Object data) {
				final VariableSymbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(parent.contextStack), node, data).symbol;
				parent.factVariable.newSingleSlotVariable(slot, symbol);
				this.constraintVariable = Optional.of(symbol);
				return data;
			}
		}

		class SFPConnectedConstraintVisitor extends SFPConstraintBase {
			public SFPConnectedConstraintVisitor(final SFPConditionalElementVisitor parent,
					final Consumer<ConditionalElement> constraintAdder, final Template template,
					final SlotAddress slot, final boolean bindingsAllowed) {
				super(parent, constraintAdder, template, slot, bindingsAllowed, Optional.empty());
			}

			// ConnectedConstraint(): ( ( SingleVariable() <AMPERSAND> LineConnectedConstraint() ) |
			// SingleVariable() | LineConnectedConstraint() )
			@Override
			public Object visit(final SFPConnectedConstraint node, final Object data) {
				final int numChildren = node.jjtGetNumChildren();
				assert Arrays.asList(1, 2).contains(numChildren);
				SelectiveSFPVisitor.stream(node, 0)
						.forEach(
								n -> this.constraintVariable =
										SelectiveSFPVisitor.sendVisitor(new SFPConnectedConstraintElementsVisitor(
												parent, constraintAdder, template, slot, bindingsAllowed,
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
			final boolean bindingsAllowed;
			Optional<VariableSymbol> constraintVariable;

			Object handleConnectedConstraint(final SimpleNode node, final Object data,
					final SFPConstraintVisitorSupplier<? extends SFPConstraintBase> visitorSupplier,
					final java.util.function.Function<List<ConditionalElement>, ConditionalElement> connector) {
				assert node.jjtGetNumChildren() > 0;
				final boolean terminal = 1 == node.jjtGetNumChildren();
				if (terminal) {
					this.constraintVariable =
							SelectiveSFPVisitor.sendVisitor(visitorSupplier.create(parent, constraintAdder, template,
									slot, bindingsAllowed, constraintVariable), node.jjtGetChild(0), data).constraintVariable;
				} else {
					final ArrayList<ConditionalElement> constraints = new ArrayList<>();
					final SFPConstraintBase visitor =
							visitorSupplier.create(parent, constraints::add, template, slot, bindingsAllowed,
									constraintVariable);
					SelectiveSFPVisitor.stream(node, 0).forEach(n -> SelectiveSFPVisitor.sendVisitor(visitor, n, data));
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
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
				SelectiveSFPVisitor.sendVisitor(new SFPConnectedConstraintVisitor(parent, constraintAdder, template,
						template.getSlotAddress(slotName.getImage()), true), node.jjtGetChild(1), data);
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
				SelectiveSFPVisitor.sendVisitor(new SFPUnorderedLHSFactBodyElementsVisitor(parent, constraintAdder,
						template), node.jjtGetChild(0), data);
				return data;
			}

			@Override
			public Object visit(final SFPOrderedLHSFactBody node, final Object data) {
				if (0 == node.jjtGetNumChildren()) {
					// empty constraint list recognized as OrderedLHSFactBody by compiler, ignore
					return data;
				}
				return SelectiveSFPVisitor.super.visit(node, data);
			}
		}

		final ExistentialStack contextStack;
		final VariableSymbol possibleFactVariable;
		boolean containsTemplateCE;
		SingleFactVariable factVariable = null;
		@NonNull
		Optional<ConditionalElement> resultCE = Optional.empty();

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
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			// the effect of this node should be the creation of one or more slot variables
			final Template template = parserToNetwork.getTemplate(templateName.getImage());
			if (null == template) {
				throw SelectiveSFPVisitor.dumpAndThrowMe(node, IllegalArgumentException::new, "No template with name "
						+ templateName + " defined yet!");
			}
			contextStack.mark();
			// if we have the job to determine the type of a fact variable, publish the template now
			if (null != possibleFactVariable) {
				this.factVariable = new SingleFactVariable(template, possibleFactVariable);
			} else {
				parserToNetwork.getScope().createDummyFactVariable(template, contextStack,
						(final SingleFactVariable fv) -> {
							this.factVariable = fv;
						});
			}
			final ConditionalElement templCE =
					parserToNetwork.getInitialFactTemplate() == template ? new InitialFactConditionalElement(
							this.factVariable) : new TemplatePatternConditionalElement(this.factVariable);
			final ArrayList<ConditionalElement> constraints = new ArrayList<>();
			constraints.add(templCE);
			SelectiveSFPVisitor.stream(node, 1).forEach(
					n -> SelectiveSFPVisitor.sendVisitor(new SFPTemplatePatternCEElementsVisitor(this,
							constraints::add, template), n, data));
			if (constraints.size() == 1) {
				this.resultCE = Optional.of(templCE);
			} else {
				assert parserToNetwork.getInitialFactTemplate() != template;
				this.resultCE = Optional.of(new AndFunctionConditionalElement(constraints));
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
			assert node.jjtGetNumChildren() > 0;
			final List<ConditionalElement> elements =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(contextStack,
									null), n, data).resultCE).filter(Optional::isPresent).map(Optional::get)
							.collect(Collectors.toList());
			final int size = elements.size();
			if (size == 1) {
				this.resultCE = Optional.of(elements.get(0));
			} else if (size > 1) {
				this.resultCE = Optional.of(new AndFunctionConditionalElement(elements));
			}
			return data;
		}

		// <or-CE> ::= (or <conditional-element>+)
		// OrFunction(): <OR> (ConditionalElement())+
		@Override
		public Object visit(final SFPOrFunction node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			final List<ConditionalElement> elements =
					SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(contextStack,
									(VariableSymbol) null), n, data).resultCE).filter(Optional::isPresent)
							.map(Optional::get).collect(Collectors.toList());
			final int size = elements.size();
			if (size == 1) {
				this.resultCE = Optional.of(elements.get(0));
			} else if (size > 1) {
				this.resultCE = Optional.of(new OrFunctionConditionalElement(elements));
			}
			return data;
		}

		// <not-CE> ::= (not <conditional-element>)
		// NotFunction(): <NOT> ConditionalElement()
		@Override
		public Object visit(final SFPNotFunction node, final Object data) {
			// not has multiple meanings: boolean operator, not exists operator
			assert node.jjtGetNumChildren() == 1;
			try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.parserToNetwork.getScope());
					final ScopedExistentialStack scopedExistentialStack =
							new ScopedExistentialStack(contextStack, this, ExistentialState.NEGATED)) {
				final SFPConditionalElementVisitor visitor =
						SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(contextStack,
								(VariableSymbol) null), node.jjtGetChild(0), data);
				if (this.containsTemplateCE) {
					this.resultCE =
							Optional.of(new NegatedExistentialConditionalElement(Stream.of(visitor.resultCE)
									.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList())));
				} else {
					this.resultCE =
							Optional.of(new NotFunctionConditionalElement(Arrays.asList(visitor.resultCE.get())));
				}
			}
			return data;
		}

		// <test-CE> ::= (test <function-call>)
		// TestCE() : <TEST> FunctionCall()
		@Override
		public Object visit(final SFPTestCE node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			final FunctionWithArguments<SymbolLeaf> functionCall =
					SelectiveSFPVisitor.sendVisitor(new SFPFunctionCallElementsVisitor(contextStack,
							SymbolToFunctionWithArguments.bySymbol(), false), node.jjtGetChild(0), data).expression;
			this.resultCE = Optional.of(new TestConditionalElement((PredicateWithArguments<SymbolLeaf>) functionCall));
			return data;
		}

		// <exists-CE> ::= (exists <conditional-element>+)
		// ExistsCE() : <EXISTS> ( ConditionalElement() )+
		@Override
		public Object visit(final SFPExistsCE node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.parserToNetwork.getScope());
					final ScopedExistentialStack scopedExistentialStack =
							new ScopedExistentialStack(contextStack, this, ExistentialState.EXISTENTIAL)) {
				final List<ConditionalElement> elements =
						SelectiveSFPVisitor
								.stream(node, 0)
								.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(
										contextStack, null), n, data).resultCE).filter(Optional::isPresent)
								.map(Optional::get).collect(Collectors.toList());
				assert this.containsTemplateCE;
				this.resultCE = Optional.of(new ExistentialConditionalElement(elements));
			}
			return data;
		}

		// <forall-CE> ::= (forall <conditional-element> <conditional-element>+)
		// ForallCE() : <FORALL> ConditionalElement() ( LOOKAHEAD(2) ConditionalElement() )+
		@Override
		public Object visit(final SFPForallCE child, final Object data) {
			final SimpleNode node = (SimpleNode) child.jjtGetParent();
			// find index of child in parent
			// initialize with invalid value
			int i = -1;
			for (int j = 0; j < node.jjtGetNumChildren(); ++j) {
				if (node.jjtGetChild(j) == child) {
					i = j;
					break;
				}
			}
			// verify that child is a child of its parent, thus we found its position
			assert -1 != i;
			final SimpleNode outerNot = new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
			node.jjtAddChild(outerNot, i);
			final SimpleNode outerAnd = new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
			outerNot.jjtAddChild(outerAnd, 0);
			outerAnd.jjtAddChild(child.jjtGetChild(0), 0);
			final SimpleNode innerNot = new SFPNotFunction(SFPParserTreeConstants.JJTNOTFUNCTION);
			outerAnd.jjtAddChild(innerNot, 1);
			if (child.jjtGetNumChildren() > 2) {
				final SimpleNode innerAnd = new SFPAndFunction(SFPParserTreeConstants.JJTANDFUNCTION);
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
			final VariableSymbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(contextStack), node.jjtGetChild(0),
							data).symbol;
			this.resultCE =
					SelectiveSFPVisitor.sendVisitor(new SFPConditionalElementVisitor(contextStack, symbol),
							node.jjtGetChild(1), data).resultCE;
			return data;
		}
	}

	class SFPDefruleConstructElementVisitor extends SFPConditionalElementVisitor {
		@NonNull
		Optional<String> comment = Optional.empty();
		@NonNull
		Optional<ArrayList<FunctionWithArguments<SymbolLeaf>>> actionList = Optional.empty();
		int salience = 0;

		class SFPRulePropertyElementsVisitor implements SelectiveSFPVisitor {
			// SimpleNode Salience() : <SALIENCE> Expression()
			@Override
			public Object visit(final SFPSalience node, final Object data) {
				assert 1 == node.jjtGetNumChildren();
				final FunctionWithArguments<SymbolLeaf> expression =
						SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(contextStack,
								SymbolToFunctionWithArguments.bySymbol(), false), node.jjtGetChild(0), data).expression;
				assert expression.getReturnType() == SlotType.LONG;
				assert Arrays.equals(expression.getParamTypes(), SlotType.empty);
				SFPDefruleConstructElementVisitor.this.salience = ((Long) expression.evaluate()).intValue();
				return data;
			}
		}

		public SFPDefruleConstructElementVisitor(final ExistentialStack contextStack,
				final VariableSymbol possibleFactVariable) {
			super(contextStack, possibleFactVariable);
		}

		// SimpleNode Declaration() : <LBRACE> <DECLARE> ( RuleProperty() )+ <RBRACE>
		// void RuleProperty(): <LBRACE> ( Salience() | AutoFocus() | SlowCompile() | RuleVersion()
		// | TemporalValidityDeclaration() ) <RBRACE>
		@Override
		public Object visit(final SFPDeclaration node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			SelectiveSFPVisitor.stream(node, 0).forEach(
					n -> SelectiveSFPVisitor.sendVisitor(new SFPRulePropertyElementsVisitor(), n, data));
			return data;
		}

		// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
		// <conditional-element>* => <expression>*)
		// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
		// ConditionalElement() )* ) <ARROW> ActionList()
		@Override
		public Object visit(final SFPConstructDescription node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.comment =
					Optional.of(SelectiveSFPVisitor.sendVisitor(new SFPStringVisitor(), node.jjtGetChild(0), data).string);
			return data;
		}

		@Override
		public Object visit(final SFPActionList node, final Object data) {
			this.actionList =
					Optional.of(SelectiveSFPVisitor
							.stream(node, 0)
							.map(n -> SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(contextStack,
									SymbolToFunctionWithArguments.bySymbol(), true), n, data).expression)
							.collect(toCollection(ArrayList::new)));
			return data;
		}
	}

	interface SymbolToFunctionWithArguments {
		FunctionWithArguments<SymbolLeaf> apply(final VariableSymbol symbol, final SimpleNode node)
				throws ClipsVariableNotDeclaredError;

		static SymbolToFunctionWithArguments bySymbol() {
			return (symbol, node) -> {
				return new SymbolLeaf(symbol);
			};
		}
	}

	// <expression> ::= <constant> | <variable> | <function-call>
	// Expression() : ( Constant() | Variable() | FunctionCall() )
	// void Variable() : (SingleVariable() | MultiVariable() | GlobalVariable() )
	// void FunctionCall() : ... see below
	@RequiredArgsConstructor
	class SFPExpressionVisitor implements SelectiveSFPVisitor {
		FunctionWithArguments<SymbolLeaf> expression;
		final RuleCondition rc;
		@NonNull
		final SymbolToFunctionWithArguments mapper;
		final boolean sideEffectsAllowed;

		@Override
		public Object visit(final SFPExpression node, final Object data) {
			assert node.jjtGetNumChildren() == 1;
			this.expression =
					SelectiveSFPVisitor.sendVisitor(new SFPExpressionElementsVisitor(rc, mapper, sideEffectsAllowed),
							node.jjtGetChild(0), data).expression;
			return data;
		}
	}

	class SFPExpressionElementsVisitor extends SFPFunctionCallElementsVisitor {
		SFPExpressionElementsVisitor(final RuleCondition rc, final SymbolToFunctionWithArguments mapper,
				final boolean sideEffectsAllowed) {
			super(rc, mapper, sideEffectsAllowed);
		}

		@Override
		public Object visit(final SFPConstant node, final Object data) {
			final SFPValueVisitor visitor = SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data);
			this.expression = new ConstantLeaf<>(visitor.value, visitor.type);
			return data;
		}

		@Override
		public Object visit(final SFPSingleVariable node, final Object data) {
			final VariableSymbol symbol;
			try {
				symbol = SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(rc), node, data).symbol;
			} catch (final NullPointerException e) {
				throw new ClipsVariableNotDeclaredError(null, node);
			}
			this.expression = this.mapper.apply(symbol, node);
			return data;
		}

		@Override
		public Object visit(final SFPGlobalVariable node, final Object data) {
			this.expression =
					new GlobalVariableLeaf<>(SFPVisitorImpl.this.parserToNetwork.getScope().getGlobalVariable(
							SelectiveSFPVisitor.sendVisitor(new GlobalVariableVisitor(), node, data).symbol));
			return data;
		}
	}

	interface SelectiveFunctionCallVisitor extends SelectiveSFPVisitor {
		Object handleFunctionCall(final SimpleNode node, final Object data);

		@Override
		public default Object visit(final SFPAssertFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPModify node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPRetractFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPFindFactByFactFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPIfElseFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPWhileFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPLoopForCountFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPAnyFunction node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPEqualsFunction node, final Object data) {
			return handleFunctionCall(node, data);
		}

		@Override
		public default Object visit(final SFPSwitchCaseFunc node, final Object data) {
			return handleFunctionCall(node, data);
		}
	}

	// RHSSlot() : <LBRACE> Symbol() ( RHSField() )* <RBRACE>
	// void RHSField() : ( Variable() | Constant() | FunctionCall() )
	// void FunctionCall() : <LBRACE> ( AssertFunc() | Modify() | RetractFunc() |
	// FindFactByFactFunc() | IfElseFunc() | WhileFunc() | LoopForCountFunc() | AnyFunction() |
	// SwitchCaseFunc() ) <RBRACE>
	@RequiredArgsConstructor
	class SFPRHSSlotElementsVisitor implements SelectiveFunctionCallVisitor {
		final RuleCondition rc;
		@NonNull
		final SymbolToFunctionWithArguments mapper;
		final boolean sideEffectsAllowed;
		FunctionWithArguments<SymbolLeaf> value;

		@Override
		public Object visit(final SFPSingleVariable node, final Object data) {
			final VariableSymbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSingleVariableVisitor(rc), node, data).symbol;
			this.value = this.mapper.apply(symbol, node);
			return data;
		}

		@Override
		public Object visit(final SFPGlobalVariable node, final Object data) {
			this.value =
					new GlobalVariableLeaf<>(SFPVisitorImpl.this.parserToNetwork.getScope().getGlobalVariable(
							SelectiveSFPVisitor.sendVisitor(new GlobalVariableVisitor(), node, data).symbol));
			return data;
		}

		@Override
		public Object visit(final SFPConstant node, final Object data) {
			final SFPValueVisitor visitor = SelectiveSFPVisitor.sendVisitor(new SFPValueVisitor(), node, data);
			this.value = new ConstantLeaf<SymbolLeaf>(visitor.value, visitor.type);
			return data;
		}

		@Override
		public Object handleFunctionCall(final SimpleNode node, final Object data) {
			this.value =
					SelectiveSFPVisitor.sendVisitor(new SFPFunctionCallElementsVisitor(rc, this.mapper,
							this.sideEffectsAllowed), node, data).expression;
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPRHSPatternElementsVisitor implements SelectiveSFPVisitor {
		final RuleCondition rc;
		@NonNull
		final SymbolToFunctionWithArguments mapper;
		final boolean sideEffectsAllowed;
		String slotName;
		FunctionWithArguments<SymbolLeaf> value;

		@Override
		public Object visit(final SFPRHSSlot node, final Object data) {
			assert node.jjtGetNumChildren() >= 2;
			if (node.jjtGetNumChildren() > 2) {
				throw new UnsupportedOperationException(
						"You can only specify one value per slot in an assert/modify statement!");
			}
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			this.slotName = symbol.getImage();
			this.value =
					SelectiveSFPVisitor.sendVisitor(new SFPRHSSlotElementsVisitor(rc, this.mapper,
							this.sideEffectsAllowed), node.jjtGetChild(1), data).value;
			return data;
		}
	}

	@RequiredArgsConstructor
	class SFPFunctionCallElementsVisitor implements SelectiveSFPVisitor {
		FunctionWithArguments<SymbolLeaf> expression;
		final RuleCondition rc;
		@NonNull
		final SymbolToFunctionWithArguments mapper;
		final boolean sideEffectsAllowed;

		@RequiredArgsConstructor
		class SFPAssertFuncElementsVisitor implements SelectiveSFPVisitor {
			TemplateContainer<SymbolLeaf> templateContainer;
			@NonNull
			final SymbolToFunctionWithArguments mapper;
			final boolean sideEffectsAllowed;

			@Override
			public Object visit(final SFPRHSPattern node, final Object data) {
				assert node.jjtGetNumChildren() > 0;
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
				final Template template = parserToNetwork.getTemplate(symbol.getImage());
				if (null == template) {
					throw new ClipsTemplateNotDefinedError(symbol, node);
				}
				final AssertTemplateContainerBuilder builder = new AssertTemplateContainerBuilder(template);
				SelectiveSFPVisitor.stream(node, 1).forEach(
						n -> {
							final SFPRHSPatternElementsVisitor visitor =
									SelectiveSFPVisitor.sendVisitor(new SFPRHSPatternElementsVisitor(rc, this.mapper,
											this.sideEffectsAllowed), n, data);
							;
							final SlotAddress slotAddress = template.getSlotAddress(visitor.slotName);
							if (null == slotAddress) {
								throw new ClipsNoSlotForThatNameError(symbol.getImage(), node);
							}
							builder.addValue(slotAddress, visitor.value);
						});
				this.templateContainer = builder.build();
				return data;
			}
		}

		// <function-call> ::= (<function-name> <express)ion>*)
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
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			@SuppressWarnings("unchecked")
			final FunctionWithArguments<SymbolLeaf>[] arguments =
					toArray(SelectiveSFPVisitor.stream(node, 1).map(
							n -> SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(rc, this.mapper,
									this.sideEffectsAllowed), n, data).expression), FunctionWithArguments[]::new);
			this.expression =
					GenericWithArgumentsComposite.newInstance(sideEffectFunctionToNetwork, sideEffectsAllowed,
							symbol.getImage(), arguments);
			return data;
		}

		// EqualsFunction() : ( <EQUALS> ( Expression() )* )
		@Override
		public Object visit(final SFPEqualsFunction node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			@SuppressWarnings("unchecked")
			final FunctionWithArguments<SymbolLeaf>[] arguments =
					toArray(SelectiveSFPVisitor.stream(node, 0).map(
							n -> SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(rc, this.mapper,
									this.sideEffectsAllowed), n, data).expression), FunctionWithArguments[]::new);
			this.expression = GenericWithArgumentsComposite.newPredicateInstance(Equals.inClips, arguments);
			return data;
		}

		// AssertFunc() : <ASSERT> ( RHSPattern() )+
		@Override
		public Object visit(final SFPAssertFunc node, final Object data) {
			assert node.jjtGetNumChildren() > 0;
			if (!sideEffectsAllowed) {
				throw new ClipsSideEffectsDisallowedHereError("assert", node);
			}
			// each child corresponds to one fact assertion
			@SuppressWarnings("unchecked")
			final TemplateContainer<SymbolLeaf>[] templateContainers =
					toArray(SelectiveSFPVisitor.stream(node, 0).map(
							n -> SelectiveSFPVisitor.sendVisitor(new SFPAssertFuncElementsVisitor(this.mapper,
									this.sideEffectsAllowed), n, data).templateContainer), TemplateContainer[]::new);
			this.expression = new Assert<>(sideEffectFunctionToNetwork, templateContainers);
			return data;
		}

		// RetractFunc() : <RETRACT> ((Expression())*)
		// every Expression should evaluate to INTEGER or FACTADDRESS
		@Override
		public Object visit(final SFPRetractFunc node, final Object data) {
			if (!sideEffectsAllowed) {
				throw new ClipsSideEffectsDisallowedHereError("retract", node);
			}
			@SuppressWarnings("unchecked")
			final FunctionWithArguments<SymbolLeaf>[] array =
					toArray(SelectiveSFPVisitor.stream(node, 0).map(
							n -> SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(rc, this.mapper,
									this.sideEffectsAllowed), n, data).expression), FunctionWithArguments[]::new);
			Arrays.stream(array).forEach(fwa -> {
				if (!FactIdentifierTypes.contains(fwa.getReturnType())) {
					throw new ClipsTypeMismatchError(null, node);
				}
			});
			this.expression = new Retract<>(sideEffectFunctionToNetwork, array);
			return data;
		}

		// Modify() : <MODIFY> ModifyPattern()
		// ModifyPattern() : Expression() ( RHSSlot() )+
		// Expression should evaluate to INTEGER or FACTADDRESS
		@Override
		public Object visit(final SFPModify node, final Object data) {
			assert node.jjtGetNumChildren() >= 2;
			if (!sideEffectsAllowed) {
				throw new ClipsSideEffectsDisallowedHereError("modify", node);
			}
			final FunctionWithArguments<SymbolLeaf> target =
					SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor(rc, this.mapper, this.sideEffectsAllowed),
							node.jjtGetChild(0), data).expression;
			@SuppressWarnings("unchecked")
			final Modify.SlotAndValue<SymbolLeaf>[] array =
					toArray(SelectiveSFPVisitor.stream(node, 1).map(
							n -> {
								final SFPRHSPatternElementsVisitor visitor =
										SelectiveSFPVisitor.sendVisitor(new SFPRHSPatternElementsVisitor(rc,
												this.mapper, this.sideEffectsAllowed), n, data);
								return new Modify.SlotAndValue<>(visitor.slotName, visitor.value);
							}), Modify.SlotAndValue[]::new);
			Arrays.stream(array).forEach(fwa -> {
				if (!FactIdentifierTypes.contains(fwa.getValue().getReturnType())) {
					throw new ClipsTypeMismatchError(null, node);
				}
			});
			this.expression = new Modify<>(sideEffectFunctionToNetwork, target, array);
			return data;
		}
	}

	class GlobalVariableVisitor implements SelectiveSFPVisitor {
		Symbol symbol;

		@Override
		public Object visit(final SFPGlobalVariable node, final Object data) {
			this.symbol =
					SFPVisitorImpl.this.parserToNetwork.getScope().getOrCreateSymbol(
							Objects.toString(node.jjtGetValue()));
			return data;
		}
	}

	class SFPStartVisitor implements SelectiveSFPVisitor {
		String value;

		@RequiredArgsConstructor
		class SFPDefruleBodyVisitor implements SelectiveSFPVisitor {
			final List<String> previousRuleNames;
			Defrule defrule;

			@Override
			public Object visit(final SFPDefruleBody node, final Object data) {
				// <defrules-construct> ::= (defrules (<rule-name> [<comment>] [<declaration>]
				// <conditional-element>* => <expression>*)+)
				// DefrulesConstruct() : {(<DEFRULES> (<LBRACE> Symbol() //name
				// [ConstructDescription()
				// ] ([LOOKAHEAD(3) Declaration() ] (ConditionalElement() )* ) <ARROW> ActionList()
				// <RBRACE> )+ )
				assert node.jjtGetNumChildren() > 1;
				final Symbol symbol =
						SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
				if (null != parserToNetwork.getRule(symbol.getImage()) || previousRuleNames.contains(symbol.getImage())) {
					throw new ClipsNameClashError(symbol.getImage(), node, "Rule " + symbol.getImage()
							+ " already defined!");
				}
				try (final ScopeCloser scopeCloser = new ScopeCloser(SFPVisitorImpl.this.parserToNetwork.getScope())) {
					final ExistentialStack existentialStack = new ExistentialStack();
					String comment = null;
					int salience = 0;
					final ArrayList<ConditionalElement> ces = new ArrayList<>();
					ArrayList<FunctionWithArguments<SymbolLeaf>> actionList = null;
					for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
						final SFPDefruleConstructElementVisitor visitor =
								SelectiveSFPVisitor.sendVisitor(new SFPDefruleConstructElementVisitor(existentialStack,
										(VariableSymbol) null), node.jjtGetChild(i), data);
						if (visitor.comment.isPresent()) {
							assert null == comment;
							comment = visitor.comment.get();
						} else if (visitor.resultCE.isPresent()) {
							ces.add(visitor.resultCE.get());
						} else if (visitor.actionList.isPresent()) {
							assert null == actionList;
							actionList = visitor.actionList.get();
						} else if (visitor.salience != 0) {
							salience = visitor.salience;
						}
					}
					if (!existentialStack.templateCEContained) {
						parserToNetwork.getScope().createDummyFactVariable(parserToNetwork.getInitialFactTemplate(),
								existentialStack,
								(final SingleFactVariable fv) -> ces.add(0, new InitialFactConditionalElement(fv)));
					}
					existentialStack.getConditionalElements().addAll(ces);
					this.defrule = new Defrule(symbol.getImage(), comment, salience, existentialStack, actionList);
					new SymbolCollector(existentialStack)
							.getNonDummySymbols()
							.stream()
							.collect(Collectors.groupingBy(Symbol::getImage))
							.entrySet()
							.stream()
							.filter(e -> e.getValue().size() > 1)
							.forEach(
									e -> SFPVisitorImpl.this.warnings.add(new Warning(
											"Two different symbols were created in rule "
													+ symbol.getImage()
													+ " for the same variable name leading to different variables, namely "
													+ e.getKey())));
				}
				return data;
			}
		}

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
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			if (null != parserToNetwork.getTemplate(symbol.getImage())) {
				throw new ClipsNameClashError(symbol.getImage(), node, "Template " + symbol + " already defined!");
			}
			final SFPDeftemplateConstructElementsVisitor visitor = new SFPDeftemplateConstructElementsVisitor(null);
			for (int i = 1; i < node.jjtGetNumChildren(); ++i) {
				node.jjtGetChild(i).jjtAccept(visitor, data);
			}
			final String comment = visitor.comment;
			parserToNetwork.defTemplate(symbol.getImage(), comment, toArray(visitor.slotDefinitions, Slot[]::new));
			return data;
		}

		@Override
		public Object visit(final SFPDefrulesConstruct node, final Object data) {
			// <defrules-construct> ::= (defrules (<rule-name> [<comment>] [<declaration>]
			// <conditional-element>* => <expression>*)+)
			// DefrulesConstruct() : {(<DEFRULES> (<LBRACE> Symbol() //name [ConstructDescription()
			// ] ([LOOKAHEAD(3) Declaration() ] (ConditionalElement() )* ) <ARROW> ActionList()
			// <RBRACE> )+ )
			final int numChildren = node.jjtGetNumChildren();
			assert numChildren >= 1;
			final Defrule[] defrules = new Defrule[numChildren];
			final List<String> previousRuleNames = new ArrayList<>();
			for (int i = 0; i < numChildren; ++i) {
				final Node child = node.jjtGetChild(i);
				final Defrule defrule =
						SelectiveSFPVisitor.sendVisitor(new SFPDefruleBodyVisitor(previousRuleNames), child, data).defrule;
				defrules[i] = defrule;
				previousRuleNames.add(defrule.getName());
			}
			parserToNetwork.defRules(defrules);
			return data;
		}

		@Override
		public Object visit(final SFPDefruleConstruct node, final Object data) {
			// <defrule-construct> ::= (defrule <rule-name> [<comment>] [<declaration>]
			// <conditional-element>* => <expression>*)
			// <DEFRULE> Symbol() [ ConstructDescription() ] ( [ LOOKAHEAD(3) Declaration() ] (
			// ConditionalElement() )* ) <ARROW> ActionList()
			assert node.jjtGetNumChildren() == 1;
			parserToNetwork.defRules(SelectiveSFPVisitor.sendVisitor(
					new SFPDefruleBodyVisitor(Collections.emptyList()), node.jjtGetChild(0), data).defrule);
			return data;
		}

		@Override
		public Object visit(final SFPDeffunctionConstruct node, final Object data) {
			// <deffunction-construct> ::= (deffunction <name> [(functiongroup <groupname>)]
			// [<comment>] (<regular-parameter>* [<wildcard-parameter>]) <expression>*)
			// DeffunctionConstruct(): ( <DEFFUNCTION> Symbol() [ ConstructDescription() ] (<LBRACE>
			// [ FunctionGroup() <RBRACE> <LBRACE> ] ( SingleVariable() )* ( MultiVariable() )*
			// <RBRACE> ) ActionList() )
			assert node.jjtGetNumChildren() > 1;
			final Symbol symbol =
					SelectiveSFPVisitor.sendVisitor(new SFPSymbolVisitor(), node.jjtGetChild(0), data).symbol;
			// handle comment, function group, variable list, action list
			symbol.getImage();
			// SFPVisitorImpl.this.symbolTableFunctions.put(symbol, null);
			// return data;
			// for now: throw
			return SFPVisitorImpl.this.visit(node, data);
		}

		@Override
		public Object visit(final SFPExpression node, final Object data) {
			// interactive mode
			final FunctionWithArguments<SymbolLeaf> expression =
					SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor((RuleCondition) null, (s, n) -> {
						throw new ClipsVariableNotDeclaredError(s, n);
					}, true), node, data).expression;
			this.value =
					sideEffectFunctionToNetwork.getLogFormatter().formatSlotValue(expression.getReturnType(),
							expression.evaluate());
			return data;
		}

		@Override
		public Object visit(final SFPDefglobalConstruct node, final Object data) {
			final SelectiveSFPVisitor visitor = new SelectiveSFPVisitor() {
				@Override
				public Object visit(final SFPGlobalAssignment node, final Object data) {
					// child 0 : global variable
					final Symbol symbol =
							SelectiveSFPVisitor.sendVisitor(new GlobalVariableVisitor(), node.jjtGetChild(0), data).symbol;
					// child 1 : expression
					final FunctionWithArguments<SymbolLeaf> expression =
							SelectiveSFPVisitor.sendVisitor(new SFPExpressionVisitor((RuleCondition) null, (s, n) -> {
								throw new ClipsVariableNotDeclaredError(s, n);
							}, true), node.jjtGetChild(1), data).expression;
					SFPVisitorImpl.this.parserToNetwork.getScope().setOrCreateGlobalVariable(symbol, expression);
					return data;
				}
			};
			SelectiveSFPVisitor.stream(node, 0).forEach(n -> SelectiveSFPVisitor.sendVisitor(visitor, n, data));
			return data;
		}
	}
}
