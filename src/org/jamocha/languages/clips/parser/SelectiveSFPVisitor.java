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

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jamocha.languages.clips.parser.generated.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public interface SelectiveSFPVisitor extends SFPParserVisitor {

	static String prefix = " ";

	public static <V extends SelectiveSFPVisitor, N extends Node> V sendVisitor(final V visitor,
			final N node, final Object data) {
		node.jjtAccept(visitor, data);
		return visitor;
	}

	public static Stream<Node> stream(final Node node, final int startIndex) {
		return IntStream.range(startIndex, node.jjtGetNumChildren()).mapToObj(
				i -> node.jjtGetChild(i));
	}

	public static RuntimeException dumpAndThrowMe(final SimpleNode node,
			final java.util.function.Function<String, RuntimeException> thrower,
			final String exceptionDescription) {
		dumpToStdOut(node);
		return thrower.apply(exceptionDescription);
	}

	public static void dumpToStdOut(final SimpleNode node) {
		System.out.println(dump(node, prefix));
	}

	public static String dump(final SimpleNode node, final String currentPrefix) {
		if (null == node)
			return null;
		final Object nodeValue = node.jjtGetValue();
		final StringBuilder sb = new StringBuilder();
		sb.append(node.toString(currentPrefix));
		if (null != nodeValue) {
			sb.append('[').append(nodeValue.toString()).append(']');
		}
		sb.append("\n");
		sb.append(stream(node, 0).map(n -> dump((SimpleNode) n, currentPrefix + prefix)).collect(
				Collectors.joining()));
		return sb.toString();
	}

	@Override
	public default Object visit(final SimpleNode node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPStart node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFloat node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPInteger node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDateTime node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSymbol node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPString node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTrue node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNil node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSymbolType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPStringType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDateTimeType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLexemeType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPBooleanType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPIntegerType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFloatType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNumberType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFactAddressType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFalse node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPConstant node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPConstructDescription node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSingleVariable node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPGlobalVariable node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPMultiVariable node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPVariableType node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAnyFunction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPEqualsFunction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPExpression node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAssertFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPModify node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFindFactByFactFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRetractFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPIfElseFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPWhileFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLoopForCountFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSwitchCaseFunc node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPCaseStatement node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSwitchDefaults node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDeftemplateConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSilent node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSlotDefinition node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSingleSlotDefinition node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPMultiSlotDefinition node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAttributes node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefaultAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefaultAttributes node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDeriveAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNoneAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDynamicAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRHSSlot node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRHSPattern node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefruleConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefrulesConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefruleBody node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPActionList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDeclaration node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTemporalValidityDeclaration node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTemporalValidity node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAMillisecond node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTASecond node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAMinute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAHour node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTADay node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAMonth node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAYear node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTAWeekday node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTADuration node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSalience node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAutoFocus node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSlowCompile node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRuleVersion node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNotFunction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAndFunction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPOrFunction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAssignedPatternCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLogicalCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTestCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPExistsCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPForallCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTemplatePatternCE node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPUnorderedLHSFactBody node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPOrderedLHSFactBody node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLHSSlot node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSingleFieldWildcard node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPMultiFieldWildcard node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPConnectedConstraint node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAmpersandConnectedConstraint node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLineConnectedConstraint node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTerm node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNegation node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPColon node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPEquals node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefglobalConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPGlobalAssignment node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDeffunctionConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFunctionGroup node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefgenericConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPParameterRestriction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPWildcardParameterRestriction node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPQuery node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTypeAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPTypeSpecification node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPAllowedConstantAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPSymbolList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPStringList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPLexemeList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPIntegerList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPFloatList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPNumberList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPValueList node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRangeAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPRangeSpecification node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPCardinalityAttribute node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPCardinalitySpecification node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(final SFPDefmoduleConstruct node, final Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}
}
