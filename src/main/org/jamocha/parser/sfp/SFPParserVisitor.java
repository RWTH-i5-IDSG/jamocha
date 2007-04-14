/* Generated By:JJTree: Do not edit this line. /Users/amufsuism/Documents/eclipse_workspace/jamocha/src/main/org/jamocha/parser/sfp/SFPParserVisitor.java */

package org.jamocha.parser.sfp;

public interface SFPParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(SFPStart node, Object data);
  public Object visit(SFPFloat node, Object data);
  public Object visit(SFPInteger node, Object data);
  public Object visit(SFPDateTime node, Object data);
  public Object visit(SFPSymbol node, Object data);
  public Object visit(SFPString node, Object data);
  public Object visit(SFPTrue node, Object data);
  public Object visit(SFPSymbolType node, Object data);
  public Object visit(SFPStringType node, Object data);
  public Object visit(SFPDateTimeType node, Object data);
  public Object visit(SFPLexemeType node, Object data);
  public Object visit(SFPBooleanType node, Object data);
  public Object visit(SFPIntegerType node, Object data);
  public Object visit(SFPLongType node, Object data);
  public Object visit(SFPShortType node, Object data);
  public Object visit(SFPFloatType node, Object data);
  public Object visit(SFPDoubleType node, Object data);
  public Object visit(SFPNumberType node, Object data);
  public Object visit(SFPFalse node, Object data);
  public Object visit(SFPConstructDescription node, Object data);
  public Object visit(SFPSingleVariable node, Object data);
  public Object visit(SFPGlobalVariable node, Object data);
  public Object visit(SFPMultiVariable node, Object data);
  public Object visit(SFPVariable node, Object data);
  public Object visit(SFPVariableType node, Object data);
  public Object visit(SFPFunctionCall node, Object data);
  public Object visit(SFPAnyFunction node, Object data);
  public Object visit(SFPExpression node, Object data);
  public Object visit(SFPAssertFunc node, Object data);
  public Object visit(SFPFindFactByFactFunc node, Object data);
  public Object visit(SFPRetractFunc node, Object data);
  public Object visit(SFPIfElseFunc node, Object data);
  public Object visit(SFPWhileFunc node, Object data);
  public Object visit(SFPLoopForCntFunc node, Object data);
  public Object visit(SFPSwitchCaseFunc node, Object data);
  public Object visit(SFPCaseStatement node, Object data);
  public Object visit(SFPSwitchDefaults node, Object data);
  public Object visit(SFPDeftemplateConstruct node, Object data);
  public Object visit(SFPSingleSlotDefinition node, Object data);
  public Object visit(SFPMultiSlotDefinition node, Object data);
  public Object visit(SFPTemplateAttribute node, Object data);
  public Object visit(SFPAttributes node, Object data);
  public Object visit(SFPDefaultAttribute node, Object data);
  public Object visit(SFPDefaultAttributes node, Object data);
  public Object visit(SFPDeriveAttribute node, Object data);
  public Object visit(SFPNoneAttribute node, Object data);
  public Object visit(SFPDynamicAttribute node, Object data);
  public Object visit(SFPTemplateRHSPattern node, Object data);
  public Object visit(SFPRHSSlot node, Object data);
  public Object visit(SFPDefruleConstruct node, Object data);
  public Object visit(SFPActionList node, Object data);
  public Object visit(SFPDeclaration node, Object data);
  public Object visit(SFPRuleProperty node, Object data);
  public Object visit(SFPSalience node, Object data);
  public Object visit(SFPAutoFocus node, Object data);
  public Object visit(SFPRuleVersion node, Object data);
  public Object visit(SFPBooleanSymbol node, Object data);
  public Object visit(SFPConditionalElement node, Object data);
  public Object visit(SFPBooleanFunction node, Object data);
  public Object visit(SFPNotFunction node, Object data);
  public Object visit(SFPAndFunction node, Object data);
  public Object visit(SFPOrFunction node, Object data);
  public Object visit(SFPAssignedPatternCE node, Object data);
  public Object visit(SFPLogicalCE node, Object data);
  public Object visit(SFPTestCE node, Object data);
  public Object visit(SFPExistsCE node, Object data);
  public Object visit(SFPForallCE node, Object data);
  public Object visit(SFPTemplatePatternCE node, Object data);
  public Object visit(SFPAttributeConstraint node, Object data);
  public Object visit(SFPLHSSlot node, Object data);
  public Object visit(SFPSingleFieldWildcard node, Object data);
  public Object visit(SFPMultiFieldWildcard node, Object data);
  public Object visit(SFPConnectedConstraint node, Object data);
  public Object visit(SFPAmpersand node, Object data);
  public Object visit(SFPLine node, Object data);
  public Object visit(SFPTerm node, Object data);
  public Object visit(SFPNegation node, Object data);
  public Object visit(SFPColon node, Object data);
  public Object visit(SFPEquals node, Object data);
  public Object visit(SFPDefglobalConstruct node, Object data);
  public Object visit(SFPGlobalAssignment node, Object data);
  public Object visit(SFPDeffunctionConstruct node, Object data);
  public Object visit(SFPDefgenericConstruct node, Object data);
  public Object visit(SFPParameterRestriction node, Object data);
  public Object visit(SFPWildcardParameterRestriction node, Object data);
  public Object visit(SFPQuery node, Object data);
  public Object visit(SFPTypeAttribute node, Object data);
  public Object visit(SFPTypeSpecification node, Object data);
  public Object visit(SFPAllowedConstantAttribute node, Object data);
  public Object visit(SFPSymbolList node, Object data);
  public Object visit(SFPStringList node, Object data);
  public Object visit(SFPLexemeList node, Object data);
  public Object visit(SFPIntegerList node, Object data);
  public Object visit(SFPFloatList node, Object data);
  public Object visit(SFPNumberList node, Object data);
  public Object visit(SFPValueList node, Object data);
  public Object visit(SFPRangeAttribute node, Object data);
  public Object visit(SFPRangeSpecification node, Object data);
  public Object visit(SFPCardinalityAttribute node, Object data);
  public Object visit(SFPCardinalitySpecification node, Object data);
  public Object visit(SFPDefmoduleConstruct node, Object data);
}
