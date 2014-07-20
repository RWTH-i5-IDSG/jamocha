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
	public default Object visit(SimpleNode node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPStart node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFloat node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPInteger node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDateTime node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSymbol node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPString node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTrue node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNil node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSymbolType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPStringType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDateTimeType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLexemeType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPBooleanType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPIntegerType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFloatType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNumberType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFactAddressType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFalse node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPConstant node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPConstructDescription node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSingleVariable node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPGlobalVariable node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPMultiVariable node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPVariableType node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAnyFunction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPExpression node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAssertFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPModify node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFindFactByFactFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRetractFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPIfElseFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPWhileFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLoopForCountFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSwitchCaseFunc node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPCaseStatement node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSwitchDefaults node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDeftemplateConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSilent node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSlotDefinition node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSingleSlotDefinition node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPMultiSlotDefinition node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAttributes node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefaultAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefaultAttributes node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDeriveAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNoneAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDynamicAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRHSSlot node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRHSPattern node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefruleConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPActionList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDeclaration node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTemporalValidityDeclaration node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTemporalValidity node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAMillisecond node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTASecond node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAMinute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAHour node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTADay node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAMonth node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAYear node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTAWeekday node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTADuration node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSalience node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAutoFocus node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSlowCompile node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRuleVersion node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNotFunction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAndFunction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPOrFunction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAssignedPatternCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLogicalCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTestCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPExistsCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPForallCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTemplatePatternCE node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPUnorderedLHSFactBody node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPOrderedLHSFactBody node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLHSSlot node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSingleFieldWildcard node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPMultiFieldWildcard node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPConnectedConstraint node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAmpersandConnectedConstraint node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLineConnectedConstraint node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTerm node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNegation node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPColon node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPEquals node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefglobalConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPGlobalAssignment node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDeffunctionConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFunctionGroup node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefgenericConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPParameterRestriction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPWildcardParameterRestriction node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPQuery node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTypeAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPTypeSpecification node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPAllowedConstantAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPSymbolList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPStringList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPLexemeList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPIntegerList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPFloatList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPNumberList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPValueList node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRangeAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPRangeSpecification node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPCardinalityAttribute node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPCardinalitySpecification node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}

	@Override
	public default Object visit(SFPDefmoduleConstruct node, Object data) {
		dumpToStdOut(node);
		throw new UnsupportedOperationException(String.valueOf(data));
	}
}
