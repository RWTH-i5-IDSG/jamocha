/* Generated By:JJTree: Do not edit this line. SFPLogicalCE.java */

package org.jamocha.languages.clips.parser;

public class SFPLogicalCE extends SimpleNode {
  public SFPLogicalCE(int id) {
    super(id);
  }

  public SFPLogicalCE(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}