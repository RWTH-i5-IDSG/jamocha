/* Generated By:JJTree: Do not edit this line. SFPVariableType.java */

package org.jamocha.languages.clips.parser;

public class SFPVariableType extends SimpleNode {
  public SFPVariableType(int id) {
    super(id);
  }

  public SFPVariableType(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}