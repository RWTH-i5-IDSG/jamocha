/* Generated By:JavaCC: Do not edit this line. SFPParserDefaultVisitor.java Version 6.1_2 */
package org.jamocha.languages.clips.parser.generated;

public class SFPParserDefaultVisitor implements SFPParserVisitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPStart node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFloat node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPInteger node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDateTime node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSymbol node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPString node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTrue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNil node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSymbolType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPStringType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDateTimeType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLexemeType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPBooleanType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPIntegerType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFloatType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNumberType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFactAddressType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFalse node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPConstant node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPConstructDescription node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSingleVariable node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPGlobalVariable node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPMultiVariable node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPVariableType node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAnyFunction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPExpression node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAssertFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPModify node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFindFactByFactFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRetractFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPIfElseFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPWhileFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLoopForCountFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSwitchCaseFunc node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPCaseStatement node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSwitchDefaults node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDeftemplateConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSilent node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSlotDefinition node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSingleSlotDefinition node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPMultiSlotDefinition node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAttributes node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefaultAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefaultAttributes node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDeriveAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNoneAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDynamicAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRHSSlot node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRHSPattern node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefruleConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefrulesConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefruleBody node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPActionList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDeclaration node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTemporalValidityDeclaration node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTemporalValidity node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAMillisecond node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTASecond node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAMinute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAHour node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTADay node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAMonth node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAYear node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTAWeekday node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTADuration node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSalience node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAutoFocus node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSlowCompile node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRuleVersion node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNotFunction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAndFunction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPOrFunction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAssignedPatternCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLogicalCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTestCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPExistsCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPForallCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTemplatePatternCE node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPUnorderedLHSFactBody node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPOrderedLHSFactBody node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLHSSlot node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSingleFieldWildcard node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPMultiFieldWildcard node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPConnectedConstraint node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAmpersandConnectedConstraint node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLineConnectedConstraint node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTerm node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNegation node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPColon node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPEquals node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefglobalConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPGlobalAssignment node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDeffunctionConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFunctionGroup node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefgenericConstruct node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPParameterRestriction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPWildcardParameterRestriction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPQuery node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTypeAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPTypeSpecification node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPAllowedConstantAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPSymbolList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPStringList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPLexemeList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPIntegerList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPFloatList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPNumberList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPValueList node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRangeAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPRangeSpecification node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPCardinalityAttribute node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPCardinalitySpecification node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(SFPDefmoduleConstruct node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=d5466dd000ebccc7846b99f86a160f58 (do not edit this line) */
