/* Generated By:JJTree: Do not edit this line. SFPMultiFieldWildcard.java */

package org.jamocha.languages.clips.parser;

public class SFPMultiFieldWildcard extends SimpleNode {
  public SFPMultiFieldWildcard(int id) {
    super(id);
  }

  public SFPMultiFieldWildcard(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}