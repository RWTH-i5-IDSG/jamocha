/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"){ throw new
 * UnsupportedOperationException(data.toString()); } you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.languages.clips.parser;

import org.jamocha.languages.clips.parser.generated.*;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public interface SelectiveSFPVisitor extends SFPParserVisitor {

	public default Object visit(SimpleNode node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPStart node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFloat node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPInteger node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDateTime node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSymbol node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPString node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTrue node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNil node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSymbolType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPStringType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDateTimeType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLexemeType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPBooleanType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPIntegerType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLongType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPShortType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFloatType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDoubleType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNumberType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFalse node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPConstant node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPConstructDescription node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSingleVariable node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPGlobalVariable node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPMultiVariable node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPVariableType node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAnyFunction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPExpression node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAssertFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPModify node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFindFactByFactFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRetractFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPIfElseFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPWhileFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLoopForCountFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSwitchCaseFunc node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPCaseStatement node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSwitchDefaults node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDeftemplateConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSilent node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSlotDefinition node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSingleSlotDefinition node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPMultiSlotDefinition node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAttributes node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefaultAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefaultAttributes node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDeriveAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNoneAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDynamicAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPModifyPattern node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRHSSlot node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRHSPattern node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefruleConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPActionList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDeclaration node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTemporalValidityDeclaration node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTemporalValidity node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAMillisecond node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTASecond node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAMinute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAHour node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTADay node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAMonth node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAYear node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTAWeekday node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTADuration node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSalience node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAutoFocus node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSlowCompile node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRuleVersion node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNotFunction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAndFunction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPOrFunction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAssignedPatternCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLogicalCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTestCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPExistsCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPForallCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTemplatePatternCE node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPUnorderedLHSFactBody node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPOrderedLHSFactBody node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLHSSlot node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSingleFieldWildcard node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPMultiFieldWildcard node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPConnectedConstraint node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAmpersandConnectedConstraint node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLineConnectedConstraint node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTerm node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNegation node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPColon node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPEquals node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefglobalConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPGlobalAssignment node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDeffunctionConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFunctionGroup node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefgenericConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPParameterRestriction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPWildcardParameterRestriction node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPQuery node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTypeAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPTypeSpecification node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPAllowedConstantAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPSymbolList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPStringList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPLexemeList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPIntegerList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPFloatList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPNumberList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPValueList node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRangeAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPRangeSpecification node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPCardinalityAttribute node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPCardinalitySpecification node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

	public default Object visit(SFPDefmoduleConstruct node, Object data) {
		throw new UnsupportedOperationException(data.toString());
	}

}
