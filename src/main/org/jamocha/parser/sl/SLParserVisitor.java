/* Generated By:JJTree: Do not edit this line. /Users/charlie/Documents/workspace/Jamocha/src/main/org/jamocha/parser/sl/SLParserVisitor.java */

package org.jamocha.parser.sl;

public interface SLParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(SLContent node, Object data);
  public Object visit(SLContentExpression node, Object data);
  public Object visit(SLWff node, Object data);
  public Object visit(SLNot node, Object data);
  public Object visit(SLAnd node, Object data);
  public Object visit(SLOr node, Object data);
  public Object visit(SLImplies node, Object data);
  public Object visit(SLEquiv node, Object data);
  public Object visit(SLForAll node, Object data);
  public Object visit(SLExists node, Object data);
  public Object visit(SLB node, Object data);
  public Object visit(SLU node, Object data);
  public Object visit(SLPG node, Object data);
  public Object visit(SLI node, Object data);
  public Object visit(SLActionOp node, Object data);
  public Object visit(SLFeasible node, Object data);
  public Object visit(SLDone node, Object data);
  public Object visit(SLAtomicFormula node, Object data);
  public Object visit(SLTrue node, Object data);
  public Object visit(SLFalse node, Object data);
  public Object visit(SLBinaryTermOp node, Object data);
  public Object visit(SLEqual node, Object data);
  public Object visit(SLResult node, Object data);
  public Object visit(SLTermOrIE node, Object data);
  public Object visit(SLTerm node, Object data);
  public Object visit(SLIdentifyingExpression node, Object data);
  public Object visit(SLIota node, Object data);
  public Object visit(SLAny node, Object data);
  public Object visit(SLAll node, Object data);
  public Object visit(SLFunctionalTerm node, Object data);
  public Object visit(SLConstant node, Object data);
  public Object visit(SLNumericalConstant node, Object data);
  public Object visit(SLVariable node, Object data);
  public Object visit(SLActionExpression node, Object data);
  public Object visit(SLAction node, Object data);
  public Object visit(SLPropositionSymbol node, Object data);
  public Object visit(SLPredicateSymbol node, Object data);
  public Object visit(SLFunctionSymbol node, Object data);
  public Object visit(SLAgent node, Object data);
  public Object visit(SLSequence node, Object data);
  public Object visit(SLSet node, Object data);
  public Object visit(SLParameter node, Object data);
  public Object visit(SLParameterValue node, Object data);
  public Object visit(SLParameterName node, Object data);
  public Object visit(SLString node, Object data);
  public Object visit(SLInteger node, Object data);
  public Object visit(SLFloat node, Object data);
  public Object visit(SLDateTime node, Object data);
}
