/* Generated By:JJTree: Do not edit this line. /Users/alex/Documents/workspace/Jamocha/src/main/org/jamocha/parser/slp/SLParserVisitor.java */

package org.jamocha.parser.slp;

public interface SLParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(SLContent node, Object data);
  public Object visit(SLContentExpression node, Object data);
  public Object visit(SLProposition node, Object data);
  public Object visit(SLWff node, Object data);
  public Object visit(SLIdentifyingExpression node, Object data);
  public Object visit(SLFunctionalTermWithTermOrIE node, Object data);
  public Object visit(SLFunctionalTermWithParameter node, Object data);
  public Object visit(SLActionExpression node, Object data);
  public Object visit(SLAgent node, Object data);
  public Object visit(SLSetOrSequence node, Object data);
  public Object visit(SLParameter node, Object data);
  public Object visit(SLUnaryLogicalOp node, Object data);
  public Object visit(SLBinaryLogicalOp node, Object data);
  public Object visit(SLBinaryTermOp node, Object data);
  public Object visit(SLQuantifier node, Object data);
  public Object visit(SLModalOp node, Object data);
  public Object visit(SLActionOp node, Object data);
  public Object visit(SLReferentialOp node, Object data);
  public Object visit(SLPropositionSymbol node, Object data);
  public Object visit(SLPredicateSymbol node, Object data);
  public Object visit(SLFunctionSymbol node, Object data);
  public Object visit(SLBooleanSymbol node, Object data);
  public Object visit(SLString node, Object data);
  public Object visit(SLInteger node, Object data);
  public Object visit(SLFloat node, Object data);
  public Object visit(SLDateTime node, Object data);
  public Object visit(SLVariable node, Object data);
  public Object visit(SLParameterName node, Object data);
}
