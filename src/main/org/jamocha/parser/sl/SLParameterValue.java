/* Generated By:JJTree: Do not edit this line. SLParameterValue.java */

package org.jamocha.parser.sl;

public class SLParameterValue extends SimpleNode {
  public SLParameterValue(int id) {
    super(id);
  }

  public SLParameterValue(SLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
