/* Generated By:JJTree: Do not edit this line. SFPModify.java */

package org.jamocha.languages.clips.parser;

public class SFPModify extends SimpleNode {
  public SFPModify(int id) {
    super(id);
  }

  public SFPModify(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}